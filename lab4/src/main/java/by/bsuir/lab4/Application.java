package by.bsuir.lab4;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Лабораторная работа №4, вариант 10 — Железная дорога.
 * Точка входа Spring Boot + Vaadin.
 */
@SpringBootApplication
@Theme(value = "lab4")
public class Application implements AppShellConfigurator {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
