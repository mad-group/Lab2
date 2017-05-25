package group3.myapplicationlab2;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

/**
 * Created by anr.putina on 12/05/17.
 */


public class GroupMember implements Serializable {

    private String name;
    private String email;
    private Double payment= new Double(0);

    public void groupMemberConstructor(String name, String email){
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getPayment() {
        return payment;
    }

    public void setPayment(Double payment) {
        this.payment = payment;
    }

}

