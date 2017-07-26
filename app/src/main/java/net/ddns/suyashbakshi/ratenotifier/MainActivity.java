package net.ddns.suyashbakshi.ratenotifier;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import static net.ddns.suyashbakshi.ratenotifier.R.id.symbol_tv;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.fav_pref), MODE_PRIVATE);

        //noinspection SimplifiableIfStatement
        if (id == R.id.clear_target) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.fav_pref), "");
            editor.apply();
            Toast.makeText(getApplicationContext(), R.string.clear_preference, Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.show_bid) {
            String pref = sharedPreferences.getString(getString(R.string.fav_pref), "");
            if (!TextUtils.isEmpty(pref)) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.preference)
                        .setMessage("Currency combination: " + pref.split("/")[0]
                                + "\nTarget Bid: " + pref.split("/")[1])
                        .show();
            }
            else{
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.preference)
                        .setMessage(R.string.no_bid_set)
                        .show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
