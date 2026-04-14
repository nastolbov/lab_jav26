package by.bsuir.lab4;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Лабораторная работа №4. Вариант 10 — Железная дорога.
 *
 *   - заголовок страницы помещён в одну из ячеек HorizontalLayout,
 *   - вертикальное меню (Tabs) для выбора операций,
 *   - HorizontalLayout — для расположения компонентов,
 *   - две кнопки чтения файла: классический способ
 *     (FileReader/BufferedReader) и функциональный (Files.lines()),
 *   - сообщения об исключениях отображаются на странице.
 *
 * Операции варианта 10:
 *   1) станция с наибольшим количеством проходящих поездов;
 *   2) сравнение двух станций по числу пассажирских поездов.
 */
@Route("")
public class MainView extends HorizontalLayout {

    private final TextField fileField = new TextField("Имя файла");

    private final Grid<Station> grid = new Grid<>(Station.class, false);
    private List<Station> data = List.of();

    private final Paragraph mostLoaded   = new Paragraph();
    private final Paragraph compareInfo  = new Paragraph();
    private final TextField stationA     = new TextField("Станция A");
    private final TextField stationB     = new TextField("Станция B");
    private final Paragraph errorBox     = new Paragraph();

    public MainView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // ---------- Заголовок (в ячейке HorizontalLayout) ----------
        H2 title = new H2("Лабораторная работа №4 — Железная дорога (вариант 10)");
        title.getStyle().set("margin", "0 0 12px 0").set("color", "#003366");

        // ---------- Grid ----------
        grid.addColumn(Station::getName).setHeader("Станция").setAutoWidth(true);
        grid.addColumn(Station::getPassenger).setHeader("Пассажирских").setAutoWidth(true);
        grid.addColumn(Station::getCargo).setHeader("Товарных").setAutoWidth(true);
        grid.addColumn(Station::getTotal).setHeader("Всего").setAutoWidth(true);
        grid.setAllRowsVisible(true);

        // ---------- Поле файла + способ чтения + кнопка ----------
        fileField.setWidth("420px");
        fileField.setValue("data/stations.txt");

        RadioButtonGroup<String> readMode = new RadioButtonGroup<>();
        readMode.setLabel("Способ чтения");
        readMode.setItems("FileReader / BufferedReader",
                          "Files.lines() (функциональный стиль)");
        readMode.setValue("FileReader / BufferedReader");

        Button btnLoad = new Button("Загрузить", e -> load(readMode.getValue()));

        VerticalLayout fileBox = new VerticalLayout(fileField, readMode, btnLoad, errorBox);
        fileBox.setPadding(false);
        errorBox.getStyle().set("color", "red");

        // ---------- Операции ----------
        Div opTable = new Div(grid);

        Div opMost  = new Div(mostLoaded);
        Button btnMost = new Button("Найти", e -> findMostLoaded());
        opMost.add(new Paragraph("Станция с наибольшим количеством поездов:"), btnMost);

        Div opCompare = new Div();
        Button btnCompare = new Button("Сравнить", e -> compare());
        stationA.setValue("Минск");
        stationB.setValue("Брест");
        opCompare.add(
                new Paragraph("Сравнить две станции по числу пассажирских поездов:"),
                new HorizontalLayout(stationA, stationB),
                btnCompare,
                compareInfo);

        // ---------- Вертикальное меню Tabs ----------
        Map<Tab, Component> tabsToComponents = new HashMap<>();
        Tab tab1 = new Tab("Таблица");
        Tab tab2 = new Tab("Самая загруженная");
        Tab tab3 = new Tab("Сравнение");
        tabsToComponents.put(tab1, opTable);
        tabsToComponents.put(tab2, opMost);
        tabsToComponents.put(tab3, opCompare);

        Tabs tabs = new Tabs(tab1, tab2, tab3);
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.setHeight("260px");
        tabs.setWidth("220px");

        tabs.addSelectedChangeListener(ev -> {
            tabsToComponents.values().forEach(c -> c.setVisible(false));
            tabsToComponents.get(tabs.getSelectedTab()).setVisible(true);
        });
        tabsToComponents.values().forEach(c -> c.setVisible(false));
        opTable.setVisible(true);

        Div pages = new Div(opTable, opMost, opCompare);
        pages.setWidthFull();

        // ---------- HorizontalLayout: левая ячейка с заголовком и меню,
        //            правая ячейка — содержимое страниц ----------
        VerticalLayout left = new VerticalLayout(title, fileBox, tabs);
        left.setWidth("520px");

        add(left, pages);
    }

    // ---------- ОБРАБОТЧИКИ ----------

    private void load(String mode) {
        errorBox.setText("");
        try {
            Path path = Paths.get(fileField.getValue().trim());
            if (mode.startsWith("Files.lines")) {
                data = StationReader.readFunctional(path);
            } else {
                data = StationReader.readClassic(path);
            }
            grid.setItems(data);
            mostLoaded.setText("");
            compareInfo.setText("");
            Notification.show("Загружено: " + data.size() + " строк");
        } catch (StationFormatException ex) {
            data = List.of();
            grid.setItems(data);
            errorBox.setText("Ошибка формата файла: " + ex.getMessage());
        } catch (IOException ex) {
            data = List.of();
            grid.setItems(data);
            errorBox.setText("Ошибка чтения файла: " + ex.getMessage());
        } catch (Exception ex) {
            data = List.of();
            grid.setItems(data);
            errorBox.setText("Неожиданная ошибка: " + ex.getMessage());
        }
    }

    private void findMostLoaded() {
        if (data.isEmpty()) {
            mostLoaded.setText("Сначала загрузите файл.");
            return;
        }
        Station best = data.get(0);
        for (Station s : data) {
            if (s.getTotal() > best.getTotal()) best = s;
        }
        mostLoaded.setText("Станция: " + best.getName()
                + " — всего поездов " + best.getTotal()
                + " (пасс. " + best.getPassenger() + ", тов. " + best.getCargo() + ")");
    }

    private void compare() {
        if (data.isEmpty()) {
            compareInfo.setText("Сначала загрузите файл.");
            return;
        }
        Station a = findByName(stationA.getValue());
        Station b = findByName(stationB.getValue());
        if (a == null || b == null) {
            compareInfo.setText("Не найдена одна из станций: "
                    + (a == null ? stationA.getValue() : "")
                    + (b == null ? " " + stationB.getValue() : ""));
            return;
        }
        if (a.getPassenger() == b.getPassenger()) {
            compareInfo.setText("У станций " + a.getName() + " и " + b.getName()
                    + " одинаковое число пассажирских поездов: " + a.getPassenger());
            return;
        }
        Station more = a.getPassenger() > b.getPassenger() ? a : b;
        Station less = more == a ? b : a;
        compareInfo.setText("Через станцию '" + more.getName()
                + "' проходит больше пассажирских поездов: "
                + more.getPassenger() + " против "
                + less.getPassenger() + " у '" + less.getName() + "'.");
    }

    private Station findByName(String name) {
        if (name == null) return null;
        String n = name.trim();
        for (Station s : data) {
            if (s.getName().equalsIgnoreCase(n)) return s;
        }
        return null;
    }
}
