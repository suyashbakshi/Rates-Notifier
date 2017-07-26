package net.ddns.suyashbakshi.ratenotifier;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by suyash on 7/24/2017.
 */

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.ViewHolder> {

    ArrayList<String> mList;
    Context mContext;

    public void addItem(String item) {
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
        Log.v("Test_data", mList.get(position) + " " + split[0] + " " + split[1] + " " + split[2] + " " + split[3] + " " + split[4] + " ");
        holder.symbol_tv.setText(split[0]);
        holder.bid_tv.setText("Bid: " + split[1]);
        holder.ask_tv.setText("Ask: " + split[2]);
        holder.high_tv.setText("High: " + split[3]);
        holder.high_tv.setTextColor(Color.GREEN);
        holder.low_tv.setText("Low: " + split[4]);
        holder.low_tv.setTextColor(Color.RED);

        if (Integer.parseInt(split[5]) == 1) {
            holder.symbol_tv.setTextColor(Color.GREEN);
            holder.indicator_iv.setImageResource(R.drawable.green_arrow);
        } else if (Integer.parseInt(split[5]) == -1) {
            holder.symbol_tv.setTextColor(Color.RED);
            holder.indicator_iv.setImageResource(R.drawable.red_arrow);
        } else {
            holder.symbol_tv.setTextColor(Color.WHITE);
            holder.indicator_iv.setImageResource(0);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void clear() {
        mList.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView symbol_tv, ask_tv, bid_tv, high_tv, low_tv;
        ImageView indicator_iv;
        LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnCreateContextMenuListener(this);
            symbol_tv = (TextView) itemView.findViewById(R.id.symbol_tv);
            bid_tv = (TextView) itemView.findViewById(R.id.bid_tv);
            ask_tv = (TextView) itemView.findViewById(R.id.ask_tv);
            high_tv = (TextView) itemView.findViewById(R.id.high_tv);
            low_tv = (TextView) itemView.findViewById(R.id.low_tv);
            indicator_iv = (ImageView) itemView.findViewById(R.id.indicator_iv);
            layout = (LinearLayout) itemView.findViewById(R.id.container_layout);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle(R.string.preference);
            contextMenu.add(0, view.getId(), 0, "Set target bid").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {

                    final EditText editText = new EditText(mContext);
                    editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    new AlertDialog.Builder(mContext)
                            .setTitle("Edit Preference")
                            .setMessage("Set target bid for " + symbol_tv.getText())
                            .setView(editText)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String targetBid = String.valueOf(editText.getText());

                                    if (TextUtils.isEmpty(targetBid))
                                        return;
                                    else if (!targetBid.matches("\\d+(?:\\.\\d+)?")) {
                                        Toast.makeText(mContext, "Invalid input, only integers allowed", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.fav_pref), mContext.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(mContext.getString(R.string.fav_pref), symbol_tv.getText() + "/" + targetBid);
                                    editor.apply();

                                    Toast.makeText(mContext, "Target bid for " + symbol_tv.getText() + " set to " + targetBid, Toast.LENGTH_SHORT).show();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();

                    return true;
                }
            });
        }

    }
}
