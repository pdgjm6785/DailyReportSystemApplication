package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

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

    @GetMapping("/add")
    public String create(@ModelAttribute Report report, Model model, @AuthenticationPrincipal UserDetail userDetail ) {
        report.setEmployee(userDetail.getEmployee()); // ログイン中の従業員情報をセット
        model.addAttribute("report", report);
        return "reports/new";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("report") @Valid Report report, BindingResult result, RedirectAttributes redirectAttributes,Model model,@AuthenticationPrincipal UserDetail userDetail) {
        if (result.hasErrors()) {
            return create(report, model ,userDetail);
        }
        report.setEmployee(userDetail.getEmployee()); // ログイン中の従業員情報をセット
        reportService.save(report); // レポートを保存または更新
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
    //3.2 2000追加
    // 日報詳細画面から日報更新画面を表示するメソッド
    @GetMapping("/{id}/update")
    public String updateReportForm(@PathVariable ("id")Integer id, Model model) {
        Report report = reportService.findReportById(id);
        model.addAttribute("report", report);
        return "reports/update";
    }

    // 日報削除処理
    @PostMapping("/{id}/delete")
    public String deleteReport(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        reportService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "日報を削除しました");
        return "redirect:/reports";
    }

}