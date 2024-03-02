package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 日報一覧画面を表示するメソッド
    @GetMapping("")
    public String listReports(Model model) {
        List<Report> reportList = reportService.findAllReports();
        model.addAttribute("reportList", reportList);
        model.addAttribute("listSize", reportList.size());
        return "reports/list";
    }

    // 新規日報作成画面を表示するメソッド
    @GetMapping("/add")
    public String showAddReportForm(Model model) {
        Report report = new Report();
        report.setReportDate(LocalDate.now()); // 日付を初期化
        model.addAttribute("report", report);
        return "reports/new";
    }

    @PostMapping("/add")
    public String addReport(@ModelAttribute("report") @Valid Report report, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "reports/new"; // バリデーションエラーがある場合は、新規登録画面に戻る
        }
        reportService.saveOrUpdateReport(report); // レポートを保存または更新
        redirectAttributes.addFlashAttribute("successMessage", "日報を登録しました");
        return "redirect:/reports";
    }

    // 日報詳細画面を表示するメソッド
    @GetMapping("/{id}/")
    public String showReportDetail(@PathVariable("id") Integer id, Model model) {
        Report report = reportService.findReportById(id);
        if (report == null) {
            return "redirect:/reports";
        }
        model.addAttribute("report", report);
        return "reports/detail";
    }

    // 日報削除処理
    @PostMapping("/{id}/delete")
    public String deleteReport(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        reportService.deleteReport(id);
        redirectAttributes.addFlashAttribute("successMessage", "日報を削除しました");
        return "redirect:/reports";
    }

}

