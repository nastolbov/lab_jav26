package by.bsuir.lab3;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Лабораторная работа №3, вариант 10.
 * Точка входа Spring Boot + Vaadin.
 */
@SpringBootApplication
@Theme(value = "lab3")
public class Application implements AppShellConfigurator {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
