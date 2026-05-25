package com.example.docmate.payload.response;

import com.example.docmate.enums.WeekDay;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorScheduleResponse {
    private String id;
    private DayOfWeek availableDay;
    private boolean available;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate startDate;
    private LocalDate endDate;
    private String appointmentId;
}
