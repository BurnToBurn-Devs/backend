package com.burntoburn.easyshift.dto.schedule.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PageInfoDTO {
    private boolean isLast;

    public static PageInfoDTO fromEntity(boolean isLast){
        return new PageInfoDTO(isLast);
    }
}

