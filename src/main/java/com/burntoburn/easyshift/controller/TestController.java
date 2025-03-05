package com.burntoburn.easyshift.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @Operation(
            summary = "테스트 엔드포인트",
            description = "Swagger 및 API 동작 테스트를 위한 엔드포인트입니다."
    )
    @GetMapping
    public ResponseEntity<TestResponse> testEndpoint() {
        return ResponseEntity.ok(new TestResponse("테스트 성공"));
    }
    
    
    public record TestResponse(String message) {}
    
}
