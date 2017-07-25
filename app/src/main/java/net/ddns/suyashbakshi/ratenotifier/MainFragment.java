package net.ddns.suyashbakshi.ratenotifier;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
    public MainFragment() {
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mainList = (RecyclerView) rootView.findViewById(R.id.main_list);
        mainList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new MainListAdapter(new ArrayList<String>(), getContext());
        mainList.setAdapter(mAdapter);
        registerForContextMenu(mainList);

        mainList.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                Snackbar snackbar = Snackbar.make(view, R.string.snackbar_message,Snackbar.LENGTH_LONG);
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
                }, 3000);
                refreshLayout.setRefreshing(false);
            }
        });

        updateRateList();
        startAlert();
        getContext().registerReceiver(new MyReceiver(),new IntentFilter(),null,null);
        return rootView;
    }

    public void updateRateList(){
        FetchDataService fetchDataService = new FetchDataService(mAdapter,getContext());
        fetchDataService.execute();
    }

    public void startAlert() {

        int refreshPeriod = 60;

        Intent intent = new Intent(getContext(), MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(), 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                SystemClock.elapsedRealtime(),
                refreshPeriod*1000,
                pendingIntent);

        Toast.makeText(getContext(), "Alarm set in 60 seconds",Toast.LENGTH_LONG).show();


    }

    public static class MyReceiver extends BroadcastReceiver{
        public MyReceiver() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, R.string.data_refresh_msg,Toast.LENGTH_SHORT).show();
            FetchDataService fetchDataService = new FetchDataService(mAdapter,context);
            fetchDataService.execute();
        }
    }
}
