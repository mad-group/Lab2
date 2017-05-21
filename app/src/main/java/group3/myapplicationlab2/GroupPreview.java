package group3.myapplicationlab2;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * Created by anr.putina on 08/05/17.
 */

public class GroupPreview implements Serializable, Comparable<GroupPreview> {

    private String name;
    private String id;
    private String description;
    private long lastModify;
    private Integer notifications=0;

    public GroupPreview(){

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getLastModify(){return this.lastModify;}
    public void setLastModify(long lm){lastModify = lm;}

    @Override
    public int compareTo(@NonNull GroupPreview groupPreview) {
        return (int)(this.lastModify - groupPreview.getLastModify()) ;
    }

    @Exclude
    public Integer getNotifications() {
        return notifications;
    }

    @Exclude
    public void addNotifications(){
        this.notifications = this.notifications + 1;
    }

    @Exclude
    public void resetNotifications(){
        this.notifications = 0;
    }
}
