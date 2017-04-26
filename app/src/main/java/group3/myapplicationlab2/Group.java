package group3.myapplicationlab2;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
/**
 * Created by mc on 03/04/17.
 */

public class Group {
    private String name;
    private String description;
    private int id;
    private List<String> members;
    private FirebaseDatabase db;
    //private List<Purchase> notes;

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

    public String getDescription(){
        return this.description;
    }

    public List<String> getMembers(){
        return this.members;
    }

    public int getId(){
        return this.id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setMembers(List<String> members){
        this.members = members;
    }



}
