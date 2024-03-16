package com.driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository {
    private HashMap<String,Order> orderRepo=new HashMap<>(); //OrderId--Order
    private HashMap<String,DeliveryPartner> partnerRepo=new HashMap<>(); //DeliveryPartnerId--DeliveryPartner
    private HashMap<String, ArrayList<String>> partnerOrderRepo=new HashMap<>(); //PartnerId--OrderId
    private HashMap<String,String> orderPartnerRepo=new HashMap<>(); //OrderId--PartnerId

//    @Autowired
//    public OrderRepository(){
//        orderRepo=new HashMap<>();
//        partnerRepo=new HashMap<>();
//        partnerOrderRepo=new HashMap<>();
//        orderPartnerRepo=new HashMap<>();
//    }
    public void addOrder(Order order){
        orderRepo.put(order.getId(),order);
    }
    public void addPartner(String partnerID){
        partnerRepo.put(partnerID,new DeliveryPartner(partnerID));
    }
    public void addOrderPartnerPair(String orderId, String partnerId){
        //It is expected that orderId and partnerId exists
        if(!partnerOrderRepo.containsKey(partnerId))
            partnerOrderRepo.put(partnerId,new ArrayList<>());
        partnerOrderRepo.get(partnerId).add(orderId);
        orderPartnerRepo.put(orderId,partnerId);
        DeliveryPartner dp=partnerRepo.get(partnerId);
        dp.setNumberOfOrders(dp.getNumberOfOrders()+1);
    }
    public Order getOrderById(String orderId){
        return orderRepo.getOrDefault(orderId,null);
    }
    public DeliveryPartner getPartnerById(String partnerId){
        return partnerRepo.getOrDefault(partnerId,null);
    }
    public int getOrderCountByPartnerId(String partnerId){
        int count=0;
        if(partnerOrderRepo.containsKey(partnerId))
            count=partnerOrderRepo.get(partnerId).size();
        return count;
        //Another code can also be used
//        DeliveryPartner dp=partnerRepo.get(partnerId);
//        return dp.getNumberOfOrders();

    }
    public List<String> getOrdersByPartnerId(String partnerId){
        ArrayList<String> ans=new ArrayList<>();
        if(partnerOrderRepo.containsKey(partnerId)){
            ans.addAll(partnerOrderRepo.get(partnerId));
        }
        return ans;
    }
    public List<String> getAllOrders(){
        ArrayList<String> ans=new ArrayList<>();
        ans.addAll(orderRepo.keySet());
        return ans;
    }
    public int getCountOfUnassignedOrders(){
        return orderRepo.size()-orderPartnerRepo.size();
    }
    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId){
        int count=0;
        int convertedTime=0;
        String givenTime[]=time.split(":");
        convertedTime=Integer.parseInt(givenTime[0])*60+Integer.parseInt(givenTime[1]);
        if(partnerOrderRepo.containsKey(partnerId)){
            for(String orderId : partnerOrderRepo.get(partnerId)){
                if(orderRepo.get(orderId).getDeliveryTime()>convertedTime)
                    count++;
            }
        }
        return count;
    }
    public String getLastDeliveryTimeByPartnerId(String partnerId){
        String time=null;
        if(partnerOrderRepo.containsKey(partnerId)){
            int maxTime=Integer.MIN_VALUE;
            for(String orderId:partnerOrderRepo.get(partnerId)){
                if(orderRepo.get(orderId).getDeliveryTime()>maxTime)
                    maxTime=orderRepo.get(orderId).getDeliveryTime();
            }
            if(maxTime!=Integer.MIN_VALUE){
                int hr=maxTime/60;
                int min=maxTime%60;
                if(hr>9)
                    time=hr+":";
                else{
                    time="0";
                    time=time+hr;
                    time=time+":";
                }
                if(min>9)
                    time=time+min;
                else{
                    time=time+"0";
                    time=time+min;
                }
            }
        }
        return time;
    }
    public void deletePartnerById(String partnerId){
        //Here no need to update partner's numberOfOrders
        if(partnerRepo.containsKey(partnerId)){
            partnerRepo.remove(partnerId);
            if(partnerOrderRepo.containsKey(partnerId)){
                for(String orderId:partnerOrderRepo.get(partnerId))
                    orderPartnerRepo.remove(orderId);
                partnerOrderRepo.remove(partnerId);
            }
        }
    }
    public void deleteOrderById(String orderId){
      //  if(orderRepo.containsKey(orderId)){
            orderRepo.remove(orderId);
            String partnerId=null;
            if(orderPartnerRepo.containsKey(orderId)){
                partnerId=orderPartnerRepo.get(orderId);
                DeliveryPartner dp=partnerRepo.get(partnerId);
            //    System.out.println("Before updating numberof orders "+dp);
                dp.setNumberOfOrders(dp.getNumberOfOrders()-1);
            //    System.out.println("After updating numberof orders "+dp);
                orderPartnerRepo.remove(orderId);
                ArrayList<String> temp=partnerOrderRepo.get(partnerId);
                temp.remove(orderId);
                if(temp.isEmpty())
                    partnerOrderRepo.remove(partnerId);
            }
    //    }
    }
}
