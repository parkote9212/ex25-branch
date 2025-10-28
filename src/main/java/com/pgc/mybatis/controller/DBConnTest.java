package com.pgc.mybatis.controller;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DBConnTest {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/dbconn")
    public String dbconn() {
        try (Connection conn = dataSource.getConnection()) {
            String result = conn.getMetaData().getURL();
            return "DB연결 성공: " + result;
        } catch (Exception e) {
            e.printStackTrace();
            return "DB연결실패";
        }
    }
}