package group3.myapplicationlab2;


import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.security.Timestamp;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
/**
 * Created by mc on 03/04/17.
 */

public class Group implements Serializable {
    private String name;
    private String description;
    private String id;
    private List<String> members;
    private List<Purchase> purchases;
    private FirebaseDatabase db;
    private String pin;
    private long lastModifyTimeStamp;
    private Double total_amount;
    private List<Double> aggPurchases;
    private List<Double> myDebts;
    private List<GroupMember> groupMembers;

    public Group() {
    }

    public void GroupConstructor(Map<String, Object> objectHashMap){

        this.setName(objectHashMap.get("name").toString());
        this.setDescription(objectHashMap.get("description").toString());
        this.setPin(objectHashMap.get("pin").toString());
        this.setMembers((ArrayList<String>) objectHashMap.get("members"));

        List<GroupMember> groupMembers = new ArrayList<GroupMember>();
        if (objectHashMap.get("members2") != null){
            Map <String, Object> objMember = (HashMap<String, Object>) objectHashMap.get("members2");
            for (Object obj_member: objMember.values()){
                Map <String, Object> member = (Map<String, Object>)obj_member;
                GroupMember groupMember = new GroupMember();
                groupMember.setName(member.get("name").toString());
                groupMember.setEmail(member.get("email").toString());
                groupMembers.add(groupMember);
            }
            this.groupMembers = groupMembers;
        }
        List<Purchase> purchases = new ArrayList<Purchase>();
        purchases.removeAll(purchases);
        if (objectHashMap.get("purchases") != null){
            Map <String, Object> objPurchases = (HashMap<String, Object>) objectHashMap.get("purchases");
            for (Object ob: objPurchases.values()){
                Map <String, Object> purchase = (Map<String, Object>)ob;
                Purchase p = new Purchase();
                p.setAuthorName(purchase.get("authorName").toString());
                p.setUser_name(purchase.get("user_name").toString());
                p.setCausal(purchase.get("causal").toString());
                p.setDateMillis(Long.parseLong(purchase.get("dateMillis").toString()));
                p.setGroup_id(purchase.get("group_id").toString());
                p.setPathImage(purchase.get("pathImage").toString());
                p.setTotalAmount(Double.parseDouble(purchase.get("totalAmount").toString()));
                purchases.add(p);
            }
        }
        Collections.sort(purchases,Collections.<Purchase>reverseOrder());
        this.setPurchases(purchases);
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return this.description;
    }
    public void setDescription(String description){
        this.description = description;
    }

    public List<String> getMembers(){
        return this.members;
    }
    public void setMembers(List<String> members){
        this.members = members;
    }

    public String getId(){
        return this.id;
    }
    public void setId(String id){this.id = id;}

    public String getPin(){
        return this.pin;
    }
    public void setPin(String pin) {this.pin = pin;}

    public List<Purchase> getPurchases(){
        return this.purchases;
    }
    public void setPurchases(List<Purchase> purchases){
        this.purchases = purchases;
    }

    public Long getLastModifyTimeStamp(){return this.lastModifyTimeStamp;}

    public void computePaymentProportion(User user){
        String mySelf = user.getEmail();

        for (int i=0; i < this.purchases.size(); i++){
            Purchase purchase = this.purchases.get(i);
            Double toPay = new Double(0);
            toPay = purchase.getTotalAmount()/this.groupMembers.size();
            String owner = purchase.getAuthorName();
            if (owner.equals(mySelf)){
                for (int ii=0; ii<this.groupMembers.size(); ii++){
                    if(!this.groupMembers.get(ii).getEmail().equals(mySelf)){
                        this.groupMembers.get(ii).setPayment(this.groupMembers.get(ii).getPayment() + toPay);
                    }
                }
            }
            else {
                for (int ii=0; ii<this.groupMembers.size(); ii++){
                    if (this.groupMembers.get(ii).getEmail().equals(owner)){
                        this.groupMembers.get(ii).setPayment(this.groupMembers.get(ii).getPayment() - toPay);
                    }
                }
            }
        }
    }

    public void computeAggregateExpenses(User user){

        this.aggPurchases = new ArrayList<Double>();

        while(this.aggPurchases.size() < this.members.size()){
            this.aggPurchases.add(new Double(0));
        }

        for (int i = 0; i < this.purchases.size(); i++){
            Purchase purchase = this.purchases.get(i);

            Double toPay = new Double(0);
            toPay = purchase.getTotalAmount()/this.getMembers().size();

            int index = this.getMembers().indexOf(purchase.getAuthorName());

            if (purchase.getAuthorName().equals(user.getEmail())){

                for (int ii=0; ii<this.aggPurchases.size(); ii++){
                    if (ii != index){
                        this.aggPurchases.set(ii, this.aggPurchases.get(ii) + toPay);
                    }
                }

            }
            else{

                for (int ii=0; ii<this.aggPurchases.size(); ii++){
                    if (ii == index){
                        this.aggPurchases.set(ii, this.aggPurchases.get(ii) - toPay);
                    }
                }

            }

        }

        return;
    }

    public List<Double> getAggPurchases(){
        return this.aggPurchases;
    }

    public void computeTotalExpense(){
        Double total = new Double(0);

        for (int i = 0; i< this.purchases.size(); i++){
            Purchase purchase = this.purchases.get(i);
            total = total + purchase.getTotalAmount();
        }

        this.total_amount = total;
    }

    public Double getTotalAmount(){
        return this.total_amount;
    }

    public Double getTotalExpenses(List<Double> aggPurchases){
        int i;
        Double totExpenses = new Double(0);
        for(i = 0; i < aggPurchases.size(); i++)
            totExpenses = totExpenses + aggPurchases.get(i);
        return totExpenses;
    }

    public List<GroupMember> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<GroupMember> groupMembers) {
        this.groupMembers = groupMembers;
    }
}
