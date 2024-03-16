package com.driver;

public class Order {

    private String id;
    private int deliveryTime;

    public Order(String id, String deliveryTime) {
        this.id = id;
        // Convert delivery time to minutes since midnight
        String[] timeComponents = deliveryTime.split(":");
        int hours = Integer.parseInt(timeComponents[0]);
        int minutes = Integer.parseInt(timeComponents[1]);
        this.deliveryTime = hours * 60 + minutes;
    }

    public String getId() {
        return id;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }
}
