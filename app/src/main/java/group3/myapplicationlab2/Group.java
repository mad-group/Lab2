package group3.myapplicationlab2;


import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.*;

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
    private String lastEvent;
    private String lastAuthor;
    private Double total_amount;
    private List<Double> aggPurchases;
    private List<Double> myDebts;
    private List<GroupMember> groupMembers;
    private List<String> userKeys;


    public Group() {
    }

    public void GroupConstructor(Map<String, Object> objectHashMap){

        this.setName(objectHashMap.get("name").toString());
        this.setDescription(objectHashMap.get("description").toString());
        this.setPin(objectHashMap.get("pin").toString());
        this.setMembers((ArrayList<String>) objectHashMap.get("members"));
        this.setLastAuthor(objectHashMap.get("lastAuthor").toString());
        this.setLastEvent(objectHashMap.get("lastEvent").toString());
        this.setId(objectHashMap.get("id").toString());

        List<GroupMember> groupMembers = new ArrayList<GroupMember>();
        if (objectHashMap.get("members2") != null){
            Map <String, Object> objMember = (HashMap<String, Object>) objectHashMap.get("members2");
            for (Object obj_member: objMember.values()){
                Map <String, Object> member = (Map<String, Object>)obj_member;
                GroupMember groupMember = new GroupMember();
                groupMember.setName(member.get("name").toString());
                groupMember.setEmail(member.get("email").toString());
                groupMember.setUser_id(member.get("user_id").toString());
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
                p.setAuthorName(purchase.get("author_id").toString());
                p.setUser_name(purchase.get("user_name").toString());
                p.setCausal(purchase.get("causal").toString());
                p.setDateMillis(Long.parseLong(purchase.get("dateMillis").toString()));
                p.setGroup_id(purchase.get("group_id").toString());
                p.setPathImage(purchase.get("pathImage").toString());
                p.setEncodedString(purchase.get("encodedString").toString());
                p.setTotalAmount(Double.parseDouble(purchase.get("totalAmount").toString()));
                p.setPurchase_id(purchase.get("purchase_id").toString());

                List<PurchaseContributor> pc_list = new ArrayList<>();
                pc_list.removeAll(pc_list);
                if (purchase.get("contributors")!=null){
                    Map<String, Object> mapPC = (HashMap<String, Object>) purchase.get("contributors");
                    for (Object ob2 : mapPC.values()){
                        PurchaseContributor pc = new PurchaseContributor();
                        Map<String,Object> pcFromMap =  (Map<String,Object>) ob2;
                        pc.setPayed(Boolean.parseBoolean(pcFromMap.get("payed").toString()));
                        pc.setUser_id(pcFromMap.get("user_id").toString());
                        pc.setAmount(Double.parseDouble(pcFromMap.get("amount").toString()));
                        pc.setUser_name(pcFromMap.get("user_name").toString());
                        //pc.setContributor_id(pcFromMap.get("contributor_id").toString());
                        pc_list.add(pc);
                    }
                    p.setContributors(pc_list);
                }
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

    public void resetPaymentProportion(){
        for (int ii=0; ii<this.getGroupMembers().size(); ii++){
            this.groupMembers.get(ii).setPayment(new Double(0));
        }
        return;
    }

    public void computePaymentProportion(User user){
        String mySelf = user.getUid();

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

    public void computePaymentProportionContributors(User user){
        String myself = user.getUid();

        for (int i=0; i<this.purchases.size(); i++){
            Purchase purchase = this.purchases.get(i);
            String owner = purchase.getAuthorName();

            List<GroupMember> backup = this.groupMembers;
            Boolean present = false;

            for (PurchaseContributor purchaseContributor: purchase.getContributors()){
                Log.d("NAME", purchaseContributor.getUser_id());
                Log.d("SOLDI", String.valueOf(purchaseContributor.getAmount()));
            }

            if (purchase.getContributors().size()>1){

                Log.d("UYES", "YES");

                for (PurchaseContributor purchaseContributor: purchase.getContributors()){


                    if (owner.equals(myself)){

                        if (!purchaseContributor.getUser_id().equals(myself)){

                            for (int ii=0; ii<this.groupMembers.size(); ii++){
                                if(this.groupMembers.get(ii).getUser_id().equals(purchaseContributor.getUser_id())){
                                    this.groupMembers.get(ii).setPayment(this.groupMembers.get(ii).getPayment() + purchaseContributor.getAmount());
                                }
                            }

                        }

                    }
                    else {

                        if (!purchaseContributor.getUser_id().equals(myself)){
                            for (int ii=0; ii<this.groupMembers.size(); ii++){
                                if(this.groupMembers.get(ii).getUser_id().equals(purchaseContributor.getUser_id())){
                                    this.groupMembers.get(ii).setPayment(this.groupMembers.get(ii).getPayment() - purchaseContributor.getAmount());
                                }
                            }
                        }

                    }

                    if (purchaseContributor.getUser_id().equals(myself)){
                        present = true;
                    }

                }

                if (!present){
                    this.groupMembers = backup;
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

    public String getLastEvent() {
        return lastEvent;
    }

    public void setLastEvent(String lastEvent) {
        this.lastEvent = lastEvent;
    }

    public String getLastAuthor() {
        return lastAuthor;
    }

    public void setLastAuthor(String lastAuthor) {
        this.lastAuthor = lastAuthor;
    }
}
