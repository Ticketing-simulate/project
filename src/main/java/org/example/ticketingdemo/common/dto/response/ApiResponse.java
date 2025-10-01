package org.example.ticketingdemo.common.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.example.ticketingdemo.common.enums.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final LocalDateTime timestamp;

    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /* ---------- 성공 응답 ---------- */
    public static <T> ApiResponse<T> ofSuccess(ResponseMessage msg, T data) {
        return new ApiResponse<>(true, msg.getMessage(), data);
    }

    public static <T> ApiResponse<T> ofSuccessMessage(ResponseMessage msg) {
        return new ApiResponse<>(true, msg.getMessage(), null);
    }

    /* ---------- 실패 응답 ---------- */
    public static <T> ApiResponse<T> ofError(ResponseMessage msg, T data) {
        return new ApiResponse<>(false, msg.getMessage(), data);
    }

    public static <T> ApiResponse<T> ofErrorMessage(ResponseMessage msg) {
        return new ApiResponse<>(false, msg.getMessage(), null);
    }

    /* ---------- ResponseEntity 편의 메서드 ---------- */
    // 성공
    public static <T> ResponseEntity<ApiResponse<T>> ok(ResponseMessage msg, T data) {
        return ResponseEntity.ok(ofSuccess(msg, data));
    }

    public static ResponseEntity<ApiResponse<Void>> okMessage(ResponseMessage msg) {
        return ResponseEntity.ok(ofSuccessMessage(msg));
    }

    // 실패
    public static <T> ResponseEntity<ApiResponse<T>> error(ResponseMessage msg, T data) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ofError(msg, data));
    }

    public static ResponseEntity<ApiResponse<Void>> errorMessage(ResponseMessage msg) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ofErrorMessage(msg));
    }
}
