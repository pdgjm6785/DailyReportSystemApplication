package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;

import com.techacademy.entity.Employee;
import com.techacademy.service.EmployeeService;
import com.techacademy.service.UserDetail;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

 // 従業員一覧画面
    @GetMapping
    public String list(Model model) {
        List<Employee> employeeList = employeeService.findAll(); // 従業員リストを取得
        int listSize = employeeList.size(); // 従業員リストのサイズを取得
        model.addAttribute("listSize", listSize); // 従業員リストのサイズを追加
        model.addAttribute("employeeList", employeeList); // 従業員リストを追加
        return "employees/list";
    }

    // 従業員詳細画面
    @GetMapping(value = "/{code}/")
    public String detail(@PathVariable String code, Model model) {

        model.addAttribute("employee");
        return "employees/detail";
    }

    // 従業員新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Employee employee) {

        return "employees/new";
    }

    // 従業員新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Employee employee, BindingResult res, Model model) {

        // パスワード空白チェック
        /*
         * エンティティ側の入力チェックでも実装は行えるが、更新の方でパスワードが空白でもチェックエラーを出さずに
         * 更新出来る仕様となっているため上記を考慮した場合に別でエラーメッセージを出す方法が簡単だと判断
         */
        if ("".equals(employee.getPassword())) {
            // パスワードが空白だった場合
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.BLANK_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.BLANK_ERROR));

            return create(employee);

        }

        // 入力チェック
        if (res.hasErrors()) {
            return create(employee);
        }

        // 論理削除を行った従業員番号を指定すると例外となるためtry~catchで対応
        // (findByIdでは削除フラグがTRUEのデータが取得出来ないため)
        try {
            ErrorKinds result = employeeService.save(employee);

            if (ErrorMessage.contains(result)) {
                model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
                return create(employee);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return create(employee);
        }

        return "redirect:/employees";
    }

    // （chapter6課題作成ここから1.28〜30）//従業員更新画面〜〜〜〜〜〜〜〜〜〜〜〜〜〜〜
    // 従業員更新画面
    @GetMapping(value = "/{code}/update")
    public String edit(@PathVariable String code, Model model) {
        Employee employee = employeeService.findByCode(code);
        model.addAttribute("employee", employee);
        return "employees/update"; // 遷移先のビュー名を返す
    }


    // 従業員更新処理
    @PostMapping(value = "/{code}/update")
    public String update(@PathVariable String code, @Validated Employee employee, BindingResult res, Model model) {
        // パスワードのバリデーション
        if (employee.getPassword() != null && !isValidPassword(employee.getPassword())) {
        model.addAttribute("passwordError", "パスワードは半角英数字のみで8文字以上16文字以下で入力してください");
            return edit(code, model);
        }

        if (res.hasErrors()) {
            return edit(code, model);
        }

        try {
            //EmployeeServiceのupdateメソッドの呼び出し
            ErrorKinds result = employeeService.update(code, employee);

            if (ErrorMessage.contains(result)) {
                model.addAttribute("updateError", ErrorMessage.getErrorValue(result));
                return edit(code, model);
            }

        } catch (DataIntegrityViolationException e) {
            model.addAttribute("updateError", ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
            return edit(code, model);
        }

        return "redirect:/employees/" + code + "/";
    }

    // パスワードのバリデーション
    private static final String PASSWORD_PATTERN = "^[a-zA-Z0-9]{8,16}$";

    private static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            // パスワードが空の場合はバリデーション成功
            return true;
        }

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    // （chapter6課題作成ここまで）〜〜〜〜〜〜〜〜〜〜〜〜〜〜〜

    // 従業員削除処理
    @PostMapping(value = "/{code}/delete")
    public String delete(@PathVariable String code, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        ErrorKinds result = employeeService.delete(code, userDetail);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("employee", employeeService.findByCode(code));
            return detail(code, model);
        }

        return "redirect:/employees";
    }

}