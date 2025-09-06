//package com.emolink.emolink.handler;
//
//import com.emolink.emolink.DTO.ErrorResponse;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//    // IllegalStateException을 처리
//    @ExceptionHandler(IllegalStateException.class)
//    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
//        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
//        return ResponseEntity
//                .status(HttpStatus.CONFLICT) // 409 Conflict: 리소스 충돌 (이미 기기가 존재)
//                .body(errorResponse);
//    }
//}