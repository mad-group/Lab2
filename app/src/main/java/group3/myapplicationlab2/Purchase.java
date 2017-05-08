package group3.myapplicationlab2;

import android.content.Intent;
import android.net.Uri;
import android.util.Pair;
import android.util.SparseArray;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by mc on 03/04/17.
 */

public class Purchase {
    private String path_image = "nopath";
    private Long date_millis;
    private String causal;
    private double total_amount;
    private String author_name;
    private String group_id;
    private String date;
    //private String author_id;
    //private int partition_id; //the id of partion methods
    //private SparseArray<Float> participants; //array having <int key, Float value> where int k is default
    //private String purchase_id;

    public Purchase(){
    }

/*
    public Purchase(String author_name, float total_amount, String causal, Long date) {
        this.author_name = author_name;
        this.total_amount = total_amount;
        this.causal = causal;
        this.date_millis = date;
    }
*/

    public String getAuthorName(){return this.author_name;}
    public void setAuthorName(String an){this.author_name = an;}

    public double getTotalAmount() {return this.total_amount;}
    public void setTotalAmount(double ta){this.total_amount = ta;}

    public String getCausal() {return this.causal;}
    public void setCausal(String causal){this.causal = causal;}

    public Long getDateMillis() {
        return this.date_millis;
    }
    public String getDate(){
        return this.date;
    }

    private void setDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        Date d = new Date();
        d.setTime(this.date_millis);
        this.date =  sdf.format(d);
    }
    public void setDateMillis(Long dateInMillis){
        this.date_millis = dateInMillis;
        setDate();
    }

    public String getPathImage(){return this.path_image;}
    public void setPathImage(String path){this.path_image = path;}

    public void setGroup_id(String group_id){this.group_id = group_id;}
    public String getGroup_id(){return this.group_id;}




/*
    public String getAuthorId(){return this.author_id;}
    public String getPurchase_id(){return this.purchase_id;}
*/
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

}
