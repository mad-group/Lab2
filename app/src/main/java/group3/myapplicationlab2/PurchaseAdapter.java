package group3.myapplicationlab2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Created by gaeta on 26/05/2017.
 */

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.MyViewHolder> {
    private List<Purchase> purchaseList;

    public  class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView author, amount;
        public MyViewHolder(View view) {
            super(view);
            author = (TextView)view.findViewById(R.id.tv_card_expense_name);
            amount = (TextView)view.findViewById(R.id.tv_card_general_info);
        }
    }

    // Adapter constructor
    public PurchaseAdapter(List<Purchase> purchaseList) {
        this.purchaseList = purchaseList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Purchase purchaseItem = purchaseList.get(position);
        holder.author.setText(purchaseItem.getAuthorName());
        holder.amount.setText(String.valueOf(purchaseItem.getTotalAmount()));
    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }
}
