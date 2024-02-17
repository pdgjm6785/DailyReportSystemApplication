package com.techacademy.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "report_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd") // 日付のフォーマットを指定
    @NotNull(message = "日付を入力してください")
    private LocalDate reportDate;

    @Column(name = "title", length = 100, nullable = false)
    @NotBlank(message = "タイトルを入力してください")
    @Size(max = 100, message = "100文字以下で入力してください")
    private String title;

    @Column(name = "content", columnDefinition = "LONGTEXT", nullable = false)
    @NotBlank(message = "内容を入力してください")
    @Size(max = 600, message = "600文字以下で入力してください")
    private String content;

    @Column(name = "delete_flg", columnDefinition = "TINYINT", nullable = false)
    private boolean deleteFlg;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_code", referencedColumnName = "code", nullable = false)
    private Employee employee;

    @Column(name = "password")
    @NotBlank(message = "パスワードを入力してください")
    @Size(min = 8, max = 16, message = "8文字以上16文字以下で入力してください")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "パスワードは半角英数字のみで入力してください")
    private String password;
}