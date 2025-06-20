package com.logistics.routeoptimizer.model;

public class Order {
    private final int location; 
    private final int deadline; 

    public Order(int location, int deadline) {
        if (location < 0) {
            throw new IllegalArgumentException("Индекс локации не может быть отрицательным");
        }
        if (deadline < 0) {
            throw new IllegalArgumentException("Дедлайн не может быть отрицательным");
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