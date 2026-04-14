package by.bsuir.lab3;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

/**
 * Главный экран лабораторной работы №3, вариант 10.
 *
 * Интерфейс на Vaadin: пользователь вводит массив чисел через
 * запятую, выбирает один из вариантов обработки, результат
 * отображается на странице.
 */
@Route("")
public class MainView extends VerticalLayout {

    private final TextField input = new TextField("Числа через запятую");
    private final Paragraph original     = new Paragraph();
    private final Paragraph sortedInsert = new Paragraph();
    private final Paragraph sortedSelect = new Paragraph();
    private final Paragraph foreachInsert= new Paragraph();
    private final Paragraph foreachSelect= new Paragraph();
    private final Paragraph viaInterface = new Paragraph();

    public MainView() {
        setPadding(true);
        setSpacing(true);

        H2 title = new H2("Лабораторная работа №3 — Абстрактные классы и интерфейсы (вариант 10)");

        input.setWidth("520px");
        input.setPlaceholder("например: 5, 2, 8, 1, 4, 9, 3");
        input.setValue("5, 2, 8, 1, 4, 9, 3");

        Button btnRun = new Button("Выполнить", e -> run());

        HorizontalLayout controls = new HorizontalLayout(input, btnRun);
        controls.setAlignItems(FlexComponent.Alignment.END);

        add(title, controls,
            original,
            sortedInsert, foreachInsert,
            sortedSelect, foreachSelect,
            viaInterface);
    }

    private void run() {
        double[] arr;
        try {
            arr = parse(input.getValue());
            if (arr.length == 0) throw new IllegalArgumentException("пустой массив");
        } catch (Exception ex) {
            Notification.show("Ошибка ввода: " + ex.getMessage());
            return;
        }

        original.setText("Исходный массив: " + format(arr));

        // ----- через АБСТРАКТНЫЙ КЛАСС -----
        // Демонстрация полиморфизма: указатель базового класса
        AbstractArray ins = new Insert(arr);
        ins.sort();
        sortedInsert.setText("Insert (абстр. класс) — после сортировки вставками: "
                + format(ins.getData()));
        ins.foreachDefault();
        foreachInsert.setText("Insert.foreach (квадраты): " + format(ins.getData()));

        AbstractArray sel = new Selection(arr);
        sel.sort();
        sortedSelect.setText("Selection (абстр. класс) — после сортировки выбором: "
                + format(sel.getData()));
        sel.foreachDefault();
        foreachSelect.setText("Selection.foreach (логарифмы): " + format(sel.getData()));

        // ----- через ИНТЕРФЕЙС -----
        IArray iIns = new InsertI(arr);
        IArray iSel = new SelectionI(arr);
        iIns.sort(); iIns.foreachDefault();
        iSel.sort(); iSel.foreachDefault();
        viaInterface.setText(
                "Через интерфейс IArray:  InsertI=" + format(iIns.getData())
                + "  |  SelectionI=" + format(iSel.getData()));
    }

    private static double[] parse(String s) {
        String[] parts = s.split("[,;\\s]+");
        double[] r = new double[parts.length];
        int n = 0;
        for (String p : parts) {
            if (p.isBlank()) continue;
            r[n++] = Double.parseDouble(p.replace(',', '.'));
        }
        double[] out = new double[n];
        System.arraycopy(r, 0, out, 0, n);
        return out;
    }

    private static String format(double[] a) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < a.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(String.format("%.4f", a[i]));
        }
        return sb.append("]").toString();
    }
}
