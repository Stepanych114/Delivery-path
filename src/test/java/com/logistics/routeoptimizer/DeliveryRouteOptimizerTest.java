package com.logistics.routeoptimizer;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.logistics.routeoptimizer.model.Order;

public class DeliveryRouteOptimizerTest {

    @Test
    void testSimpleDeliveryRoute() {
        int[][] graph = {
                {0, 2, 3},
                {2, 0, 3},
                {3, 3, 0}
        };

        DeliveryRouteOptimizer optimizer = new DeliveryRouteOptimizer(graph);
        List<Order> orders = Arrays.asList(
                new Order(1, 7),
                new Order(2, 4)
        );

        List<Integer> route = optimizer.findDeliveryRoute(0, orders);
        assertNotNull(route);
        
        assertTrue(route.contains(0) && route.contains(1) && route.contains(2));
        
        int currentTime = 0;
        for (int i = 1; i < route.size(); i++) {
            currentTime += graph[route.get(i - 1)][route.get(i)];
            for (Order order : orders) {
                if (order.getLocation() == route.get(i)) {
                    assertTrue(currentTime <= order.getDeadline());
                }
            }
        }
        // Проверяем минимальную длину маршрута
        int totalDistance = 0;
        for (int i = 1; i < route.size(); i++) {
            totalDistance += graph[route.get(i - 1)][route.get(i)];
        }
        
        assertTrue(totalDistance <= 6);
    }

    @Test
    void testImpossibleDelivery() {
        int[][] graph = {
                {0, 50, 50},
                {50, 0, 50},
                {50, 50, 0}
        };

        DeliveryRouteOptimizer optimizer = new DeliveryRouteOptimizer(graph);
        List<Order> orders = Arrays.asList(
                new Order(1, 30),
                new Order(2, 30)
        );

        List<Integer> route = optimizer.findDeliveryRoute(0, orders);
        assertNull(route);
    }

    @Test
    void testEmptyOrders() {
        int[][] graph = {{0, 10}, {10, 0}};
        DeliveryRouteOptimizer optimizer = new DeliveryRouteOptimizer(graph);
        List<Order> orders = List.of();
        List<Integer> route = optimizer.findDeliveryRoute(0, orders);
        assertEquals(List.of(0), route);
    }

    @Test
    void testInvalidStartLocation() {
        int[][] graph = {{0, 10}, {10, 0}};
        DeliveryRouteOptimizer optimizer = new DeliveryRouteOptimizer(graph);
        List<Order> orders = List.of(new Order(1, 30));
        assertThrows(IllegalArgumentException.class, () -> optimizer.findDeliveryRoute(2, orders));
    }

    @Test
    void testInvalidOrderLocation() {
        int[][] graph = {{0, 10}, {10, 0}};
        DeliveryRouteOptimizer optimizer = new DeliveryRouteOptimizer(graph);
        List<Order> orders = List.of(new Order(2, 30));
        assertThrows(IllegalArgumentException.class, () -> optimizer.findDeliveryRoute(0, orders));
    }

    @Test
    void testMultipleOrdersSameLocation() {
        int[][] graph = {
                {0, 2, 3},
                {2, 0, 3},
                {3, 3, 0}
        };

        DeliveryRouteOptimizer optimizer = new DeliveryRouteOptimizer(graph);
        List<Order> orders = Arrays.asList(
                new Order(1, 5),
                new Order(1, 7),
                new Order(2, 10)
        );

        List<Integer> route = optimizer.findDeliveryRoute(0, orders);
        assertNotNull(route);
        assertTrue(route.contains(0) && route.contains(1) && route.contains(2));
        int currentTime = 0;
        for (int i = 1; i < route.size(); i++) {
            currentTime += graph[route.get(i - 1)][route.get(i)];
            for (Order order : orders) {
                if (order.getLocation() == route.get(i)) {
                    assertTrue(currentTime <= order.getDeadline());
                }
            }
        }
        int totalDistance = 0;
        for (int i = 1; i < route.size(); i++) {
            totalDistance += graph[route.get(i - 1)][route.get(i)];
        }
        assertTrue(totalDistance <= 5);
    }
    @Test
    void testMinimalDistanceMultipleValidRoutes() {
        int[][] graph = {
                {0, 1, 10, 10},
                {1, 0, 1, 10},
                {10, 1, 0, 1},
                {10, 10, 1, 0}
        };

        DeliveryRouteOptimizer optimizer = new DeliveryRouteOptimizer(graph);
        List<Order> orders = Arrays.asList(
                new Order(1, 100),
                new Order(2, 100),
                new Order(3, 100)
        );

        List<Integer> route = optimizer.findDeliveryRoute(0, orders);
        assertNotNull(route);
        assertTrue(route.contains(0) && route.contains(1) && route.contains(2) && route.contains(3));
        int totalDistance = 0;
        for (int i = 1; i < route.size(); i++) {
            totalDistance += graph[route.get(i - 1)][route.get(i)];
        }
        // Проверяем, что маршрут [0,1,2,3] или [0,1,3,2] с длиной 3 выбран, а не [0,2,1,3] с длиной 21
        assertTrue(totalDistance <= 3);
    }

    @Test
    void testLargeNumberOfLocations() {
        int n = 10;
        int[][] graph = new int[n + 1][n + 1];
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= n; j++) {
                graph[i][j] = (i == j) ? 0 : 10;
            }
        }

        List<Order> orders = Arrays.asList(
                new Order(1, 100),
                new Order(2, 100),
                new Order(3, 100),
                new Order(4, 100),
                new Order(5, 100),
                new Order(6, 100),
                new Order(7, 100),
                new Order(8, 100),
                new Order(9, 100),
                new Order(10, 100)
        );

        DeliveryRouteOptimizer optimizer = new DeliveryRouteOptimizer(graph);
        List<Integer> route = optimizer.findDeliveryRoute(0, orders);
        assertNotNull(route);
        assertEquals(n + 1, route.size());
        for (int i = 1; i <= n; i++) {
            assertTrue(route.contains(i));
        }
        int currentTime = 0;
        for (int i = 1; i < route.size(); i++) {
            currentTime += graph[route.get(i - 1)][route.get(i)];
            for (Order order : orders) {
                if (order.getLocation() == route.get(i)) {
                    assertTrue(currentTime <= order.getDeadline());
                }
            }
        }
    }
}