package com.pgc.mybatis.service;

import com.pgc.mybatis.domain.Student;
import com.pgc.mybatis.mapper.StudentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentMapper studentMapper; // MyBatis Mapper 주입

    // CREATE
    @Transactional
    public Student createStudent(Student student) {
        // 생성 시각과 수정 시각을 현재로 설정
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());
        studentMapper.insert(student);
        return student; // insert 후 auto-generated key가 student 객체에 채워짐 (설정 필요)
    }

    // READ by ID
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public Student getStudentById(Long id) {

        if (id == 999L) {
            throw new RuntimeException("AOP 예외 테스트용 강제 에러!");
        }

        return studentMapper.findById(id);
    }

    // READ All
    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentMapper.findAll();
    }

    // UPDATE
    @Transactional
    public Student updateStudent(Long id, Student studentDetails) {
        Student existingStudent = studentMapper.findById(id);
        if (existingStudent == null) {
            // 수정할 대상이 없으면 null 반환 (Controller에서 NOT_FOUND 처리)
            return null;
        }
        // 전달받은 정보로 기존 학생 정보 업데이트
        existingStudent.setName(studentDetails.getName());
        existingStudent.setEmail(studentDetails.getEmail());
        existingStudent.setAge(studentDetails.getAge());
        existingStudent.setUpdatedAt(LocalDateTime.now()); // 수정 시각 갱신

        studentMapper.update(existingStudent);
        return existingStudent;
    }

    // DELETE
    @Transactional
    public void deleteStudent(Long id) {
        studentMapper.deleteById(id);
    }
}