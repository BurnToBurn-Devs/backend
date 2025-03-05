package com.burntoburn.easyshift.controller;

import com.burntoburn.easyshift.common.response.ApiResponse;
import com.burntoburn.easyshift.dto.store.use.*;
import com.burntoburn.easyshift.service.store.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;


    // ========================================

    /**
     * 매장 생성 API
     */
    @PostMapping
    public ResponseEntity<ApiResponse<StoreCreateResponse>> createStore(@Valid @RequestBody StoreCreateRequest request) {
        StoreCreateResponse response = storeService.createStore(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    // ========================================

    /**
     * 매장 목록 조회 API
     */
    @GetMapping
    public ResponseEntity<ApiResponse<UserStoresResponse>> getUserStores(@RequestParam Long userId) {
        UserStoresResponse response = storeService.getUserStores(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ========================================

    /**
     * 매장 조회 API
     */
    @GetMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreInfoResponse>> getStore(@PathVariable Long storeId) {
        // UserId는 spring security의 @AuthenticationPrincipal로 받아올 수 있음
        // Long userId = userDetails.getUserId();

        Long userId = 1L; // 여기서는 임의로 1로 설정
        StoreInfoResponse response = storeService.getStoreInfo(storeId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    // ========================================

    /**
     * 매장 정보 수정 API
     */
    @PatchMapping("/{storeId}")
    public ResponseEntity<ApiResponse<Void>> updateStore(@PathVariable Long storeId, @Valid @RequestBody StoreUpdateRequest request) {
        storeService.updateStore(storeId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ========================================

    /**
     * 매장 삭제 API
     */
    @DeleteMapping("/{storeId}")
    public ResponseEntity<ApiResponse<Void>> deleteStore(@PathVariable Long storeId) {
        storeService.deleteStore(storeId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    // ========================================

    /**
     * 매장 사용자 목록 조회 API
     */
    @GetMapping("/{storeId}/users")
    public ResponseEntity<ApiResponse<StoreUsersResponse>> getStoreUsers(@PathVariable Long storeId) {
        StoreUsersResponse response = storeService.getStoreUsers(storeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ========================================


    /**
     * 매장 정보 조회 API
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<StoreResponse>> getStoreSimpleInfo(@RequestParam UUID storeCode) {
        StoreResponse response = storeService.getStoreSimpleInfo(storeCode);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    // ========================================

    /**
     * 매장 입장 API
     */
    @GetMapping("/join")
    public ResponseEntity<ApiResponse<Void>> getStore(@RequestParam UUID storeCode) {
        // UserId는 spring security의 @AuthenticationPrincipal로 받아올 수 있음
        // Long userId = userDetails.getUserId();
        Long userId = 1L; // 여기서는 임의로 1로 설정
        
        storeService.joinUserStore(storeCode, userId);

        return ResponseEntity.ok(ApiResponse.success(null));
    }


}
