package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.entity.Report;
import com.techacademy.constants.ErrorKinds;
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

    public Report findReportById(Integer id) {
        return reportRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }
    @Transactional
    public ErrorKinds save(Report report) {


        // 従業員番号重複チェック
//        if (findByCode(employee.getCode()) != null) {
//            return ErrorKinds.DUPLICATE_ERROR;
//        }

        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }
    public Report saveOrUpdateReport(Report report) {
        return reportRepository.save(report);
    }

    // 1件を検索
    public Report findById(Integer id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

    public ErrorKinds delete(Integer id) {
        Report report = findById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    // 従業員に紐づく日報情報を取得するメソッドを追加
    public List<Report> findByEmployee(Employee employee) {
        return reportRepository.findByEmployee(employee);
    }
}