package com.burntoburn.easyshift.dto.leave.req;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveRequestDto {
    private List<LocalDate> dates;
}
