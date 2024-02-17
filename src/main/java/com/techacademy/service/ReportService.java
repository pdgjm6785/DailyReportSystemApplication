package com.techacademy.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techacademy.entity.Report;
import com.techacademy.entity.Employee; // 追加
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public List<Report> findAllReports() {
        return reportRepository.findAll();
    }

    public Report findReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    public Report saveOrUpdateReport(Report report) {
        return reportRepository.save(report);
    }

    public void deleteReport(Long id) {
        if (reportRepository.existsById(id)) {
            reportRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("IDに対応する日報が見つかりません: " + id);
        }
    }

    // 従業員に紐づく日報情報を取得するメソッドを追加
    public List<Report> findByEmployee(Employee employee) {
        return reportRepository.findByEmployee(employee);
    }
}
