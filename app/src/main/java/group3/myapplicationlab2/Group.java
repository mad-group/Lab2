package group3.myapplicationlab2;


import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
/**
 * Created by mc on 03/04/17.
 */

public class Group {
    private String name;
    private String description;
    private int id;
    private String[] members; //composed by membersid, not "members object"
    private List<Purchase> notes;

    public Group(String name, int id, String[] members, String description){
        this.name = name;
        this.id =  id;
        this.members = members;
        this.description = description;
    }

    public String getName(){
        return this.name;
    }

    public String getDescription(){
        return this.description;
    }

    public String[] getMembers(){
        return this.members;
    }

}
