package com.pgc.mybatis.domain;

// jakarta.validation.constraints.* 패키지의 어노테이션들을 import 합니다.
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Student {

    private Long id;

    // @NotBlank: Null, "", " " (공백)을 모두 허용하지 않음
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.") // 이메일 형식 검증
    private String email;

    // @NotNull: Null을 허용하지 않음 (Integer 같은 객체 타입에 사용)
    @NotNull(message = "나이는 필수 입력 항목입니다.")
    @Min(value = 1, message = "나이는 1 이상의 값이어야 합니다.") // 최소값 검증
    private Integer age;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}