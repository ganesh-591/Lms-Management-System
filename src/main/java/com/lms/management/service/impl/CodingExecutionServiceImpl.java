package com.lms.management.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.CodingExecutionResult;
import com.lms.management.model.CodingTestCase;
import com.lms.management.model.ExamQuestion;
import com.lms.management.model.ExamResponse;
import com.lms.management.model.ProgrammingLanguage;
import com.lms.management.repository.CodingExecutionResultRepository;
import com.lms.management.repository.CodingTestCaseRepository;
import com.lms.management.repository.ExamQuestionRepository;
import com.lms.management.repository.ExamResponseRepository;
import com.lms.management.service.CodingExecutionService;

@Service
@Transactional
public class CodingExecutionServiceImpl implements CodingExecutionService {

    private final ExamResponseRepository examResponseRepository;
    private final CodingTestCaseRepository codingTestCaseRepository;
    private final CodingExecutionResultRepository executionResultRepository;
    private final ExamQuestionRepository examQuestionRepository;

    // Limit max 5 parallel executions
    private final Semaphore executionLimiter = new Semaphore(5);

    public CodingExecutionServiceImpl(
            ExamResponseRepository examResponseRepository,
            CodingTestCaseRepository codingTestCaseRepository,
            CodingExecutionResultRepository executionResultRepository,
            ExamQuestionRepository examQuestionRepository) {

        this.examResponseRepository = examResponseRepository;
        this.codingTestCaseRepository = codingTestCaseRepository;
        this.executionResultRepository = executionResultRepository;
        this.examQuestionRepository = examQuestionRepository;
    }

    @Override
    public void runSubmission(Long responseId) {

        try {
            executionLimiter.acquire();

            ExamResponse response = examResponseRepository.findById(responseId)
                    .orElseThrow(() -> new IllegalStateException("Response not found"));

            if (response.getCodingSubmissionCode() == null ||
                response.getCodingSubmissionCode().isBlank()) {
                throw new IllegalStateException("No code submitted");
            }

            ExamQuestion examQuestion = examQuestionRepository
                    .findById(response.getExamQuestionId())
                    .orElseThrow(() -> new IllegalStateException("ExamQuestion not found"));

            ProgrammingLanguage language =
                    examQuestion.getQuestion().getProgrammingLanguage();

            List<CodingTestCase> testCases =
                    codingTestCaseRepository.findByQuestionId(
                            examQuestion.getQuestionId());

            executionResultRepository.findByResponseId(responseId)
                    .forEach(executionResultRepository::delete);

            int passedCount = 0;

            for (CodingTestCase testCase : testCases) {

                long startTime = System.currentTimeMillis();

                ProcessResult result;
                try {
                    result = executeInDocker(
                            response.getCodingSubmissionCode(),
                            language,
                            testCase.getInputData()
                    );
                } catch (Exception e) {
                    result = new ProcessResult(null, e.getMessage(), -1, false);
                }

                String status;
                boolean passed = false;

                if (result.isTimeout()) {
                    status = "TLE";
                }
                else if (result.getExitCode() != 0) {
                    status = "RE";
                }
                else if (result.getStdout() != null &&
                        result.getStdout().trim()
                                .equals(testCase.getExpectedOutput().trim())) {
                    status = "AC";
                    passed = true;
                }
                else {
                    status = "WA";
                }

                if (passed) passedCount++;

                CodingExecutionResult entity = new CodingExecutionResult();
                entity.setResponseId(responseId);
                entity.setTestCaseId(testCase.getTestCaseId());
                entity.setActualOutput(result.getStdout());
                entity.setErrorMessage(result.getStderr());
                entity.setExecutionStatus(status);
                entity.setPassed(passed);
                entity.setExecutionTimeMs(
                        System.currentTimeMillis() - startTime
                );

                executionResultRepository.save(entity);
            }

            double marks =
                    testCases.isEmpty() ? 0 :
                    ((double) passedCount / testCases.size())
                            * examQuestion.getMarks();

            response.setMarksAwarded(marks);
            response.setEvaluationType("AUTO");
            examResponseRepository.save(response);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Execution interrupted", e);
        }
        finally {
            executionLimiter.release();
        }
    }

    private ProcessResult executeInDocker(
            String code,
            ProgrammingLanguage language,
            String input) throws Exception {

        Path tempDir = Files.createTempDirectory("exec_" + UUID.randomUUID());

        String fileName = getFileName(language);
        Files.writeString(tempDir.resolve(fileName), code);

        String dockerCommand = buildDockerCommand(
                language,
                tempDir.toAbsolutePath().toString(),
                fileName
        );

        ProcessBuilder builder =
                new ProcessBuilder("cmd", "/c", dockerCommand);

        Process process = builder.start();

        try (BufferedWriter writer =
                     new BufferedWriter(
                             new OutputStreamWriter(process.getOutputStream()))) {

            if (input != null && !input.isBlank()) {
                writer.write(input);
                writer.newLine();
                writer.flush();
            }
        }

        boolean finished =
                process.waitFor(3, TimeUnit.SECONDS);

        if (!finished) {
            process.destroyForcibly();
            deleteDirectory(tempDir);
            return new ProcessResult(null, "TIMEOUT", -1, true);
        }

        String stdout = readStream(process.getInputStream());
        String stderr = readStream(process.getErrorStream());
        int exitCode = process.exitValue();

        deleteDirectory(tempDir);

        return new ProcessResult(stdout, stderr, exitCode, false);
    }

    private String readStream(InputStream stream) throws IOException {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString().trim();
    }

    private String getFileName(ProgrammingLanguage language) {
        switch (language) {
            case JAVA: return "Main.java";
            case PYTHON: return "main.py";
            case C: return "main.c";
            case CPP: return "main.cpp";
            default: throw new IllegalStateException("Unsupported language");
        }
    }

    private String buildDockerCommand(
            ProgrammingLanguage language,
            String hostPath,
            String fileName) {

        String base =
                "docker run --rm --memory=256m --cpus=0.5 " +
                "--pids-limit=64 --network=none " +
                "-v \"" + hostPath + "\":/app -w /app ";

        switch (language) {

            case JAVA:
                return base +
                        "eclipse-temurin:17 bash -c \"javac " +
                        fileName + " && java Main\"";

            case PYTHON:
                return base +
                        "python:3.11 bash -c \"python " + fileName + "\"";

            case C:
                return base +
                        "gcc:latest bash -c \"gcc " +
                        fileName + " -o main && ./main\"";

            case CPP:
                return base +
                        "gcc:latest bash -c \"g++ " +
                        fileName + " -o main && ./main\"";

            default:
                throw new IllegalStateException("Unsupported language");
        }
    }

    private void deleteDirectory(Path path) throws IOException {
        Files.walk(path)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(p -> {
                    try { Files.delete(p); }
                    catch (IOException ignored) {}
                });
    }

    private static class ProcessResult {

        private final String stdout;
        private final String stderr;
        private final int exitCode;
        private final boolean timeout;

        public ProcessResult(String stdout,
                             String stderr,
                             int exitCode,
                             boolean timeout) {
            this.stdout = stdout;
            this.stderr = stderr;
            this.exitCode = exitCode;
            this.timeout = timeout;
        }

        public String getStdout() { return stdout; }
        public String getStderr() { return stderr; }
        public int getExitCode() { return exitCode; }
        public boolean isTimeout() { return timeout; }
    }
    @Override
    public List<CodingExecutionResult> getResultsByResponse(Long responseId) {
        return executionResultRepository.findByResponseId(responseId);
    }
}