package com.lms.management.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentAttendanceStatus {

    private Long studentId;
    private int attendancePercent;
    private boolean eligible;
    private boolean atRiskByPercent;
    private boolean atRiskByAbsence;
    private boolean alertRequired;
    private String alertReason; 
  
}
