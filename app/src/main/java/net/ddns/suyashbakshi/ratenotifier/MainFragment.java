package net.ddns.suyashbakshi.ratenotifier;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.Context.ALARM_SERVICE;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private RecyclerView mainList;
    private static MainListAdapter mAdapter;
    private static AlarmManager alarmManager;
    static final int ALARM_ID = 76458;

    public MainFragment() {
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mAdapter = new MainListAdapter(new ArrayList<String>(), getContext());

        mainList = (RecyclerView) rootView.findViewById(R.id.main_list);
        mainList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mainList.setAdapter(mAdapter);
        registerForContextMenu(mainList);

        mainList.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                Snackbar snackbar = Snackbar.make(view, R.string.snackbar_message, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                updateRateList();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        updateRateList();

        getContext().registerReceiver(new MyReceiver(), new IntentFilter(), null, null);
        return rootView;
    }

    public void updateRateList() {

        if (Utility.isOnline(getContext())) {
            FetchDataService fetchDataService = new FetchDataService(mAdapter, getContext());
            fetchDataService.execute();
            if (!isAlarmSet(getContext()))
                startAlarm();
        } else {
            Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                    .show();
        }
    }

    public void startAlarm() {
        Log.v("Alarm_check", "created");
        int refreshPeriod = 60;

        Intent intent = new Intent(getContext(), MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(), ALARM_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                SystemClock.elapsedRealtime(),
                refreshPeriod * 1000,
                pendingIntent);

        Toast.makeText(getContext(), R.string.refresh_rate_text, Toast.LENGTH_LONG).show();
    }

    public boolean isAlarmSet(Context context) {
        Intent intent = new Intent(getContext(), MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(), ALARM_ID, intent, PendingIntent.FLAG_NO_CREATE);
        boolean isWorking = (pendingIntent != null);
        Log.d("Alarm_check", "alarm is " + (isWorking ? "" : "not") + " working...");
        return isWorking;
    }

    private static void stopAlarm(Context context) {

        Intent myIntent = new Intent(context, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, ALARM_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager != null) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Toast.makeText(context, "Alarm Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public static class MyReceiver extends BroadcastReceiver {
        public MyReceiver() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utility.isOnline(context)) {
                FetchDataService fetchDataService = new FetchDataService(mAdapter, context);
                fetchDataService.execute();
                Toast.makeText(context, R.string.data_refresh_msg, Toast.LENGTH_SHORT).show();
            } else {
                stopAlarm(context);
            }
        }

    }
}
