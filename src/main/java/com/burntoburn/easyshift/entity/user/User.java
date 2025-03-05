package com.burntoburn.easyshift.entity.user;

import com.burntoburn.easyshift.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Entity
@Table(name = "users") // user는 일부 DB에서 예약어이므로 users로 사용
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자 (protected)
@AllArgsConstructor // 모든 필드를 포함한 생성자 자동 생성
@Builder // Lombok Builder 적용
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // id는 Builder에서 설정 불가
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    // 사용자 이름 필드
    @Column(nullable = false)
    private String name;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // WORKER, ADMINISTRATOR

    private String avatarUrl;

    public User update(String newEmail) {
        this.email = newEmail;
        return this;
    }

    // 이름 변경하는 메서드
    public void updateName(String newName) {
        this.name = newName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId()) && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(getName(), user.getName()) && Objects.equals(getPhoneNumber(), user.getPhoneNumber()) && getRole() == user.getRole() && Objects.equals(getAvatarUrl(), user.getAvatarUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail(), getName(), getPhoneNumber(), getRole(), getAvatarUrl());
    }
}
