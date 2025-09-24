package org.example.ticketingdemo.common.enums;

import lombok.Getter;

// 각자 Message를 명세서에 따라 등록하시면 됩니다!
//NOTE:  불필요시 삭제 예정
@Getter
public enum ResponseMessage {

    TASK_CREATED("Task가 생성되었습니다."),
    TASK_LIST_INQUIRE("Task 목록을 조회했습니다."),
    TASK_OBJECT_INQUIRE("Task를 조회했습니다."),
    TASK_CONTENT_UPDATED("Task가 수정되었습니다."),
    TASK_STATUS_UPDATED("작업 상태가 업데이트되었습니다."),
    TASK_DELETED("Task가 삭제되었습니다.");

    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }

}
