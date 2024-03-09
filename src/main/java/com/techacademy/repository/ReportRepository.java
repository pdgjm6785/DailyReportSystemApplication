package com.techacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.techacademy.entity.Report;
import com.techacademy.entity.Employee;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    // 従業員に紐づく日報情報を取得
    List<Report> findByEmployee(Employee employee);
    boolean existsByEmployeeAndReportDate(Employee employee , LocalDate reportDate);
    boolean existsByEmployeeAndReportDateAndIdNot(Employee employee , LocalDate reportDate , Integer id);
}