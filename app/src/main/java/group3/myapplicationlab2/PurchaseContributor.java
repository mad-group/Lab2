package group3.myapplicationlab2;

import java.io.Serializable;

/**
 * Created by mc on 24/05/17.
 */

public class PurchaseContributor implements Serializable {
    private String user_id;
    private String user_name;
    private boolean payed = false; //if it is true, this contributor isnt showd in the resume
    private double amount;
    private String contributor_id;

    public PurchaseContributor(){

    }

    public String getUser_id(){return user_id;}
    public void setUser_id(String uid){user_id = uid;}

    public String getUser_name(){return user_name;}
    public void setUser_name(String un){user_name = un;}

    public boolean getPayed(){return payed;}
    public void setPayed(boolean value){payed = value;}

    public double getAmount(){return amount;}
    public void setAmount(double value){amount = value;}

    public String getContributor_id() {return contributor_id;}
    public void setContributor_id(String cid) {contributor_id=cid;}


}
