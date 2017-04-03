package group3.myapplicationlab2;

/**
 * Created by mc on 03/04/17.
 */

import java.util.*;

public class Partition {
    //int id;

    public Partition(){

    }

    public HashMap<Integer, Float> compute_partitions(List participants, List quote, float amount, int partition_id){
        //equi
        HashMap<Integer, Float> out_fractions = new HashMap<Integer, Float>();
        if (partition_id == 1){
            for (int i=0; i< participants.size(); i++){
                int id = (int)participants.get(i);
                float fract = (float) amount/participants.size();
                out_fractions.put(new Integer(id),new Float(fract));
            }
        }
        //quote
        if (partition_id == 2 || partition_id == 3){
            //compute total number of quote
            int total_sum = 0;
            for (Object q : quote){
                total_sum += (int) q;
            }
            for (int i=0; i < participants.size(); i++){
                int id = (int)participants.get(i);
                int q = (int)quote.get(i);
                float partial = amount *q / total_sum;
                out_fractions.put(new Integer(id), new Float(partial));
            }
        }

    return out_fractions;
    }



}
