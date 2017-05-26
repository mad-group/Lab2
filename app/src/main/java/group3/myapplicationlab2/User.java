package group3.myapplicationlab2;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mc on 26/04/17.
 */

public class User implements Serializable {
    private String name;
    private String email;
    private String uid;
    private List<GroupPreview> groups;
    private HashMap<String, Notification> notificationHashMap;

    public void setEmail(String email){
        this.email = email;
    }
    public String getEmail(){return email;}

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<GroupPreview> getGroups(){
        return this.groups;
    }
    public void setGroups(List<GroupPreview> groups){
        this.groups = groups;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Notification> getNotificationHashMap() {
        return notificationHashMap;
    }

    public void setNotificationHashMap(HashMap<String, Notification> notificationHashMap) {
        this.notificationHashMap = notificationHashMap;
    }
}
