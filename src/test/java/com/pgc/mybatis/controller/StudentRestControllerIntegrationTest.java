package com.pgc.mybatis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgc.mybatis.domain.Student;
import com.pgc.mybatis.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 2. @WebMvcTest: Web 레이어만 테스트, StudentRestController를 타겟으로 지정
@WebMvcTest(StudentRestController.class)
class StudentRestControllerIntegrationTest { // 클래스명 변경 (선택)

    // 3. @Autowired MockMvc: 스프링이 자동 설정한 MockMvc 주입
    @Autowired
    private MockMvc mockMvc;

    // 4. @Autowired ObjectMapper: 스프링이 자동 설정한 ObjectMapper 주입
    // (JSON <-> Java 객체 변환)
    @Autowired
    private ObjectMapper objectMapper;

    // 5. @MockitoBean: StudentService를 모의 객체로 만들어 스프링 컨텍스트에 주입
    // 이 테스트 컨텍스트에서는 실제 StudentService 빈이 로드되지 않음.
    @MockitoBean
    private StudentService studentService;

    // (참고: @Mock, @InjectMocks, standaloneSetup()은 여기서는 사용하지 않음)

    @Test
    @DisplayName("학생 생성 성공 (w/ @MockitoBean)")
    void createStudent_Success() throws Exception {
        // given
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

        // studentService.createStudent()가 호출되면 createdStudent를 반환하도록 설정
        given(studentService.createStudent(any(Student.class))).willReturn(createdStudent);

        // when & then
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentToCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("테스트"));
    }

    @Test
    @DisplayName("학생 생성 실패 - @Valid 유효성 검사")
    void createStudent_ValidationFailure() throws Exception {
        // given
        Student invalidStudent = new Student();
        invalidStudent.setName(""); // @NotBlank 위반
        invalidStudent.setEmail("not-an-email"); // @Email 위반
        invalidStudent.setAge(0); // @Min(1) 위반

        // when & then
        // @Valid에 의해 컨트롤러 진입 전에 예외가 발생하므로,
        // studentService의 given() 설정은 필요 없음.
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidStudent)))
                .andExpect(status().isBadRequest()); // HTTP 400 Bad Request
    }

    @Test
    @DisplayName("ID로 학생 조회 성공 (w/ @MockitoBean)")
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
    @DisplayName("ID로 학생 조회 실패 - 찾을 수 없음 (w/ @MockitoBean)")
    void getStudentById_NotFound() throws Exception {
        // given
        Long studentId = 99L;
        given(studentService.getStudentById(studentId)).willReturn(null);

        // when & then
        mockMvc.perform(get("/api/students/{id}", studentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("학생 정보 수정 성공 (w/ @MockitoBean)")
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

        given(studentService.updateStudent(eq(studentId), any(Student.class))).willReturn(updatedStudent);

        // when & then
        mockMvc.perform(put("/api/students/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("김수정"));
    }
}