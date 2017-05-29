package group3.myapplicationlab2;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.util.SparseArray;
import android.widget.Toast;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by mc on 03/04/17.
 */

public class Purchase implements Serializable, Comparable<Purchase> {
    private String path_image = "nopath";
    private Long date_millis;
    private String causal;
    private double total_amount;
    private String author_name;
    private String group_id;
    private String date;
    private long lastModify;
    private String user_name;
    private String author_id;
    private String purchase_id;
    private List<PurchaseContributor> contributors;
    private String encodedString;
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

    public Long getLastModify(){return this.lastModify;}
    public void setLastModify(Long lm){lastModify = lm;}
    @Override
    public int compareTo(@NonNull Purchase purchase) {
        return (int)(this.lastModify - purchase.getLastModify());
    }


    public String getUser_name() {
        return user_name;
    }
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getAuthor_id(){return this.author_id;}
    public void setAuthor_id(String author_id){this.author_id = author_id;}

    public void setContributors(List<PurchaseContributor> l){contributors = l;}
    public List<PurchaseContributor> getContributors(){return contributors;}

    public String getPurchase_id() {return purchase_id;}
    public void setPurchase_id(String pid) {purchase_id=pid;}

    public void setEncodedString(String encodedString) {
        this.encodedString = encodedString;
    }
    public String getEncodedString() {return this.encodedString;}
}
