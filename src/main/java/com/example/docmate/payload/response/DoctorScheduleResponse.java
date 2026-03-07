package com.example.docmate.payload.response;

import com.example.docmate.enums.WeekDay;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorScheduleResponse {
    private String id;
    private WeekDay availableDay;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
