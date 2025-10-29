package com.pgc.mybatis.controller;

import com.pgc.mybatis.domain.Student; // Student 도메인 import 필요
import com.pgc.mybatis.service.StudentService; // StudentService import 필요
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/") // 기본 경로 설정 (루트)
@RequiredArgsConstructor
public class StudentViewController {

    private final StudentService studentService; // 서비스 주입

    /**
     * [READ] 학생 전체 목록 페이지 (index.html)
     * 루트("/") 또는 "/students" 경로로 접속 시
     */
    @GetMapping(value = {"/", "/students"})
    public String listStudents(Model model) {
        List<Student> students = studentService.getAllStudents();
        model.addAttribute("students", students); // 뷰에 학생 목록 전달
        return "index"; // "index.html" 렌더링
    }

    /**
     * [CREATE] 학생 생성 폼 페이지 (new.html)
     */
    @GetMapping("/students/new")
    public String newStudentForm(Model model) {
        model.addAttribute("student", new Student()); // 타임리프 폼 바인딩을 위한 빈 객체 전달
        return "new"; // "new.html" 렌더링
    }

    /**
     * [CREATE] 학생 생성 처리
     */
    @PostMapping("/students")
    public String createStudent(@Valid @ModelAttribute Student student,
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "new";
        }
        studentService.createStudent(student);
        return "redirect:/students"; // 목록 페이지로 리다이렉트
    }

    /**
     * [UPDATE] 학생 수정 폼 페이지 (edit.html)
     */
    @GetMapping("/students/{id}/edit")
    public String editStudentForm(@PathVariable("id") Long id, Model model) {
        Student student = studentService.getStudentById(id);
        if (student == null) {
            // TODO: 학생이 없을 경우 예외 처리 페이지
            return "redirect:/students";
        }
        model.addAttribute("student", student); // 뷰에 기존 학생 정보 전달
        return "edit"; // "edit.html" 렌더링
    }

    /**
     * [UPDATE] 학생 수정 처리
     */
    @PostMapping("/students/{id}")
    public String updateStudent(@PathVariable("id") Long id, @Valid @ModelAttribute Student studentDetails,
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return "edit";
        }
        studentService.updateStudent(id, studentDetails);
        return "redirect:/students"; // 목록 페이지로 리다이렉트
    }

    /**
     * [DELETE] 학생 삭제 처리
     */
    @PostMapping("/students/{id}/delete")
    public String deleteStudent(@PathVariable("id") Long id) {
        studentService.deleteStudent(id);
        return "redirect:/students"; // 목록 페이지로 리다이렉트
    }
}