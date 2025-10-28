package com.pgc.mybatis.mapper;

import com.pgc.mybatis.domain.Student;
import org.apache.ibatis.annotations.*; // 필요한 어노테이션 import

import java.util.List;

@Mapper // 이 인터페이스가 MyBatis 매퍼임을 나타냄
public interface StudentMapper {

    /**
     * CREATE (생성)
     * @Insert 어노테이션을 사용해 INSERT 쿼리문 작성
     * @Options: useGeneratedKeys=true로 설정하여 DB가 생성한 ID 값을
     * keyProperty="id"에 지정된 Student 객체의 'id' 필드에 반환받습니다.
     */
    @Insert("INSERT INTO student (name, email, age, created_at, updated_at) " +
            "VALUES (#{name}, #{email}, #{age}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Student student);

    /**
     * READ (ID로 1건 조회)
     * @Select 어노테이션을 사용해 SELECT 쿼리문 작성
     */
    @Select("SELECT * FROM student WHERE id = #{id}")
    Student findById(Long id);

    /**
     * READ (전체 조회)
     */
    @Select("SELECT * FROM student ORDER BY id DESC")
    List<Student> findAll();

    /**
     * UPDATE (수정)
     * @Update 어노테이션을 사용해 UPDATE 쿼리문 작성
     */
    @Update("UPDATE student " +
            "SET name = #{name}, email = #{email}, age = #{age}, updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    void update(Student student);

    /**
     * DELETE (삭제)
     * @Delete 어노테이션을 사용해 DELETE 쿼리문 작성
     */
    @Delete("DELETE FROM student WHERE id = #{id}")
    void deleteById(Long id);
}