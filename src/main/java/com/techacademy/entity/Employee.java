package com.techacademy.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
@Entity
@Table(name = "employees")
public class Employee {

    public enum Role {
        GENERAL("一般"), ADMIN("管理者");

        private String name;

        private Role(String name) {
            this.name = name;
        }

        public String getValue() {
            return this.name;
        }
    }

    @Id
    @Column(length = 10)
    @NotEmpty
    @Length(max = 10)
    private String code;

    @Column(length = 20, nullable = false)
    @NotEmpty
    @Length(max = 20)
    private String name;

    @Column(columnDefinition = "VARCHAR(10)", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(columnDefinition = "TINYINT", nullable = false)
    private boolean deleteFlg;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Report> reportList; // 日報エンティティを参照するフィールド

    public void deleteByEmployee(Employee employee) {
        // TODO 自動生成されたメソッド・スタブ

    }

}