package net.ddns.suyashbakshi.ratenotifier;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by suyash on 7/24/2017.
 */

public class FetchDataService extends AsyncTask<Void, Void, String> {

    private MainListAdapter mAdapter;
    private Context mContext;

    public FetchDataService(MainListAdapter adapter, Context context) {
        mAdapter = adapter;
        mContext = context;
    }

    @Override
    protected void onPostExecute(String xml) {
        super.onPostExecute(xml);

        if (TextUtils.isEmpty(xml)) {
            Toast.makeText(mContext, R.string.no_data, Toast.LENGTH_LONG).show();
            return;
        }

        convertXMLToUXFormat(mAdapter, xml);
        mAdapter.notifyDataSetChanged();
        Log.v("Check_insert", String.valueOf(mAdapter.getItemCount()));


    }

    private void convertXMLToUXFormat(MainListAdapter mAdapter, String xml) {

        SharedPreferences preferences = mContext.getSharedPreferences(mContext.getString(R.string.fav_pref), Context.MODE_PRIVATE);
        String savedPref = preferences.getString(mContext.getString(R.string.fav_pref), "");

        String pref_symbol = null, target_bid = null;
        if (!TextUtils.isEmpty(savedPref)) {
            String[] split = savedPref.split("/");
            pref_symbol = split[0];
            target_bid = split[1];
        }
        String symbol = null, bid = null, ask = null, high = null, low = null, direction = null;
        StringBuilder builder;
        XmlPullParserFactory xmlFactoryObject = null;
        XmlPullParser myparser = null;
        try {
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            myparser = xmlFactoryObject.newPullParser();

            myparser.setInput(new StringReader(xml));

            int event = myparser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT: {
                        Log.v("Start document", "");
                        break;
                    }
                    case XmlPullParser.START_TAG: {
                        Log.v("Start tag ", myparser.getName());
                        if (myparser.getName().equalsIgnoreCase("Rate")) {
                            symbol = myparser.getAttributeValue(0);
                        } else if (myparser.getName().equalsIgnoreCase("Bid")) {
                            event = myparser.next();
                            bid = myparser.getText();

                            if (symbol.equalsIgnoreCase(pref_symbol) && Double.valueOf(bid) >= Double.valueOf(target_bid)) {
                                Log.v("Bid_check", "reached");
                                showNotification(pref_symbol,target_bid);
                            }

                        } else if (myparser.getName().equalsIgnoreCase("Ask")) {
                            event = myparser.next();
                            ask = myparser.getText();
                        } else if (myparser.getName().equalsIgnoreCase("High")) {
                            event = myparser.next();
                            high = myparser.getText();
                        } else if (myparser.getName().equalsIgnoreCase("Low")) {
                            event = myparser.next();
                            low = myparser.getText();
                        } else if (myparser.getName().equalsIgnoreCase("Direction")) {
                            event = myparser.next();
                            direction = myparser.getText();
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG: {
                        Log.v("End tag ", myparser.getName());
                        if (myparser.getName().equalsIgnoreCase("Rate")) {
                            builder = new StringBuilder()
                                    .append(symbol)
                                    .append("/" + bid)
                                    .append("/" + ask)
                                    .append("/" + high)
                                    .append("/" + low)
                                    .append("/" + direction);
                            mAdapter.addItem(builder.toString());
                            Log.v("Built_string", builder.toString());
                        }
                        break;
                    }
                    case XmlPullParser.END_DOCUMENT: {
                        Log.v("Start document", "");
                        break;
                    }
                }
                event = myparser.next();
            }

        } catch (UnsupportedEncodingException e) {

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void showNotification(String pref_symbol, String target_bid) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(R.drawable.dollar);
        mBuilder.setContentTitle(mContext.getString(R.string.app_name));
        mBuilder.setContentText(mContext.getString(R.string.target_reached));
        mBuilder.setVibrate(new long[]{0, 100, 100, 100});
        mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(mContext.getString(R.string.app_name));
        inboxStyle.setSummaryText(mContext.getString(R.string.target_reached));
        inboxStyle.addLine(pref_symbol + "'s bid is now more than " + target_bid);
        mBuilder.setStyle(inboxStyle);

        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // notificationID allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }

    @Override
    protected String doInBackground(Void... voids) {

        if (mAdapter != null) {
            mAdapter.clear();
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://rates.fxcm.com/RatesXML")
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            return response.body().string();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
