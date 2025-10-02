package org.example.ticketingdemo.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.example.ticketingdemo.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보를 DB에 저장하는 JPA Entity 클래스
 *
 */

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String userName;

    @Column(unique = true)
    private String email;

    private String password;

    private String role;


    // 일반 생성자
    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.role = "ADMIN";
    }

    public User(String s, String s1) {
        super();
    }

    // 사용자 정보 업데이트
    public void update(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    // Role 업데이트
    public void setRole(String role) {
        this.role = role;
    }
}