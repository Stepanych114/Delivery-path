package com.logistics.routeoptimizer;

import com.logistics.routeoptimizer.model.Order;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите количество локаций доставки (без начальной локации):");
        int n = scanner.nextInt();
        if (n < 1) {
            System.out.println("Ошибка: количество локаций доставки должно быть положительным!");
            return;
        }

        int[][] graph = new int[n+1][n+1];
        for (int i = 0; i < n+1; i++) {
            graph[i][i] = 0;
        }

        for (int i = 0; i < n+1; i++) {
            for (int j = i + 1; j < n+1; j++) {
                System.out.println("Введите время (в минутах) от локации " + i + " до локации " + j + ":");
                int time = scanner.nextInt();
                if (time < 0) {
                    System.out.println("Ошибка: время не может быть отрицательным!");
                    return;
                }
                graph[i][j] = time;
                graph[j][i] = time;
            }
        }
        for(int i = 0;i<3;i++)
        {
            for(int j = 0;j<3;j++)
            {
                System.out.print(graph[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println("Количество заказов: " + n);

        List<Order> orders = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            System.out.println("Дедлайн для локации " + i + " (в минутах):");
            int deadline = scanner.nextInt();
            if (deadline < 0) {
                System.out.println("Ошибка: дедлайн не может быть отрицательным!");
                return;
            }
            orders.add(new Order(i, deadline));
        }

        int start = 0;

        DeliveryRouteOptimizer optimizer = new DeliveryRouteOptimizer(graph);

        List<Integer> route = optimizer.findDeliveryRoute(start, orders);
        if (route != null) {
            System.out.println("Маршрут доставки: " + route);
        } else {
            System.out.println("Невозможно построить маршрут, удовлетворяющий всем дедлайнам.");
        }

        scanner.close();
    }
}