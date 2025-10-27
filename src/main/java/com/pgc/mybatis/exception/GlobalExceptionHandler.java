package com.pgc.mybatis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @RestControllerAdvice : 모든 @RestController에서 발생하는 예외를
 * 전역적으로 처리하는 클래스임을 나타냅니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @ExceptionHandler(MethodArgumentNotValidException.class) :
     * @Valid 어노테이션으로 인한 유효성 검증 실패 시 발생하는
     * MethodArgumentNotValidException 예외를 이 메서드가 처리합니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 응답 상태 코드를 400 (Bad Request)로 지정
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        // 발생한 모든 유효성 검증 오류를 순회
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            // (FieldError) 형변환을 통해 오류가 발생한 필드 이름을 가져옴
            String fieldName = ((FieldError) error).getField();
            // Student.java에 설정한 defaultMessage를 가져옴
            String errorMessage = error.getDefaultMessage();

            // { "필드명" : "오류 메시지" } 형태로 Map에 저장
            errors.put(fieldName, errorMessage);
        });

        // 오류 정보를 담은 Map과 400 상태 코드를 함께 반환
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}