package com.pgc.mybatis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentViewController {

    /**
     * 웹 브라우저에서 "/" 경로로 접속 시
     * resources/templates/student.html 파일을 찾아서 보여준다.
     */
    @GetMapping("/")
    public String studentPage() {
        return "student"; // "student.html"을 렌더링
    }
}