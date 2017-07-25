package net.ddns.suyashbakshi.ratenotifier;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IntegerRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by suyash on 7/24/2017.
 */

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.ViewHolder> {

    ArrayList<String> mList;
    Context mContext;

    public void addItem(String item){
        mList.add(item);
    }

    public MainListAdapter(ArrayList<String> list, Context context) {
        this.mList = list;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String[] split = mList.get(position).split("/");
        Log.v("Test_data",mList.get(position)+" "+split[0]+" "+split[1]+" "+split[2]+" "+split[3]+" "+split[4]+" ");
        holder.symbol_tv.setText(split[0]);
        holder.bid_tv.setText("Bid: " +split[1]);
        holder.ask_tv.setText("Ask: " +split[2]);
        holder.high_tv.setText("High: " +split[3]);
        holder.low_tv.setText("Low: " + split[4]);

        if(Integer.parseInt(split[5]) == 1){
            holder.symbol_tv.setTextColor(Color.GREEN);
        }else if(Integer.parseInt(split[5]) == -1){
            holder.symbol_tv.setTextColor(Color.RED);
        }else {
            holder.symbol_tv.setTextColor(Color.DKGRAY);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView symbol_tv,ask_tv,bid_tv,high_tv,low_tv;

        public ViewHolder(View itemView) {
            super(itemView);
            symbol_tv = (TextView) itemView.findViewById(R.id.symbol_tv);
            bid_tv = (TextView) itemView.findViewById(R.id.bid_tv);
            ask_tv = (TextView) itemView.findViewById(R.id.ask_tv);
            high_tv = (TextView) itemView.findViewById(R.id.high_tv);
            low_tv = (TextView) itemView.findViewById(R.id.low_tv);
        }
    }
}