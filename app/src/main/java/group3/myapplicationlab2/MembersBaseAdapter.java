package group3.myapplicationlab2;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by anr.putina on 12/06/17.
 */

public class MembersBaseAdapter extends BaseAdapter{

    private Context context;
    ArrayList<PurchaseContributor> purchaseContributors;
    private DecimalFormat df = new DecimalFormat("##.##");


    public MembersBaseAdapter(Context context, ArrayList<PurchaseContributor> purchaseContributors){
        this.context = context;
        this.purchaseContributors = purchaseContributors;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if(purchaseContributors != null && purchaseContributors.size() != 0){
            return purchaseContributors.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return purchaseContributors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //ViewHolder holder = null;
        final ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.manual_adapter_item, null);

            holder.member = (TextView) convertView.findViewById(R.id.item_member);
            holder.amount = (EditText) convertView.findViewById(R.id.item_amount);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.ref = position;

        holder.member.setText(purchaseContributors.get(position).getUser_name());
        holder.amount.setText(df.format(purchaseContributors.get(position).getAmount()));
        holder.amount.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (!arg0.toString().isEmpty()){
                    purchaseContributors.get(holder.ref).setAmount(Float.parseFloat(arg0.toString()));
                }
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView member;
        EditText amount;
        int ref;
    }

    public float sumAmounts(){
        float total = 0;
        for (int i=0; i<purchaseContributors.size();i++){
            total += purchaseContributors.get(i).getAmount();
        }
        return total;
    }


}
