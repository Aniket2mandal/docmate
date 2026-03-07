package com.example.docmate.payload.request;

import com.example.docmate.enums.WeekDay;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorScheduleRequest {
    private WeekDay availableDay;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String doctorId;
}
