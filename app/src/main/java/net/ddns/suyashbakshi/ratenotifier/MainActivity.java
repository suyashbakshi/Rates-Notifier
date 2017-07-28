package net.ddns.suyashbakshi.ratenotifier;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.fav_pref), MODE_PRIVATE);

        if (id == R.id.clear_target) {
            clearBid(sharedPreferences);
            return true;
        } else if (id == R.id.show_bid) {
           showBid(sharedPreferences);
        } else if (id == R.id.spinner) {
            notifyWhenSelect();
        }

        return super.onOptionsItemSelected(item);
    }

    private void notifyWhenSelect() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final SharedPreferences[] sharedPreferences = {getSharedPreferences(getString(R.string.notify_pref), MODE_PRIVATE)};

        Spinner spinner = new Spinner(getApplicationContext());
        spinner.setAdapter(adapter);
        spinner.setSelection(Integer.parseInt(sharedPreferences[0].getString(getString(R.string.notify_pref),"0")));

        new AlertDialog.Builder(MainActivity.this)
                .setView(spinner)
                .show();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(i!=0) {
                    Log.v("spinner_check", String.valueOf(i));
                    sharedPreferences[0] = getSharedPreferences(getString(R.string.notify_pref), MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences[0].edit();
                    editor.putString(getString(R.string.notify_pref), String.valueOf(i));
                    editor.apply();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void showBid(SharedPreferences sharedPreferences) {
        String pref = sharedPreferences.getString(getString(R.string.fav_pref), "");
        if (!TextUtils.isEmpty(pref)) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.preference)
                    .setMessage("Currency combination: " + pref.split("/")[0]
                            + "\nTarget Bid: " + pref.split("/")[1])
                    .show();
        } else {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.preference)
                    .setMessage(R.string.no_bid_set)
                    .show();
        }
    }

    private void clearBid(SharedPreferences sharedPreferences) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.fav_pref), "");
        editor.apply();
        Toast.makeText(getApplicationContext(), R.string.clear_preference, Toast.LENGTH_SHORT).show();
    }
}
