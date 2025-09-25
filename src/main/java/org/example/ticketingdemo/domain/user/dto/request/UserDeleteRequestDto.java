package org.example.ticketingdemo.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 탈퇴 API에서 클라이언트가 전달하는 요청 데이터를 담는 DTO 클래스
 *
 * - UserController.delete() : 이 DTO를 @RequestBody로 받음
 * - UserService.deleteById() : 이 DTO의 password를 검증 후 Soft Delete 수행
 */

@Getter
@NoArgsConstructor
public class UserDeleteRequestDto {
    private String password;
}