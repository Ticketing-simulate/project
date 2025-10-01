package org.example.ticketingdemo.common.enums;

import lombok.Getter;

@Getter
public enum ResponseMessage {
    LOGIN_SUCCESS("로그인에 성공했습니다."),
    LOGOUT_SUCCESS("로그아웃이 완료되었습니다."),
    CREATE_SUCCESS("생성이 완료되었습니다."),
    UPDATE_SUCCESS("수정이 완료되었습니다."),
    DELETE_SUCCESS("삭제가 완료되었습니다.");

    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }
}
