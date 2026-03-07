package com.example.docmate.payload.request;

import com.example.docmate.enums.WeekDay;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorScheduleRequest {
    private WeekD
}
