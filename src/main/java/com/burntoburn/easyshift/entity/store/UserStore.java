package com.burntoburn.easyshift.entity.store;

import com.burntoburn.easyshift.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "user_store") // 테이블명 명시
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder // Lombok Builder 적용
public class UserStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "user_store_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
}
