package group3.myapplicationlab2;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by gaetano on 25/05/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    public ArrayList<Purchase> list;

    /*private String[] titles = {"Chapter One",
            "Chapter Two",
            "Chapter Three",
            "Chapter Four",
            "Chapter Five",
            "Chapter Six",
            "Chapter Seven",
            "Chapter Eight"};

    private String[] details = {"Item one details",
            "Item two details", "Item three details",
            "Item four details", "Item file details",
            "Item six details", "Item seven details",
            "Item eight details"};*/

    public class ViewHolder extends RecyclerView.ViewHolder {
        public int currentItem;
        public TextView expenseName, expenseAuthor;
        // Adapter constructor
        public ViewHolder(View itemView) {
            super(itemView);
            expenseName = (TextView)expenseName.findViewById(R.id.tv_card_expense_name);
            expenseAuthor = (TextView)expenseAuthor.findViewById(R.id.tv_card_general_info);
            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public  void onClick(View v) {
                    //Log.d("TestRec", "Dentro onClick di recycler view");
                    int position = getAdapterPosition();
                    Snackbar.make(v, "Click detected on item " + position,
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });*/
        }
    }

    // Recycler adapter: it receives purchases list
    public RecyclerAdapter(ArrayList<Purchase> list) {
        this.list = list;
    }

    @Override
    // This method returns the inflated CardView
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Purchase purc = list.get(i);
        // Here the definition of what we want to show in the expense card
        viewHolder.expenseAuthor.setText(purc.getAuthorName());
        viewHolder.expenseName.setText(purc.getCausal());
        /*viewHolder.itemImage.setImageResource(images[i]);*/
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}