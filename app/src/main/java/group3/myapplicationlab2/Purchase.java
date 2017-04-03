package group3.myapplicationlab2;

import java.util.*;

/**
 * Created by mc on 03/04/17.
 */

public class Purchase {
    Date date;
    String causal;
    Float amount;
    int member_id;
    int partition_id; //the id of partion method
    List sharing_members;

    public Purchase(){
        this.date = new Date();
    }

    public void InsertPurchase(int creator, int partition_id, List partecipants, int group_id){
        Partition p = new Partition();
        List quote;
       // float fraction_amount = p.compute_partitions(partecipants, quote, amount, partition_id);
    }

}
