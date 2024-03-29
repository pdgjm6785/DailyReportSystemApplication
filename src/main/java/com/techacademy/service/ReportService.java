package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
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
                .orElse(null);

    }
    public List<Report> findAllReportsForUser(UserDetail userDetail) {
        if (userDetail.getEmployee().getRole() == Employee.Role.GENERAL) {
            // 一般権限のユーザーの場合、自分が登録した日報情報のみを取得
            return reportRepository.findByEmployee(userDetail.getEmployee());
        } else if (userDetail.getEmployee().getRole() == Employee.Role.ADMIN) {
            // 管理者権限のユーザーの場合、全ての日報情報を取得
            return reportRepository.findAll();
        }
        // その他の権限の場合、空のリストを返すなどの適切な処理を行う
        return Collections.emptyList();
    }
    @Transactional
    public ErrorKinds save(Report report) {
        if (reportRepository.existsByEmployeeAndReportDate(report.getEmployee(), report.getReportDate())) {
            return ErrorKinds.DATECHECK_ERROR;
        }

        report.setDeleteFlg(false);

        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }
    @Transactional
    public ErrorKinds update(Report report) {
        // 更新対象の従業員を検索
        Report existingReport = findReportById(report.getId());

        // 更新しようとしている日付が既存の日報と重複しているかどうかをチェック
        if (reportRepository.existsByEmployeeAndReportDateAndIdNot(report.getEmployee(), report.getReportDate(), report.getId())) {
            return ErrorKinds.DATECHECK_ERROR;
        }

        // 更新対象の従業員が存在しない場合はエラー
        if (existingReport == null) {
            return ErrorKinds.NOT_FOUND_ERROR;
        }

        // 更新処理の実装
        existingReport.setTitle(report.getTitle());
        existingReport.setContent(report.getContent());
        existingReport.setUpdatedAt(LocalDateTime.now());

        reportRepository.save(existingReport);
        return ErrorKinds.SUCCESS;
    }

//    @Transactional
//    public ErrorKinds update(Report report) {
//     // 更新対象の従業員を検索
//        Report existingReport = findReportById(report.getId());
//
//        if (reportRepository.existsByEmployeeAndReportDateAndIdNot(report.getEmployee(), report.getReportDate(),report.getId())) {
//            return ErrorKinds.DATECHECK_ERROR;
//        }
//        // 更新対象の従業員が存在しない場合はエラー
//        if (existingReport == null) {
//            return ErrorKinds.NOT_FOUND_ERROR;
//        }
//
//        // 更新処理の実装
//        existingReport.setTitle(report.getTitle());
//        existingReport.setContent(report.getContent());
//        existingReport.setUpdatedAt(LocalDateTime.now());
//
//        reportRepository.save(existingReport);
//        return ErrorKinds.SUCCESS;
//    }


    // 1件を検索
    public Report findById(Integer id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

    // 従業員に紐づく日報情報を取得するメソッドを追加
    public List<Report> findByEmployee(Employee employee) {
        return reportRepository.findByEmployee(employee);
    }
    @Transactional
    public ErrorKinds delete(Integer id) {

            // 削除対象の日報を取得
            Optional<Report> optionalReport = reportRepository.findById(id);

            // 日報が存在するかチェック
            if (optionalReport.isPresent()) {
                Report report = optionalReport.get();

                // 日報を論理削除
                report.setDeleteFlg(true);
                report.setUpdatedAt(LocalDateTime.now());

                // データベースから日報を削除
                reportRepository.save(report);

                return ErrorKinds.SUCCESS;
            } else {
                // 削除対象の日報が見つからない場合はエラーを返す
                return ErrorKinds.NOT_FOUND_ERROR;
            }
    }
}