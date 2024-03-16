package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    private HashMap<String, Order> orderMap;
    private HashMap<String, DeliveryPartner> partnerMap;
    private HashMap<String, HashSet<String>> partnerToOrderMap;
    private HashMap<String, String> orderToPartnerMap;

    public OrderRepository() {
        this.orderMap = new HashMap<>();
        this.partnerMap = new HashMap<>();
        this.partnerToOrderMap = new HashMap<>();
        this.orderToPartnerMap = new HashMap<>();
    }

    public void saveOrder(Order order) {
        orderMap.put(order.getId(), order);
    }

    public void savePartner(String partnerId) {
        partnerMap.put(partnerId, new DeliveryPartner(partnerId));
    }

    public void saveOrderPartnerMap(String orderId, String partnerId) {
        if (!orderMap.containsKey(orderId) || !partnerMap.containsKey(partnerId)) {
            return; // Do nothing if either order or partner doesn't exist
        }
        if (!partnerToOrderMap.containsKey(partnerId)) {
            partnerToOrderMap.put(partnerId, new HashSet<>());
        }
        partnerToOrderMap.get(partnerId).add(orderId);
        orderToPartnerMap.put(orderId, partnerId);
    }

    public Order findOrderById(String orderId) {
        return orderMap.get(orderId);
    }

    public DeliveryPartner findPartnerById(String partnerId) {
        return partnerMap.get(partnerId);
    }

    public Integer findOrderCountByPartnerId(String partnerId) {
        return partnerToOrderMap.containsKey(partnerId) ? partnerToOrderMap.get(partnerId).size() : 0;
    }

    public List<String> findOrdersByPartnerId(String partnerId) {
        return new ArrayList<>(partnerToOrderMap.getOrDefault(partnerId, new HashSet<>()));
    }

    public List<String> findAllOrders() {
        return new ArrayList<>(orderMap.keySet());
    }

    public void deletePartner(String partnerId) {
        if (partnerToOrderMap.containsKey(partnerId)) {
            for (String orderId : partnerToOrderMap.get(partnerId)) {
                orderToPartnerMap.remove(orderId);
            }
            partnerToOrderMap.remove(partnerId);
        }
        partnerMap.remove(partnerId);
    }

    public void deleteOrder(String orderId) {
        String partnerId = orderToPartnerMap.get(orderId);
        if (partnerId != null) {
            partnerToOrderMap.get(partnerId).remove(orderId);
            DeliveryPartner partner = partnerMap.get(partnerId);
            if (partner != null) {
                partner.setNumberOfOrders(partner.getNumberOfOrders() - 1);
            }
            orderToPartnerMap.remove(orderId);
        }
        orderMap.remove(orderId);
    }

    public Integer findCountOfUnassignedOrders() {
        int unassignedCount = 0;
        for (String orderId : orderMap.keySet()) {
            if (!orderToPartnerMap.containsKey(orderId)) {
                unassignedCount++;
            }
        }
        return unassignedCount;
    }

    public Integer findOrdersLeftAfterGivenTimeByPartnerId(String timeString, String partnerId) {
        int givenTime = convertToMinutes(timeString);
        int count = 0;
        HashSet<String> orders = partnerToOrderMap.getOrDefault(partnerId, new HashSet<>());
        for (String orderId : orders) {
            Order order = orderMap.get(orderId);
            if (order != null && order.getDeliveryTime() > givenTime) {
                count++;
            }
        }
        return count;
    }

    public String findLastDeliveryTimeByPartnerId(String partnerId) {
        HashSet<String> orders = partnerToOrderMap.getOrDefault(partnerId, new HashSet<>());
        int lastDeliveryTime = Integer.MIN_VALUE;
        for (String orderId : orders) {
            Order order = orderMap.get(orderId);
            if (order != null && order.getDeliveryTime() > lastDeliveryTime) {
                lastDeliveryTime = order.getDeliveryTime();
            }
        }
        return convertToTime(lastDeliveryTime);
    }

    private int convertToMinutes(String timeString) {
        String[] timeComponents = timeString.split(":");
        int hours = Integer.parseInt(timeComponents[0]);
        int minutes = Integer.parseInt(timeComponents[1]);
        return hours * 60 + minutes;
    }

    private String convertToTime(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%02d:%02d", hours, mins);
    }
}
