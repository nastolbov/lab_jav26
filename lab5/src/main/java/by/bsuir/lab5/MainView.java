package by.bsuir.lab5;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Лабораторная работа №5, вариант 10. Бинарные файлы.
 *
 * UI:
 *   - кнопка «Создать файл»: открывает диалог, в котором
 *     запрашивается имя файла, n (max количество чисел),
 *     a..b (диапазон значений). Создаётся целочисленный бинарный
 *     файл с количеством чисел из диапазона 1..n.
 *   - кнопка «Выбрать файл»: открывает диалог с полем ввода имени
 *     файла, после чего его содержимое выводится.
 *   - кнопка «Обработать»: применяет вариант 10 — ищет числа,
 *     состоящие только из нечётных цифр, и выводит их по
 *     возрастанию без повторов.
 *
 * Создание, выбор и обработка не обязательно идут последовательно:
 * пользователь может выбрать ранее созданный файл.
 */
@Route("")
public class MainView extends VerticalLayout {

    private final BinaryFileService service = new BinaryFileService();

    private Path     currentFile;
    private final Paragraph fileLabel  = new Paragraph("Файл не выбран");
    private final TextArea  contentBox = new TextArea("Содержимое файла");
    private final TextArea  resultBox  = new TextArea(
            "Результат (числа из нечётных цифр, по возрастанию, без повторов)");

    public MainView() {
        setPadding(true);
        setSpacing(true);

        H2 title = new H2("Лабораторная работа №5 — Бинарные файлы (вариант 10)");

        contentBox.setWidth("640px");
        contentBox.setMinHeight("120px");
        contentBox.setReadOnly(true);

        resultBox.setWidth("640px");
        resultBox.setMinHeight("80px");
        resultBox.setReadOnly(true);

        Button btnCreate  = new Button("Создать файл…",  e -> openCreateDialog());
        Button btnChoose  = new Button("Выбрать файл…",  e -> openChooseDialog());
        Button btnProcess = new Button("Обработать",     e -> process());

        add(title,
            new HorizontalLayout(btnCreate, btnChoose, btnProcess),
            fileLabel,
            contentBox,
            resultBox);
    }

    // ---------- ДИАЛОГ СОЗДАНИЯ ФАЙЛА ----------
    private void openCreateDialog() {
        Dialog dlg = new Dialog();
        dlg.setHeaderTitle("Создание бинарного файла");

        TextField fName = new TextField("Имя файла");
        fName.setValue("data/numbers.bin");
        fName.setWidth("320px");

        IntegerField n = new IntegerField("n (макс. количество чисел)");
        n.setValue(20);
        n.setMin(1);

        IntegerField a = new IntegerField("a (мин. значение)");
        a.setValue(1);

        IntegerField b = new IntegerField("b (макс. значение)");
        b.setValue(99);

        Button ok = new Button("Создать", e -> {
            try {
                Path p = Paths.get(fName.getValue().trim());
                service.create(p,
                        n.getValue() == null ? 0 : n.getValue(),
                        a.getValue() == null ? 0 : a.getValue(),
                        b.getValue() == null ? 0 : b.getValue());
                currentFile = p;
                fileLabel.setText("Текущий файл: " + p.toAbsolutePath());
                showContent();
                resultBox.setValue("");
                Notification.show("Файл создан");
                dlg.close();
            } catch (IllegalArgumentException ex) {
                Notification.show("Ошибка ввода: " + ex.getMessage());
            } catch (IOException ex) {
                Notification.show("Ошибка записи: " + ex.getMessage());
            } catch (Exception ex) {
                Notification.show("Неожиданная ошибка: " + ex.getMessage());
            }
        });
        Button cancel = new Button("Отмена", e -> dlg.close());

        VerticalLayout body = new VerticalLayout(fName, n, a, b);
        body.setPadding(false);
        dlg.add(body);
        dlg.getFooter().add(cancel, ok);
        dlg.open();
    }

    // ---------- ДИАЛОГ ВЫБОРА ФАЙЛА ----------
    private void openChooseDialog() {
        Dialog dlg = new Dialog();
        dlg.setHeaderTitle("Выбор существующего файла");

        TextField fName = new TextField("Имя файла");
        fName.setValue(currentFile == null ? "data/numbers.bin"
                                           : currentFile.toString());
        fName.setWidth("360px");

        Button ok = new Button("Открыть", e -> {
            try {
                currentFile = Paths.get(fName.getValue().trim());
                fileLabel.setText("Текущий файл: " + currentFile.toAbsolutePath());
                showContent();
                resultBox.setValue("");
                dlg.close();
            } catch (IOException ex) {
                Notification.show("Ошибка чтения: " + ex.getMessage());
            } catch (Exception ex) {
                Notification.show("Неожиданная ошибка: " + ex.getMessage());
            }
        });
        Button cancel = new Button("Отмена", e -> dlg.close());

        dlg.add(new VerticalLayout(fName));
        dlg.getFooter().add(cancel, ok);
        dlg.open();
    }

    private void showContent() throws IOException {
        int[] all = service.readAll(currentFile);
        StringBuilder sb = new StringBuilder("Всего чисел: ").append(all.length).append('\n');
        for (int i = 0; i < all.length; i++) {
            if (i > 0) sb.append(' ');
            sb.append(all[i]);
        }
        contentBox.setValue(sb.toString());
    }

    // ---------- ОБРАБОТКА (вариант 10) ----------
    private void process() {
        if (currentFile == null) {
            Notification.show("Сначала создайте или выберите файл");
            return;
        }
        try {
            TreeSet<Integer> result = service.oddDigitNumbers(currentFile);
            if (result.isEmpty()) {
                resultBox.setValue("Нет чисел, состоящих только из нечётных цифр");
            } else {
                resultBox.setValue(result.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(" ")));
            }
        } catch (IOException ex) {
            Notification.show("Ошибка чтения: " + ex.getMessage());
        } catch (Exception ex) {
            Notification.show("Неожиданная ошибка: " + ex.getMessage());
        }
    }
}
