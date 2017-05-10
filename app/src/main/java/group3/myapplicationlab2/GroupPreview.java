package group3.myapplicationlab2;

import java.io.Serializable;

/**
 * Created by anr.putina on 08/05/17.
 */

public class GroupPreview implements Serializable {

    private String name;
    private String id;
    private String description;


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
}