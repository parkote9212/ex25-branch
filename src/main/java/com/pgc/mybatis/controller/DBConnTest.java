package com.pgc.mybatis.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
@RequiredArgsConstructor
public class DBConnTest {

    private static final Logger log = LoggerFactory.getLogger(DBConnTest.class);
    private final DataSource dataSource;

    @GetMapping("/dbconn")
    public String dbconn() {
        try (Connection conn = dataSource.getConnection()) {
            String result = conn.getMetaData().getURL();
            log.info("DB 연결 성공: {}" , result); // 로그 출력
            return "DB연결 성공: " + result;
        } catch (Exception e) {
            log.error("DB 연결 실패" , e); // 에러 로그 출력
            return "DB연결실패";
        }
    }
}