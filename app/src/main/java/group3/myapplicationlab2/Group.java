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


    public Group() {
        //this.db = db;
    }

    /*public Group(String name, int id,  List<String> members, String description){
        this.name = name;
        this.id =  id;
        this.members = members;
        this.description = description;
    }*/

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
/*    public void setLastModifyTimeStamp(long lmts) {
        this.lastModifyTimeStamp = System.currentTimeMillis();
    }*/

    public void computeAggregateExpenses(User user){

        this.aggPurchases = new ArrayList<Double>();

        while(this.aggPurchases.size() < this.members.size()){
            this.aggPurchases.add(new Double(0));
        }

        /*for (int i = 0; i< this.purchases.size(); i++){
            Purchase purchase = this.purchases.get(i);

            Double toPay = new Double(0);
            toPay = purchase.getTotalAmount()/this.getMembers().size();

            int index = this.getMembers().indexOf(purchase.getAuthorName());

            Log.d("DEBAASV", this.aggPurchases.toString());

            for (int ii=0; ii<this.aggPurchases.size(); ii++){

                if (ii==index){
                    this.aggPurchases.set(ii, this.aggPurchases.get(ii) + purchase.getTotalAmount() - toPay);
                }
                else{
                    this.aggPurchases.set(ii, this.aggPurchases.get(ii) - toPay);
                }
            }
        }*/


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

        //Log.d("DEBUASFA", aggPurchases.get(0).toString());
        //Log.d("DEBUASFA", aggPurchases.get(1).toString());
        //Log.d("DEBUASFA", aggPurchases.get(2).toString());


        /*for (int i = 0; i< this.purchases.size(); i++){
            Purchase purchase = this.purchases.get(i);
            int index = this.members.indexOf(purchase.getAuthorName());
            Double amount = aggPurchases.get(index) + purchase.getTotalAmount();
            aggPurchases.set(index, amount);
        }*/

       /* for (int i = 0; i<this.members.size(); i++) {
            Log.d("Spesa di ", this.members.get(i));
            Log.d("Amount ", aggPurchases.get(i).toString());
        }*/
        return;
    }

    public List<Double> getAggPurchases(){
        return this.aggPurchases;
    }

    public void computeTotalExpense(){
        Double total = new Double(0);

        for (int i = 0; i< this.purchases.size(); i++){
            Purchase purchase = this.purchases.get(i);
            //int index = this.members.indexOf(purchase.getAuthorName());
            total = total + purchase.getTotalAmount();
            //Double amount = aggPurchases.get(index) + purchase.getTotalAmount();
            //aggPurchases.set(index, amount);
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

    /*public List<Double> getDebtsEqualSplit() {
        List<Double> aggPurchases = this.aggregateExpenses();
        Double totalPurchases = this.getTotalExpenses(aggPurchases);
        //float quotaPerEach = totalPurchases/this.members.size();
        List<Double> debtsAndCredits = new ArrayList<Double>();
        while(debtsAndCredits.size() < this.members.size()){
            debtsAndCredits.add(new Double(0));
        }

//        for (int i = 0; i < aggPurchases.size(); i++){
//            debtsAndCredits.set(i, (quotaPerEach - aggPurchases.get(i)));
//        }

        for (int i = 0; i < aggPurchases.size(); i++){
            Double quotaPerEachPerExpenses = aggPurchases.get(i)/this.members.size();
            for (int j = 0; j < this.members.size(); j++){
                if (i!=j)
                    debtsAndCredits.set(j, debtsAndCredits.get(j) - quotaPerEachPerExpenses);
            }
            debtsAndCredits.set(i, debtsAndCredits.get(i) + quotaPerEachPerExpenses);
        }

        for (int i = 0; i<this.members.size(); i++) {
            Log.d("Spesa di ", this.members.get(i));
            Log.d("Amount ", aggPurchases.get(i).toString());
        }
        return aggPurchases;
    }*/

}
