package com.logistics.routeoptimizer.visualizer;

import java.util.ArrayList;
import java.util.List;

import com.logistics.routeoptimizer.DeliveryRouteOptimizer;
import com.logistics.routeoptimizer.model.Order;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class RouteVisualizer extends Application {

    private int[][] graph;
    private List<Order> orders = new ArrayList<>();
    private VBox edgeInputs = new VBox(5);
    private VBox deadlineInputs = new VBox(5);
    private TextArea resultArea = new TextArea();
    
    private Canvas graphCanvas = new Canvas(500, 400);
    private List<Integer> currentRoute = null;
    private int nodeCount;

    private VBox rootContent = new VBox(10);
    private TextField locationField;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Оптимизация маршрута доставки");
        resultArea.setPrefHeight(60);
        resultArea.setWrapText(true);
        resultArea.setEditable(false);

        Label locationsLabel = new Label("Введите количество локаций (не считая начальной):");
        locationField = new TextField();
        Button confirmLocationsBtn = new Button("Подтвердить");

        confirmLocationsBtn.setOnAction(e -> {
            try {
                int n = Integer.parseInt(locationField.getText());
                if (n < 1) {
                    showAlert("Ошибка", "Количество локаций должно быть положительным.");
                    return;
                }
                nodeCount = n;
                buildGraphInputs(n);
            } catch (NumberFormatException ex) {
                showAlert("Ошибка", "Введите корректное число.");
            }
        });

        rootContent.setPadding(new Insets(10));
        rootContent.getChildren().addAll(locationsLabel, locationField, confirmLocationsBtn);

        ScrollPane scrollPane = new ScrollPane(rootContent);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 700, 800);
        stage.setScene(scene);
        stage.show();
    }

    private void buildGraphInputs(int n) {
        graph = new int[n + 1][n + 1];
        edgeInputs.getChildren().clear();
        deadlineInputs.getChildren().clear();
        resultArea.clear();
        currentRoute = null;
        GraphicsContext gc = graphCanvas.getGraphicsContext2D();
    gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());

        edgeInputs.getChildren().add(new Label("Введите времена между локациями (в минутах):"));
        for (int i = 0; i <= n; i++) {
            for (int j = i + 1; j <= n; j++) {
                HBox edgeRow = new HBox(5);
                Label label = new Label("Время от " + i + " до " + j + ":");
                TextField timeField = new TextField();
                timeField.setUserData(new int[]{i, j});
                edgeRow.getChildren().addAll(label, timeField);
                edgeInputs.getChildren().add(edgeRow);
            }
        }

        deadlineInputs.getChildren().add(new Label("Введите дедлайны для заказов (в минутах):"));
        for (int i = 1; i <= n; i++) {
            HBox row = new HBox(5);
            Label label = new Label("Дедлайн для локации " + i + ":");
            TextField deadlineField = new TextField();
            deadlineField.setUserData(i);
            row.getChildren().addAll(label, deadlineField);
            deadlineInputs.getChildren().add(row);
        }

        Button computeRouteBtn = new Button("Построить маршрут");
        computeRouteBtn.setOnAction(e -> computeRoute(n));

        VBox visualBox = new VBox(10,
                new Separator(), edgeInputs,
                new Separator(), deadlineInputs,
                computeRouteBtn,
                resultArea);
        visualBox.setPadding(new Insets(10));
        if (rootContent.getChildren().size() > 3) {
            rootContent.getChildren().remove(3, rootContent.getChildren().size());
        }

        rootContent.getChildren().add(visualBox);
    }

    private void computeRoute(int n) {
        if (!rootContent.getChildren().contains(graphCanvas))
            rootContent.getChildren().add(graphCanvas);
        try {
            for (javafx.scene.Node node : edgeInputs.getChildren()) {
                if (node instanceof HBox) {
                    HBox row = (HBox) node;
                    for (javafx.scene.Node fieldNode : row.getChildren()) {
                        if (fieldNode instanceof TextField) {
                            TextField field = (TextField) fieldNode;
                            int[] indices = (int[]) field.getUserData();
                            int i = indices[0];
                            int j = indices[1];
                            int time = Integer.parseInt(field.getText());
                            if (time < 0) {
                                showAlert("Ошибка", "Время не может быть отрицательным.");
                                return;
                            }
                            graph[i][j] = time;
                            graph[j][i] = time;
                        }
                    }
                }
            }

            orders.clear();
            for (javafx.scene.Node node : deadlineInputs.getChildren()) {
                if (node instanceof HBox) {
                    HBox row = (HBox) node;
                    for (javafx.scene.Node fieldNode : row.getChildren()) {
                        if (fieldNode instanceof TextField) {
                            TextField field = (TextField) fieldNode;
                            int location = (int) field.getUserData();
                            int deadline = Integer.parseInt(field.getText());
                            if (deadline < 0) {
                                showAlert("Ошибка", "Дедлайн не может быть отрицательным.");
                                return;
                            }
                            orders.add(new Order(location, deadline));
                        }
                    }
                }
            }

            DeliveryRouteOptimizer optimizer = new DeliveryRouteOptimizer(graph);
            List<Integer> route = optimizer.findDeliveryRoute(0, orders);

            if (route != null) {
                resultArea.setText("Маршрут доставки: " + route);
                currentRoute = route;
            } else {
                resultArea.setText("Невозможно построить маршрут, удовлетворяющий дедлайнам.");
                currentRoute = null;
            }

            drawGraph(n);

        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Проверьте все введённые значения — они должны быть числами.");
        }
    }

    private void drawGraph(int n) {
        GraphicsContext gc = graphCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());

        double width = graphCanvas.getWidth();
        double height = graphCanvas.getHeight();

        double centerX = width / 2;
        double centerY = height / 2;
        double radius = Math.min(width, height) / 2 - 50;

        double[] xCoords = new double[n + 1];
        double[] yCoords = new double[n + 1];

        for (int i = 0; i <= n; i++) {
            double angle = 2 * Math.PI * i / (n + 1);
            xCoords[i] = centerX + radius * Math.cos(angle);
            yCoords[i] = centerY + radius * Math.sin(angle);
        }

        gc.setStroke(Color.GRAY);
        gc.setLineWidth(2);
        for (int i = 0; i <= n; i++) {
            for (int j = i + 1; j <= n; j++) {
                if (graph[i][j] > 0) {
                    gc.strokeLine(xCoords[i], yCoords[i], xCoords[j], yCoords[j]);
                }
            }
        }

        if (currentRoute != null && currentRoute.size() > 1) {
            gc.setStroke(Color.RED);
            gc.setLineWidth(3);
            for (int i = 0; i < currentRoute.size() - 1; i++) {
                int from = currentRoute.get(i);
                int to = currentRoute.get(i + 1);
                gc.strokeLine(xCoords[from], yCoords[from], xCoords[to], yCoords[to]);
            }
        }

        for (int i = 0; i <= n; i++) {
            gc.setFill(Color.BLUE);
            gc.fillOval(xCoords[i] - 10, yCoords[i] - 10, 20, 20);
            gc.setFill(Color.WHITE);
            gc.fillText(String.valueOf(i), xCoords[i] - 4, yCoords[i] + 5);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
