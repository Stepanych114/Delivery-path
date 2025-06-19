package com.logistics.routeoptimizer;

import com.logistics.routeoptimizer.model.Order;
import com.logistics.routeoptimizer.util.GraphUtils;

import java.util.*;

public class DeliveryRouteOptimizer {
    private final int[][] distances;
    private final int n;

    public DeliveryRouteOptimizer(int[][] graph) {
        this.n = graph.length;
        this.distances = GraphUtils.initializeGraph(graph);
        GraphUtils.floydWarshall(distances);
    }

    private boolean isValidRoute(List<Integer> route, List<Order> orders) {
        int currentTime = 0;
        int currentLocation = route.get(0); 

        for (int i = 1; i < route.size(); i++) {
            int nextLocation = route.get(i);
            int travelTime = distances[currentLocation][nextLocation];
            currentTime += travelTime;

            
            for (Order order : orders) {
                if (order.getLocation() == nextLocation) {
                    if (currentTime > order.getDeadline()) {
                        return false;
                    }
                }
            }
            currentLocation = nextLocation;
        }
        return true;
    }

    
    private void permute(List<Integer> locations, int start, List<List<Integer>> permutations) {
        if (start == locations.size()) {
            permutations.add(new ArrayList<>(locations));
        } else {
            for (int i = start; i < locations.size(); i++) {
                Collections.swap(locations, start, i);
                permute(locations, start + 1, permutations);
                Collections.swap(locations, start, i);
            }
        }
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

        
        List<Integer> locations = new ArrayList<>(orderLocations);
        List<List<Integer>> permutations = new ArrayList<>();
        permute(locations, 0, permutations);

        
        for (List<Integer> perm : permutations) {
            List<Integer> route = new ArrayList<>();
            route.add(start); 
            route.addAll(perm); 

            if (isValidRoute(route, orders)) {
                return route;
            }
        }

        return null; 
    }

    public boolean isDeliveryPossible(int start, List<Order> orders) {
        return findDeliveryRoute(start, orders) != null;
    }
}