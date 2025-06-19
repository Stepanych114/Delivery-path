package com.logistics.routeoptimizer;

import com.logistics.routeoptimizer.model.Order;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(Arrays.asList(0, 2, 1), route);
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
}