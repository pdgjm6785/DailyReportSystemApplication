package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.EmployeeRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReportService reportService;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder, ReportService reportService) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.reportService = reportService;
    }

    @Transactional
    public ErrorKinds save(Employee employee) {
        return null;
        // 保存処理の実装
    }

    @Transactional
    public void delete(String code, UserDetail userDetail) {
        // 削除処理の実装
    }

    public void findAll() {
        // 一覧表示処理の実装
    }

    public void findByCode(String code) {
        // 検索処理の実装
    }

    private void employeePasswordCheck(Employee employee) {
        // パスワードチェック処理の実装
    }

    @Transactional
    public void update(String code, Employee employee) {
        // 更新処理の実装
    }
}