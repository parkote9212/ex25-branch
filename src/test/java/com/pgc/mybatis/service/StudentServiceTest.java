package com.pgc.mybatis.service;

import com.pgc.mybatis.domain.Student;
import com.pgc.mybatis.mapper.StudentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

//    Mockito
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    //    테스트 대상( Mock 객체 주입)
    @InjectMocks
    private StudentService studentService;

    //    가짜 객체
    @Mock
    private StudentMapper studentMapper;

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1L);
        student.setName("테스트학생");
        student.setEmail("test@example.com");
        student.setAge(20);
    }

    @Test
    @DisplayName("학생 생성(createStudent) - 시간 값 자동 설정")
    void createStudent() {
        // given
        Student newStudent = new Student();
        newStudent.setName("새학생");
        newStudent.setEmail("new@example.com");
        newStudent.setAge(21);

        // ArgumentCaptor: studentMapper.insert()가 호출될 때 전달된 Student 객체를 캡처
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);

        // When
        studentService.createStudent(newStudent);

        // then
        // 1. insert가 1번 호출되었는지 검증
        // 2. 캡쳐된 객체를 가져옴
        then(studentMapper).should(times(1)).insert(studentCaptor.capture());
        Student capturedStudent = studentCaptor.getValue();

        //3. 서비스 로직에 의해 At가 설정되었는지 검증
        assertThat(capturedStudent.getName()).isEqualTo("새학생");
        assertThat(capturedStudent.getCreatedAt()).isNotNull();
        assertThat(capturedStudent.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("ID로 학생 조회(getStudentByID")
    void getStudentById() {
        // given
        // 1L ID로 조회 시, 'student' 객체를 반환하도록 설정
        given(studentMapper.findById(1L)).willReturn(student);
        // when
        Student foundStudent = studentService.getStudentById(1L);

        // then
        assertThat(foundStudent)
                .isNotNull()
                .extracting(Student::getName, Student::getAge) // 이름과 나이를 추출
                .containsExactly("테스트학생", 20); // 순서대로 검증
    }

    @Test
    @DisplayName("ID 999로 조회시 강제 예외 발생")
    void getStudentById_ForceError() {
        // given
        Long errorId = 999L;

        // when & then
        // studentService.getStudentById(999L)를 실행하면
        // RuntimeException이 발생하고, 메시지가 "AOP..."인지 검증
        assertThatThrownBy(() -> studentService.getStudentById(errorId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("AOP 예외 테스트용 강제 에러!");

        // 예외가 발생했으므로 findById는 호출되지 않아야 함 (서비스 코드 로직 확인)
        // (주: 서비스 코드에서는 findById 이전에 예외를 던졌으므로 never()가 맞음)
        then(studentMapper).should(never()).findById(errorId);
    }

    @Test
    @DisplayName("학생 정보 수정(updateStudent) - 성공")
    void updateStudent() {
        //given
        Student updateDetails = new Student();
        updateDetails.setName("수정된이름");
        updateDetails.setEmail("updated@example.com");
        updateDetails.setAge(30);

        // 1. ID로 조회 시 'student' 객체를 반환하도록 설정
        given(studentMapper.findById(1L)).willReturn(student);

        // when
        Student updatedStudent = studentService.updateStudent(1L, updateDetails);

        // then
        // update가 1번 호출되었는지 검증
        then(studentMapper).should(times(1)).update(any(Student.class));

        //2. 반환된 객체의 정보가 올바르게 수정되었는지 검증
        // 'updatedAt' 검증은 별도로
        assertThat(updatedStudent.getUpdatedAt()).isNotNull();
// 이름과 나이 검증은 체이닝
        assertThat(updatedStudent)
                .extracting(Student::getName, Student::getAge)
                .containsExactly("수정된이름", 30);

    }

    @Test
    @DisplayName("학생 정보 수정(updateStudent) - 대상 없음")
    void updateStudent_NotFound() {
        // given
        Student updateDetails = new Student();
        updateDetails.setName("수정된이름");

        // 1. ID 99L로 조회 시 null을 반환하도록 설정 (없는 학생)
        given(studentMapper.findById(99L)).willReturn(null);

        // when
        Student updatedStudent = studentService.updateStudent(99L, updateDetails);

        // then
        assertThat(updatedStudent).isNull(); // 서비스가 null을 반환
        // findById는 호출했지만, update는 호출되지 않아야 함
        then(studentMapper).should(times(1)).findById(99L);
        then(studentMapper).should(never()).update(any(Student.class));
    }

    @Test
    @DisplayName("학생 삭제(deleteStudent)")
    void deleteStudent() {
        // given
        Long id = 1L;

        // when
        studentService.deleteStudent(id);

        // then
        // studentMapper.deleteById(1L)가 1번 호출되었는지 검증
        then(studentMapper).should(times(1)).deleteById(id);
    }

}
