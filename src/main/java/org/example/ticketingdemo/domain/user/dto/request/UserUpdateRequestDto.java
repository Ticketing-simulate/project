package org.example.ticketingdemo.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 회원 정보 수정 API에서 클라이언트가 전달하는 요청 데이터를 담는 DTO 클래스
 *
 * 연관 클래스:
 * - UserController.update() : 이 DTO를 @RequestBody로 받음
 * - UserService.update() : DTO 데이터 기반으로 User Entity 업데이트 수행
 */

@Getter
public class UserUpdateRequestDto {

    // Service에서 Entity 업데이트 시 사용
    @NotBlank(message = "유저명은 필수 입력값입니다.")
    @Size(max = 4, message = "유저명은 4글자 이내여야 합니다.")
    private String userName;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
}
