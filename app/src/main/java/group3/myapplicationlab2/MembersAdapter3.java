package group3.myapplicationlab2;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anr.putina on 12/06/17.
 */

public class MembersAdapter3 extends ArrayAdapter<PurchaseContributor> {

    private float totalAmount=0;
    private int totalParts=0;
    private ArrayList<PurchaseContributor> purchaseContributors;
    private DecimalFormat df = new DecimalFormat("##.##");

    public MembersAdapter3(Context context, ArrayList<PurchaseContributor> purchaseContributors) {
        super(context, 0, purchaseContributors);
        this.purchaseContributors = purchaseContributors;
        totalParts = purchaseContributors.size();
    }

    public void setTotalAmount (float totalAmount){
        this.totalAmount = totalAmount;
        recomputeAmounts();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final PurchaseContributor purchaseContributor = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lv_ei_item, parent, false);
        }

        TextView member = (TextView) convertView.findViewById(R.id.item_member);
        member.setText(purchaseContributor.getUser_name());

        final TextView amount = (TextView) convertView.findViewById(R.id.item_amount);
        amount.setText(df.format(purchaseContributor.getAmount()));

        TextView part = (TextView) convertView.findViewById(R.id.item_part);
        part.setText(Integer.toString(purchaseContributor.getParts()));

        final View finalConvertView = convertView;
        ImageView up = (ImageView) convertView.findViewById(R.id.item_up_part);
        ImageView down = (ImageView) convertView.findViewById(R.id.item_down_part);

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView part = (TextView) finalConvertView.findViewById(R.id.item_part);
                int parts = Integer.parseInt(part.getText().toString());
                parts = parts +1;
                totalParts = totalParts +1;
                purchaseContributor.setParts(parts);
                part.setText(Integer.toString(parts));
                recomputeAmounts();
            }
        });

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView part = (TextView) finalConvertView.findViewById(R.id.item_part);
                int parts = Integer.parseInt(part.getText().toString());

                if (totalParts > 1){
                    if (parts == 0){
                        part.setText(Integer.toString(0));
                        purchaseContributor.setParts(parts);
                        recomputeAmounts();
                    }else{
                        parts = parts -1;
                        purchaseContributor.setParts(parts);
                        totalParts = totalParts - 1;
                        part.setText(Integer.toString(parts));
                        recomputeAmounts();
                    }
                }
            }
        });

        return convertView;
    }

    public void recomputeAmounts(){
        float partAmount = totalAmount/(float)totalParts;
        for (int i=0; i<purchaseContributors.size(); i++){
            purchaseContributors.get(i).setAmount(partAmount*purchaseContributors.get(i).getParts());
        }
        notifyDataSetChanged();
    }

    public ArrayList<PurchaseContributor> getPurchaseContributors(){
        return this.purchaseContributors;
    }

    public int getTotalParts(){
        return this.totalParts;
    }

    public float sumAmounts(){
        float total = 0;
        for (int i=0; i<purchaseContributors.size();i++){
            total += purchaseContributors.get(i).getAmount();
        }
        return total;
    }

    public List<PurchaseContributor> getPurchaseContributorsList(){
        return this.purchaseContributors;
    }

}
