package com.techacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.techacademy.entity.Report;
import com.techacademy.entity.Employee; 
import java.util.List; 

public interface ReportRepository extends JpaRepository<Report, Long> {
    // 従業員に紐づく日報情報を取得
    List<Report> findByEmployee(Employee employee);
}