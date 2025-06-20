package com.logistics.routeoptimizer;

import com.logistics.routeoptimizer.model.Order;
import com.logistics.routeoptimizer.util.GraphUtils;

import java.util.*;

public class DeliveryRouteOptimizer {
    private final int[][] distances;
    private final int n;
    private Map<Integer, List<Order>> locationToOrders; 
    private long[][] dp; 
    private int[][] next; 
    private static final long INF = Long.MAX_VALUE / 2;
    public DeliveryRouteOptimizer(int[][] graph) {
        this.n = graph.length;
        this.distances = GraphUtils.initializeGraph(graph);
        GraphUtils.floydWarshall(distances);
    }

    
    private boolean isValidVisit(int nextLocation, long currentTime) {
        List<Order> orders = locationToOrders.getOrDefault(nextLocation, Collections.emptyList());
        for (Order order : orders) {
            if (currentTime > order.getDeadline()) {
                return false;
            }
        }
        return true;
    }

    private void prepareLocationToOrders(List<Order> orders) {
        locationToOrders = new HashMap<>();
        for (Order order : orders) {
            locationToOrders.computeIfAbsent(order.getLocation(), k -> new ArrayList<>()).add(order);
        }
    }

   
    private long dpSolve(int current, int visited, List<Integer> locations, long currentTime) {
        int k = locations.size();
        if (visited == (1 << k) - 1) { 
            return 0;
        }

        if (dp[current][visited] != -1) {
            return dp[current][visited];
        }

        long minDistance = INF;
        int minNext = -1;

        for (int i = 0; i < k; i++) {
            if ((visited & (1 << i)) == 0) {
                int nextLocation = locations.get(i);
                long travelDistance = distances[current][nextLocation];
                long newTime = currentTime + travelDistance;

                if (isValidVisit(nextLocation, newTime)) {
                    long result = dpSolve(nextLocation, visited | (1 << i), locations, newTime);
                    if (result != INF && travelDistance + result < minDistance) {
                        minDistance = travelDistance + result;
                        minNext = i;
                    }
                }
            }
        }

        dp[current][visited] = minDistance;
        next[current][visited] = minNext;
        return minDistance;
    }

    private List<Integer> reconstructPath(int start, List<Integer> locations) {
        List<Integer> route = new ArrayList<>();
        route.add(start);
        int current = start;
        int visited = 0;
        long currentTime = 0;

        while (visited != (1 << locations.size()) - 1) {
            int nextIdx = next[current][visited];
            if (nextIdx == -1) {
                return null; 
            }
            int nextLocation = locations.get(nextIdx);
            route.add(nextLocation);
            currentTime += distances[current][nextLocation];
            current = nextLocation;
            visited |= (1 << nextIdx);
        }

        return route;
    }

    public List<Integer> findDeliveryRoute(int start, List<Order> orders) {
        if (start < 0 || start >= n) {
            throw new IllegalArgumentException("Некорректная начальная локация");
        }
        if (orders == null || orders.isEmpty()) {
            return Collections.singletonList(start);
        }

        Set<Integer> orderLocations = new HashSet<>();
        for (Order order : orders) {
            if (order.getLocation() >= n) {
                throw new IllegalArgumentException("Некорректная локация заказа: " + order.getLocation());
            }
            orderLocations.add(order.getLocation());
        }
        prepareLocationToOrders(orders);

        List<Integer> locations = new ArrayList<>(orderLocations);
        int k = locations.size();
        dp = new long[n][1 << k];
        next = new int[n][1 << k];
        for (long[] row : dp) {
            Arrays.fill(row, -1);
        }
        for (int[] row : next) {
            Arrays.fill(row, -1);
        }

        long minDistance = dpSolve(start,0, locations,0);
        if (minDistance == INF) {
            return null;
        }
        return reconstructPath(start, locations);
    }

    public boolean isDeliveryPossible(int start, List<Order> orders) {
        return findDeliveryRoute(start, orders) != null;
    }
}