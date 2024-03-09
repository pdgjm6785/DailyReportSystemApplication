package com.techacademy.service;

import java.time.LocalDate;
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
                .orElse(null);
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
        if (reportRepository.existsByEmployeeAndReportDateAndIdNot(report.getEmployee(), report.getReportDate(),report.getId())) {
            return ErrorKinds.DATECHECK_ERROR;
        }
        // 更新対象の従業員を検索
        Report existingReport = findReportById(report.getId());

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
//修正中３．５
//    @Transactional
//    public ErrorKinds updateEmployee(Integer id, Employee updatedEmployee) {
//        // 更新対象の従業員を検索
//        Employee existingEmployee = employeeRepository.findById(id)
//                .orElse(null);
//
//        // 更新対象の従業員が存在しない場合はエラー
//        if (existingEmployee == null) {
//            return ErrorKinds.NOT_FOUND_ERROR;
//        }
//
//        // 今の日付を取得
//        LocalDateTime now = LocalDateTime.now();
//
//        // 同じユーザーかつ同じ日付かつ今のIDと一致するものを更新
//        if (existingEmployee.getCode().equals(updatedEmployee.getCode()) &&
//            existingEmployee.getCreatedAt().toLocalDate().isEqual(updatedEmployee.getCreatedAt().toLocalDate()) &&
//            existingEmployee.getcode().equals(id)) {
//            existingEmployee.setName(updatedEmployee.getName());
//            existingEmployee.setPassword(updatedEmployee.getPassword());
//            existingEmployee.setRole(updatedEmployee.getRole());
//            existingEmployee.setUpdatedAt(now);
//            employeeRepository.save(existingEmployee);
//            return ErrorKinds.SUCCESS;
//        } else {
//            return ErrorKinds.UPDATE_ERROR; // 更新できない場合のエラー
//        }
//    }
//
   //修正中ここまで


}