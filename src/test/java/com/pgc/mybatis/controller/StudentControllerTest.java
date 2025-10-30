package com.pgc.mybatis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pgc.mybatis.domain.Student;
import com.pgc.mybatis.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 1. JUnit5와 Mockito를 함께 사용하기 위한 애너테이션
@ExtendWith(MockitoExtension.class)
class StudentRestControllerTest {

    // 2. Mock(가짜) 객체 생성
    @Mock
    private StudentService studentService;

    // 3. 테스트 대상 컨트롤러, @Mock으로 만든 객체를 이 컨트롤러에 주입
    @InjectMocks
    private StudentRestController studentRestController;

    // HTTP 요청을 시뮬레이션하기 위한 객체
    private MockMvc mockMvc;

    // Java 객체와 JSON 문자열 간의 변환을 위한 객체
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // LocalDateTime 타입을 JSON으로 변환하기 위한 설정
        objectMapper.registerModule(new JavaTimeModule());

        // 4. 독립 실행 모드로 MockMvc 설정. 스프링 컨텍스트를 로드하지 않음.
        mockMvc = MockMvcBuilders.standaloneSetup(studentRestController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper)) // JSON 변환기 설정
                .build();
    }

    @Test
    @DisplayName("학생 생성 성공")
    void createStudent_Success() throws Exception {
        // given (테스트 준비)
        Student studentToCreate = new Student();
        studentToCreate.setName("테스트");
        studentToCreate.setEmail("test@example.com");
        studentToCreate.setAge(20);

        Student createdStudent = new Student();
        createdStudent.setId(1L);
        createdStudent.setName("테스트");
        createdStudent.setEmail("test@example.com");
        createdStudent.setAge(20);
        createdStudent.setCreatedAt(LocalDateTime.now());
        createdStudent.setUpdatedAt(LocalDateTime.now());

        // studentService.createStudent()가 어떤 Student 객체든 받으면,
        // 미리 준비한 createdStudent를 반환하도록 설정
        given(studentService.createStudent(any(Student.class))).willReturn(createdStudent);

        // when (테스트 실행) & then (결과 검증)
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentToCreate))) // 객체를 JSON 문자열로 변환
                .andExpect(status().isCreated()) // HTTP 상태 코드가 201 Created인지 확인
                .andExpect(jsonPath("$.id").value(1L)) // JSON 응답의 id 필드 값 확인
                .andExpect(jsonPath("$.name").value("테스트"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("ID로 학생 조회 성공")
    void getStudentById_Success() throws Exception {
        // given
        Long studentId = 1L;
        Student foundStudent = new Student();
        foundStudent.setId(studentId);
        foundStudent.setName("홍길동");
        given(studentService.getStudentById(studentId)).willReturn(foundStudent);

        // when & then
        mockMvc.perform(get("/api/students/{id}", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentId))
                .andExpect(jsonPath("$.name").value("홍길동"));
    }

    @Test
    @DisplayName("ID로 학생 조회 실패 - 찾을 수 없음")
    void getStudentById_NotFound() throws Exception {
        // given
        Long studentId = 99L;
        given(studentService.getStudentById(studentId)).willReturn(null);

        // when & then
        mockMvc.perform(get("/api/students/{id}", studentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("모든 학생 조회 성공")
    void getAllStudents_Success() throws Exception {
        // given
        Student student1 = new Student();
        student1.setId(1L);
        student1.setName("학생1");

        Student student2 = new Student();
        student2.setId(2L);
        student2.setName("학생2");

        List<Student> studentList = Arrays.asList(student1, student2);
        given(studentService.getAllStudents()).willReturn(studentList);

        // when & then
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray()) // 응답이 배열인지 확인
                .andExpect(jsonPath("$.length()").value(2)) // 배열 크기 확인
                .andExpect(jsonPath("$[0].name").value("학생1"))
                .andExpect(jsonPath("$[1].name").value("학생2"));
    }


    @Test
    @DisplayName("학생 정보 수정 성공")
    void updateStudent_Success() throws Exception {
        // given
        Long studentId = 1L;
        Student studentDetails = new Student();
        studentDetails.setName("김수정");
        studentDetails.setEmail("update@example.com");
        studentDetails.setAge(25);

        Student updatedStudent = new Student();
        updatedStudent.setId(studentId);
        updatedStudent.setName("김수정");
        updatedStudent.setEmail("update@example.com");
        updatedStudent.setAge(25);

        given(studentService.updateStudent(eq(studentId), any(Student.class))).willReturn(updatedStudent);

        // when & then
        mockMvc.perform(put("/api/students/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("김수정"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    @DisplayName("학생 삭제 성공")
    void deleteStudent_Success() throws Exception {
        // given
        Long studentId = 1L;
        // studentService.deleteStudent()는 반환값이 없으므로 아무것도 하지 않도록 설정
        doNothing().when(studentService).deleteStudent(studentId);

        // when & then
        mockMvc.perform(delete("/api/students/{id}", studentId))
                .andExpect(status().isNoContent()); // HTTP 204 No Content 확인

        // deleteStudent 메서드가 1번 호출되었는지 검증
        verify(studentService).deleteStudent(studentId);
    }
}