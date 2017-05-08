package group3.myapplicationlab2;


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
}
