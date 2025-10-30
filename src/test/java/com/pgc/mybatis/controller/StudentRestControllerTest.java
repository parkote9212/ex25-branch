package com.pgc.mybatis.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgc.mybatis.domain.Student;
import com.pgc.mybatis.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

// BDDMockito (given)과 Hamcrest Matcher (is, hasSize) static import
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

/**
 * @WebMvcTest(StudentRestController.class)
 * - 웹 계층(Controller) 관련 빈만 로드합니다.
 * - @Service, @Mapper 등은 로드하지 않습니다.
 */
@WebMvcTest(StudentRestController.class)
class StudentRestControllerTest {

    @Autowired
    private MockMvc mockMvc; // HTTP 요청을 시뮬레이션

    @Autowired
    private ObjectMapper objectMapper; // Java <-> JSON 변환

    @MockBean // 컨트롤러가 의존하는 서비스를 가짜(Mock) 객체로 주입
    private StudentService studentService;

    private Student student;

    @BeforeEach
    void setUp() {
        // 테스트에서 공통으로 사용할 학생 객체 설정
        student = new Student();
        student.setId(1L);
        student.setName("테스트학생");
        student.setEmail("test@example.com");
        student.setAge(20);
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("학생 생성 (POST /api/students)")
    void createStudent() throws Exception {
        // given
        // 1. 요청으로 보낼 학생 객체 (ID가 없음)
        Student requestStudent = new Student();
        requestStudent.setName("새학생");
        requestStudent.setEmail("new@example.com");
        requestStudent.setAge(21);

        // 2. 서비스가 반환할 학생 객체 (ID가 채워짐)
        Student savedStudent = new Student();
        savedStudent.setId(1L);
        savedStudent.setName("새학생");
        savedStudent.setEmail("new@example.com");
        savedStudent.setAge(21);

        // 3. Mocking: studentService.createStudent()가 호출되면 savedStudent를 반환
        given(studentService.createStudent(any(Student.class))).willReturn(savedStudent);

        // when & then
        mockMvc.perform(post("/api/students") // POST 요청
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestStudent))) // Body에 JSON 전송
                .andExpect(status().isCreated()) // 1. HTTP 201 Created 확인
                .andExpect(jsonPath("$.id", is(1))) // 2. 응답 JSON의 id 필드 확인
                .andExpect(jsonPath("$.name", is("새학생"))); // 3. 응답 JSON의 name 필드 확인
    }

    @Test
    @DisplayName("ID로 학생 조회 (GET /api/students/{id}) - 성공")
    void getStudentById_Success() throws Exception {
        // given
        // Mocking: studentService.getStudentById(1L)이 호출되면, setUp()의 student 객체 반환
        given(studentService.getStudentById(1L)).willReturn(student);

        // when & then
        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk()) // 1. HTTP 200 OK 확인
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("테스트학생")))
                .andExpect(jsonPath("$.email", is("test@example.com")));
    }

    @Test
    @DisplayName("ID로 학생 조회 (GET /api/students/{id}) - 404 Not Found")
    void getStudentById_NotFound() throws Exception {
        // given
        // Mocking: 존재하지 않는 ID(998L)로 조회 시 서비스가 null 반환
        given(studentService.getStudentById(998L)).willReturn(null);

        // when & then
        mockMvc.perform(get("/api/students/998"))
                .andExpect(status().isNotFound()); // HTTP 404 Not Found 확인
    }

    @Test
    @DisplayName("ID로 학생 조회 (GET /api/students/{id}) - 500 Internal Server Error (서비스 예외)")
    void getStudentById_Exception() throws Exception {
        // given
        // Service 코드에 작성하신 강제 예외 발생 로직 테스트
        // Mocking: 999L ID로 조회 시 RuntimeException 발생
        given(studentService.getStudentById(999L))
                .willThrow(new RuntimeException("AOP 예외 테스트용 강제 에러!"));

        // when & then
        mockMvc.perform(get("/api/students/999"))
                .andExpect(status().isInternalServerError()); // HTTP 500 확인
    }

    @Test
    @DisplayName("모든 학생 조회 (GET /api/students)")
    void getAllStudents() throws Exception {
        // given
        Student student2 = new Student();
        student2.setId(2L);
        student2.setName("학생2");
        student2.setEmail("test2@example.com");

        List<Student> students = List.of(student, student2);

        // Mocking: studentService.getAllStudents() 호출 시 2명의 학생 리스트 반환
        given(studentService.getAllStudents()).willReturn(students);

        // when & then
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // 1. 리스트 크기가 2인지 확인
                .andExpect(jsonPath("$[0].name", is("테스트학생")))
                .andExpect(jsonPath("$[1].name", is("학생2")));
    }

    @Test
    @DisplayName("학생 정보 수정 (PUT /api/students/{id}) - 성공")
    void updateStudent_Success() throws Exception {
        // given
        // 1. 수정 요청으로 보낼 객체
        Student updateRequest = new Student();
        updateRequest.setName("수정된이름");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setAge(25);

        // 2. 서비스가 반환할 수정 완료된 객체
        Student updatedStudent = new Student();
        updatedStudent.setId(1L);
        updatedStudent.setName("수정된이름");
        updatedStudent.setEmail("updated@example.com");
        updatedStudent.setAge(25);

        // Mocking: 1L ID에 대해 수정 요청이 오면 updatedStudent 객체 반환
        given(studentService.updateStudent(eq(1L), any(Student.class)))
                .willReturn(updatedStudent);

        // when & then
        mockMvc.perform(put("/api/students/1") // PUT 요청
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("수정된이름")))
                .andExpect(jsonPath("$.age", is(25)));
    }

    @Test
    @DisplayName("학생 정보 수정 (PUT /api/students/{id}) - 404 Not Found")
    void updateStudent_NotFound() throws Exception {
        // given
        Student updateRequest = new Student();
        updateRequest.setName("수정된이름");

        // Mocking: 존재하지 않는 ID(998L)로 수정 시도 시 서비스가 null 반환
        given(studentService.updateStudent(eq(998L), any(Student.class)))
                .willReturn(null);

        // when & then
        mockMvc.perform(put("/api/students/998")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound()); // HTTP 404 Not Found 확인
    }

    @Test
    @DisplayName("학생 정보 삭제 (DELETE /api/students/{id})")
    void deleteStudent() throws Exception {
        // given
        // Mocking: studentService.deleteStudent(1L)은 반환값이 void
        willDoNothing().given(studentService).deleteStudent(1L);

        // when & then
        mockMvc.perform(delete("/api/students/1")) // DELETE 요청
                .andExpect(status().isNoContent()); // HTTP 204 No Content 확인
    }
}