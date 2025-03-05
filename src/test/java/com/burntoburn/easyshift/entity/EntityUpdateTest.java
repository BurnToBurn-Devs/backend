package com.burntoburn.easyshift.entity;

import com.burntoburn.easyshift.entity.store.Store;
import com.burntoburn.easyshift.entity.user.User;
import com.burntoburn.easyshift.repository.store.StoreRepository;
import com.burntoburn.easyshift.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.burntoburn.easyshift.entity.user.Role.WORKER;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class EntityUpdateTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StoreRepository storeRepository;

    private Store store;
    private User user;

    @BeforeEach
    void setUp() {
        store = storeRepository.save(Store.builder()
                .storeName("Test Store")
                .storeCode(UUID.randomUUID())
                .build());

        user = userRepository.save(User.builder()
                .email("test@example.com")
                .name("홍길동")
                .phoneNumber("010-1234-5678")
                .role(WORKER)
                .avatarUrl("https://example.com/avatar.png")
                .build());
    }

    @Test
    @Transactional
    @DisplayName("Store 엔티티 수정 시 updatedAt 변경 확인")
    void shouldUpdateUpdatedAtForStore() throws InterruptedException {
        LocalDateTime beforeUpdate = store.getUpdatedAt();
        assertNotNull(beforeUpdate, "❌ updatedAt이 설정되지 않았습니다.");
        System.out.println("Before Update: " + beforeUpdate);

        Thread.sleep(1000);

        store = storeRepository.findById(store.getId()).orElseThrow();
        store.updateStoreName("Updated Store Name");

        storeRepository.saveAndFlush(store);

        LocalDateTime afterUpdate = store.getUpdatedAt();
        assertNotNull(afterUpdate, "❌ updatedAt이 설정되지 않았습니다.");
        assertTrue(afterUpdate.isAfter(beforeUpdate), "❌ updatedAt이 변경되지 않았습니다.");
        System.out.println("After Update: " + afterUpdate);
    }

    @Test
    @Transactional
    @DisplayName("User 엔티티 수정 시 updatedAt 변경 확인")
    void shouldUpdateUpdatedAtForUser() throws InterruptedException {
        // Given
        LocalDateTime beforeUpdate = user.getUpdatedAt();
        assertNotNull(beforeUpdate, "❌ updatedAt이 설정되지 않았습니다.");
        System.out.println("Before Update: " + beforeUpdate);

        Thread.sleep(1000);

        // When (User 이메일 변경 후 저장)
        user = userRepository.findById(user.getId()).orElseThrow();

        user.update("updated@example.com");
        userRepository.saveAndFlush(user); // save()만 호출

        // Then
        LocalDateTime afterUpdate = user.getUpdatedAt();
        assertNotNull(afterUpdate, "❌ updatedAt이 설정되지 않았습니다.");
        assertTrue(afterUpdate.isAfter(beforeUpdate), "❌ updatedAt이 변경되지 않았습니다.");
        System.out.println("After Update: " + beforeUpdate);
    }
}
