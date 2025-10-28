package com.pgc.mybatis.controller;

import com.pgc.mybatis.domain.Student;
import com.pgc.mybatis.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students") // URL 경로 공통 부분 지정
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 주입
public class StudentRestController {

    private final StudentService studentService;

    // CREATE: 학생 생성
    @PostMapping
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) { // <-- @Valid 추가
        Student createdStudent = studentService.createStudent(student);
        return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
    }

    // READ: ID로 학생 조회
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable("id") Long id) {
        Student student = studentService.getStudentById(id);
        if (student != null) {
            return new ResponseEntity<>(student, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // READ: 모든 학생 조회
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    // UPDATE: 학생 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable("id") Long id,
                                                 @Valid @RequestBody Student studentDetails) { // <-- @Valid 추가
        Student updatedStudent = studentService.updateStudent(id, studentDetails);
        if (updatedStudent != null) {
            return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // DELETE: 학생 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable("id") Long id) {
        studentService.deleteStudent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 성공적으로 삭제되었으나 반환할 컨텐츠 없음
    }
}