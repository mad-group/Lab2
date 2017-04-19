package group3.myapplicationlab2;

import android.content.Intent;
import android.util.Pair;
import android.util.SparseArray;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by mc on 03/04/17.
 */

public class Purchase {
    Date date;
    String causal;
    Float total_amount;
    String author_amount;
    int author_id;
    int partition_id; //the id of partion methods
    SparseArray<Float> participants; //array having <int key, Float value> where int k is default
    int group_id;
    int purchase_id;

/*    public Purchase(int group_id, int author_id){
        this.group_id = group_id;

    }*/
    public Purchase(String author_amount, float total_amount, String causal, String date) {
        this.author_amount = author_amount;
        this.total_amount = total_amount;
        this.causal = causal;
        setDate(date);
    }

    /*return an hashmap which is formatted as
    * <purhcase ID | [(u1, amount1),(u2, amount2)]>
    * */
/*    public HashMap<Integer, SparseArray<Float>> debtisAndCreditsPerPurchase(Purchase p){
        HashMap <Integer, SparseArray<Float>> out = new HashMap<>();
        int key = p.getPurchase_id();
        SparseArray<Float> creditsAndDebits = new SparseArray<>();

        //create first element
        creditsAndDebits.put(p.author_id, p.author_amount);

        *//* Scan all participants list. Multiply -1 the values, and puts in the out list *//*
        for (int i = 0; i<p.participants.size(); i++){
            int user_key = p.participants.keyAt(i);
            float temp_amount = p.participants.get(key, (float)0);
            if (temp_amount != 0){
                creditsAndDebits.put(key, temp_amount);
            }
        }

        *//*Insertion int out hashmap of all the credits and debits situation for a given purchase*//*
        out.put(key, creditsAndDebits);

        return out;
    }*/

    public String getAuthorAmount(){return this.author_amount;}
    public float getTotalAmount() {return this.total_amount;}
    public String getCausal() {return this.causal;}

    public String getDate() {
        DateFormat df = new SimpleDateFormat("dd MMM yyy");
        return df.format(this.date);
    }

    public int getAuthorId(){
        return this.author_id;
    }

    public void setDate(String dateInString){
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        try{
            Date d = formatter.parse(dateInString);
            this.date = d;
        }
        catch(ParseException e){
            e.printStackTrace();
        }
    }

    public String setCausal(String causal){
        if (causal.length() < 3)
            return null;
        else
            this.causal = causal;
        return causal;
    }

    public int getPurchase_id(){
        return this.purchase_id;
    }



}
