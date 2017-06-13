package group3.myapplicationlab2;

import android.content.Context;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.internal.DowngradeableSafeParcel;
import com.google.android.gms.flags.impl.FlagProviderImpl;
import com.google.firebase.auth.GetTokenResult;
import com.google.zxing.client.result.EmailDoCoMoResultParser;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mc on 21/05/17.
 */

class MembersAdapter2 extends BaseAdapter {
    private float totAmount;
    private int[] arrayParts;
    private double[] arrayFractions;
    private DecimalFormat df = new DecimalFormat("##.##");
    private ArrayList<PurchaseContributor> pcList;
    private int totParts=0;
    private HashMap<String, Integer> map = new HashMap<>();
    private Context c;


    public MembersAdapter2(Context context,
                           ArrayList<PurchaseContributor> purchaseContributors,
                           float ta) {
        c = context;
        totAmount = ta;
        pcList = purchaseContributors;
        arrayFractions = new double[purchaseContributors.size()];
        arrayParts = new int[purchaseContributors.size()];
        Arrays.fill(arrayParts,1);
        totParts = purchaseContributors.size();
    }

    @Override
    public int getCount() {
        return pcList.size();
    }

    @Override
    public Object getItem(int position) {
        return pcList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater li=(LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView =li.inflate(R.layout.lv_ei_item, null);
        }

        PurchaseContributor pc = (PurchaseContributor) getItem(position);

        TextView member = (TextView) convertView.findViewById(R.id.item_member);
        member.setText(pc.getUser_name());

        EditText amount = (EditText) convertView.findViewById(R.id.item_amount);
        amount.setText(df.format(pc.getAmount()));

        TextView user_id_tv = (TextView) convertView.findViewById(R.id.item_user_id);
        user_id_tv.setText(pc.getUser_id());

        EditText part = (EditText)convertView.findViewById(R.id.item_part);
        part.setText(Integer.toString(pc.getParts()));

        ImageView up = (ImageView) convertView.findViewById(R.id.item_up_part);

        ImageView down = (ImageView) convertView.findViewById(R.id.item_down_part);


        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals("")){
                    //pc.get(position).setAmount(Double.parseDouble(editable.toString()));
                    pcList.get(position).setAmount(Double.parseDouble(editable.toString()));
                    notifyDataSetChanged();
                }

            }
        });

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0);
              int parts = pc.getParts();
                parts++;
                pc.setParts(parts);
                //totParts = getsumParts();
                part.setText(Integer.toString(parts));
/*
               int parts = pcList.get(position).getParts();
                parts++;
                pcList.get(position).setParts(parts);
                //totParts = getsumParts();
                part.setText(Integer.toString(parts));
                int parts = map.get(pc.getUser_id());
                parts++;
                map.put(pc.getUser_id(), parts);
                //totParts = getsumParts();
                part.setText(Integer.toString(parts));
*/

            }
        });

/*          down.findViewById(R.id.item_down_part).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0);
*//*                if (pc.getParts() == 0){
                    pc.setParts(0);
                    part.setText(Integer.toString(0));
                }else{
                    int parts = pc.getParts();
                    parts--;
                    pc.setParts(parts);
                    part.setText(Integer.toString(parts));
                }*//*
*//*                if (pcList.get(position).getParts() == 0){
                    pcList.get(position).setParts(0);
                    part.setText(Integer.toString(0));
                }else{
                    int parts = pcList.get(position).getParts();
                    parts--;
                    pcList.get(position).setParts(parts);
                    part.setText(Integer.toString(parts));
                }*//*
*//*                if (map.get(pc.getUser_id()) == 0){
                    map.put(pc.getUser_id(),0);
                    part.setText(Integer.toString(0));
                }else{
                    int parts = map.get(pc.getUser_id());
                    parts--;
                    map.put(pc.getUser_id(),0);
                    part.setText(Integer.toString(parts));
                }*//*

            }
        });

        part.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                EditText localParts = (EditText) finalConvertView.findViewById(R.id.item_part);
                String partsString = localParts.getText().toString();
                if (partsString.equals("")){
                    pcList.get(position).setParts(0);
                }
                else{
                    pcList.get(position).setParts(Integer.parseInt(partsString));
                }
                EditText localAmount = (EditText) finalConvertView.findViewById(R.id.item_amount);
                int allParts = getsumParts();

                if (allParts== 0){
                    for (int i=0; i<pcList.size(); i++){
                        pcList.get(i).setAmount(0);
                        localAmount.setText("?");
                    }
                }
                else{
                    float fraction;
                  fraction = totAmount*pc.getParts()/allParts;
                    localAmount.setText(Float.toString(fraction));*//*
                    Log.d("Debug","--------------------------------");
                 for (int i =0; i<pcList.size(); i++){
                        fraction = totAmount*pcList.get(i).getParts()/getSumPartsInArray();
                        Log.d("Debug", "i: " + i + "p: " +pcList.get(i).getParts());
                        pcList.get(i).setAmount(fraction);
                    }*//*
                    for (String k : map.keySet()){
                        fraction = totAmount*map.get(k)/getSumPartsInMap();
                        Log.d("Debug", "i: " + k+ "p: " +map.get(k));
                        for (int i=0; i< pcList.size(); i++){
                            if (pcList.get(i).getUser_id().equals(k))
                                pcList.get(i).setAmount(fraction);
                        }
                    }

                }


            }
        });*/
        return convertView;
    }

    public void setTotAmount(float totAmount) {
        this.totAmount = totAmount;
    }

    public int getsumParts(){
        int parts = 0;
        for (int i=0; i<arrayParts.length; i++){
            parts += arrayParts[i];
        }
        return parts;
    }

    public int getSumPartsInArray(){
        int parts = 0;
        for (int i=0; i<pcList.size(); i++){
            parts += pcList.get(i).getParts();
        }
        return parts;
    }

    public int getSumPartsInMap(){
        int parts = 0;
        for (String k : map.keySet()){
            parts += map.get(k);
        }
        return parts;
    }


    public void updatePartial(float newTotAmount) {
        totAmount = newTotAmount;
    }

    public ArrayList<PurchaseContributor> getPcList(){return this.getPcList();}

    private class ViewHolder {
        TextView pcUserName;
        EditText pcAmount;
        EditText pcPart;
        ImageView upArrow;
        ImageView downArrow;
        int position;

        public ViewHolder(View view, int p) {
            pcUserName = (TextView)view.findViewById(R.id.item_member);
            pcAmount = (EditText) view.findViewById(R.id.item_amount);
            pcPart = (EditText) view.findViewById(R.id.item_part);
            upArrow = (ImageView) view.findViewById(R.id.item_up_part);
            downArrow = (ImageView) view.findViewById(R.id.item_down_part);
            position = p;


        }
    }
}


