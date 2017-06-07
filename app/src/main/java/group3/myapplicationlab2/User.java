package group3.myapplicationlab2;

import android.util.Log;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mc on 26/04/17.
 */

public class User implements Serializable {
    private String name;
    private String email;
    private String uid;
    private List<GroupPreview> groups;
    private HashMap<String, GroupPreview> groupsHash;
    private HashMap<String, Notification> notificationHashMap;
    private String userPathImage="nopath";
    private String lastPicsUpload;
    private String currentPicsUpload;


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

    @Exclude
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



    public void setGroupsHash(HashMap<String, GroupPreview> groupsHash) {
        this.groupsHash = groupsHash;

        List<GroupPreview> newGroupPreviewList = new ArrayList<GroupPreview>();

        for (Map.Entry<String,GroupPreview> entry : groupsHash.entrySet()) {
            String key = entry.getKey();
            GroupPreview groupPreview1 = entry.getValue();
            Log.d("NAME", groupPreview1.getName());
            Log.d("ID", groupPreview1.getId());
            newGroupPreviewList.add(groupPreview1);
        }

        this.setGroups(newGroupPreviewList);

    }
    public HashMap<String, GroupPreview> getGroupsHash() {
        return groupsHash;
    }

    public void insertGroupPreviewInHashMap(String groupPreviewId, GroupPreview groupPreview){
        this.groupsHash.put(groupPreviewId, groupPreview);
        return;
    }



    public String getUserPathImage() {
        return userPathImage;
    }

    public void setUserPathImage(String userPathImage) {
        this.userPathImage = userPathImage;
    }

    public String getLastPicsUpload() {
        return lastPicsUpload;
    }

    public void setLastPicsUpload(String lastPicsUpload) {
        this.lastPicsUpload = lastPicsUpload;
    }

    public String getCurrentPicsUpload() {
        return currentPicsUpload;
    }

    public void setCurrentPicsUpload(String currentPicsUpload) {
        this.currentPicsUpload = currentPicsUpload;}
}
