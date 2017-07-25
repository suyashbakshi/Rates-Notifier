package net.ddns.suyashbakshi.ratenotifier;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView mainList = (RecyclerView)rootView.findViewById(R.id.main_list);
        mainList.setLayoutManager(new LinearLayoutManager(getActivity()));
        MainListAdapter mAdapter = new MainListAdapter(new ArrayList<String>(),getContext());
        mainList.setAdapter(mAdapter);

        FetchDataService fetch = new FetchDataService(mAdapter,getContext());
        fetch.execute();

        return rootView;
    }
}
