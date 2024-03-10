package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public String list(Model model,@AuthenticationPrincipal UserDetail userDetail) {
        List<Report> reportList;
        if(userDetail.getEmployee().getRole() == Employee.Role.ADMIN) {
            reportList = reportService.findAllReports();

        }
        else {
            reportList = reportService.findByEmployee(userDetail.getEmployee());
        }

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
    public String add(@ModelAttribute("report") @Valid Report report, BindingResult res, RedirectAttributes redirectAttributes,Model model,@AuthenticationPrincipal UserDetail userDetail) {
        if (res.hasErrors()) {
            return create(report, model ,userDetail);
        }
        report.setEmployee(userDetail.getEmployee()); // ログイン中の従業員情報をセット
        ErrorKinds result = reportService.save(report);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return create(report, model ,userDetail);
        }
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
    //日報詳細画面から日報更新画面を表示するメソッド//テキストをみてURLを修正する。
    @GetMapping("/{id}/update")
    public String edit(@PathVariable("id") Integer id, Model model ,Report report) {

        if (id!=null) {
            report = reportService.findReportById(id);
        }
        model.addAttribute("report", report);
        return "reports/update";
    }


    @PostMapping(value = "/{id}/update")
    public String update(@PathVariable Integer id, @ModelAttribute("report") @Validated Report report,
            BindingResult res, RedirectAttributes redirectAttributes ,Model model,@AuthenticationPrincipal UserDetail userDetail) {
        report.setEmployee(userDetail.getEmployee());

        if (res.hasErrors()) {
            return edit(null, model, report);
        }

        // 更新処理を行う前に日付の重複チェックを行う
        ErrorKinds result = reportService.update(report);

        if (ErrorMessage.contains(result)) {
            // エラーメッセージが発生した場合、エラーメッセージをモデルに追加し、編集画面に戻る
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return edit(null, model, report);
        }
        // 更新が成功した場合、日報一覧画面にリダイレクトする
        return "redirect:/reports";
    }

//    // 従業員更新処理//修正3.5
//    @PostMapping(value = "/{id}/update")
//    public String update(@PathVariable Integer id, @ModelAttribute("report") @Validated Report report,
//            BindingResult res, RedirectAttributes redirectAttributes ,Model model,@AuthenticationPrincipal UserDetail userDetail) {
//        report.setEmployee(userDetail.getEmployee());
//        //修正前
//        if (res.hasErrors()) {
//            return edit(null, model, report);
//        }
//            //EmployeeServiceのupdateメソッドの呼び出し
//            ErrorKinds result = reportService.update(report);
//
//            if (ErrorMessage.contains(result)) {
//                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
//                return edit(null, model, report);
//
//            }
//
//     //更新画面から詳細画面呼び出し
//        return "redirect:/reports";
//    }

    // 日報削除処理
    @PostMapping("/{id}/delete")
    public String deleteReport(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        reportService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "日報を削除しました");
        return "redirect:/reports";
    }

    @GetMapping("hoge")
    public String listReports(Model model, @AuthenticationPrincipal UserDetail userDetail) {
        List<Report> reportList;
        if (userDetail.getEmployee().getRole() == Employee.Role.GENERAL) {
            // 一般権限のユーザーの場合、自分が登録した日報情報のみを表示
            reportList = reportService.findByEmployee(userDetail.getEmployee());
        } else {
            // 管理者権限のユーザーの場合、他の従業員が登録したものを含めた全日報情報を表示
            reportList = reportService.findAllReports();
        }
        model.addAttribute("reportList", reportList);
        model.addAttribute("listSize", reportList.size());
        return "reports/list";
    }

    }
