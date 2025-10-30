package com.pgc.mybatis.mapper;

import com.pgc.mybatis.domain.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
// 내장 DB H2 사용
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StudentMapperTest {

    //    테스트 컨텍스트에 의 해 주입된 StudentMapper
    @Autowired
    private StudentMapper studentMapper;

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setName("테스트학생");
        student.setEmail("test@example.com");
        student.setAge(20);
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("학생정보 삽입(insert 및 ID로 조회(findById)")
    void insertAndFindById() {
//         given - (setUp()에서 student 객체 준비)

//         when
        studentMapper.insert(student);
        Long generatedId = student.getId();

//         then
        assertThat(generatedId).isNotNull();

        Student foundStudent = studentMapper.findById(generatedId);
        assertThat(foundStudent).isNotNull();
        assertThat(foundStudent.getName()).isEqualTo("테스트학생");
        assertThat(foundStudent.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("모든 학생 조회(findAll)")
    void findAll(){
        //given
        studentMapper.insert(student);

        Student student2 = new Student();
        student2.setName("학생2");
        student2.setEmail("test2@example.com");
        student2.setAge(22);
        student2.setCreatedAt(LocalDateTime.now());
        student2.setUpdatedAt(LocalDateTime.now());
        studentMapper.insert(student2);

        //when
        List<Student> students = studentMapper.findAll();

        // then
        assertThat(students).isNotNull();
        assertThat(students.size()).isEqualTo(2);
        assertThat(students.get(0).getName()).isEqualTo("학생2");
        assertThat(students.get(1).getName()).isEqualTo("테스트학생");
    }
    @Test
    @DisplayName("학생 정보 수정(update)")
    void update() {
        // given
        studentMapper.insert(student);
        Long id = student.getId();

        // when
        Student studentToUpdate = studentMapper.findById(id);
        studentToUpdate.setName("수정된이름");
        studentToUpdate.setAge(25);
        studentToUpdate.setUpdatedAt(LocalDateTime.now());
        studentMapper.update(studentToUpdate); // 정보 수정

        // then
        Student updatedStudent = studentMapper.findById(id);
        assertThat(updatedStudent.getName()).isEqualTo("수정된이름");
        assertThat(updatedStudent.getAge()).isEqualTo(25);
    }

    @Test
    @DisplayName("학생 정보 삭제(deleteById)")
    void deleteById() {
        // given
        studentMapper.insert(student);
        Long id = student.getId();
        assertThat(studentMapper.findById(id)).isNotNull(); // 삽입 확인

        // when
        studentMapper.deleteById(id); // 삭제 실행

        // then
        assertThat(studentMapper.findById(id)).isNull(); // 삭제 확인
    }
}
