package com.burntoburn.easyshift.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {

    private final boolean success;
    private final T response;
    private final ErrorResponse error;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> fail(ErrorResponse error) {
        return new ApiResponse<>(false, null, error);
    }

    // 삭제 응답의 경우 ApiResponse.success() 에서 , response = null 일 경우 추가
    public static ApiResponse<Void> success() {return new ApiResponse<>(true, null, null);
    }
}
