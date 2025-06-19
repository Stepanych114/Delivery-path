package com.logistics.routeoptimizer.model;

public class Order {
    private final int location; 
    private final int deadline; 

    public Order(int location, int deadline) {
        if (location < 0) {
            throw new IllegalArgumentException("Location index cannot be negative");
        }
        if (deadline < 0) {
            throw new IllegalArgumentException("Deadline cannot be negative");
        }
        this.location = location;
        this.deadline = deadline;
    }

    public int getLocation() {
        return location;
    }

    public int getDeadline() {
        return deadline;
    }
}