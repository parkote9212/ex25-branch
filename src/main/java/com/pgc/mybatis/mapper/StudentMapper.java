package com.pgc.mybatis.mapper;

import com.pgc.mybatis.domain.Student;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper // 이 인터페이스가 MyBatis 매퍼임을 나타냄
public interface StudentMapper {

    // insert 쿼리와 매핑. useGeneratedKeys=true 옵션으로 id 값을 반환받을 수 있음
    void insert(Student student);

    // select 쿼리와 매핑
    Student findById(Long id);

    // select 쿼리와 매핑
    List<Student> findAll();

    // update 쿼리와 매핑
    void update(Student student);

    // delete 쿼리와 매핑
    void deleteById(Long id);
}