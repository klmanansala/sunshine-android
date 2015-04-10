package com.example.android.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.StringTokenizer;

/**
Added class comment
*/
public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(getBaseContext(), SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private ShareActionProvider mShareActionProvider;

        public PlaceholderFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            Intent detailIntent = getActivity().getIntent();

            if(detailIntent != null && detailIntent.hasExtra(Intent.EXTRA_TEXT)) {
                String forecast = detailIntent.getStringExtra(Intent.EXTRA_TEXT);
                StringTokenizer tokenizer = new StringTokenizer(forecast, "-");
                String day = tokenizer.nextToken().trim();
                String date = day.substring(4);
                String description = tokenizer.nextToken().trim();
                String temperatures = tokenizer.nextToken().trim();
                String maxTemperature = temperatures.substring(0, temperatures.indexOf("/"));
                String minTemperature = temperatures.substring(temperatures.indexOf("/"));

                TextView dayTextView = (TextView) rootView.findViewById(R.id.detail_day_textview);
                TextView dateTextView = (TextView) rootView.findViewById(R.id.detail_date_textview);
                TextView maxTemperatureTextView = (TextView) rootView.findViewById(R.id.detail_max_temperature_textview);
                TextView minTemperatureTextView = (TextView) rootView.findViewById(R.id.detail_min_temperature_textview);
                TextView descriptionTextView = (TextView) rootView.findViewById(R.id.detail_description);

                dayTextView.setText(day.substring(0, 3));
                dateTextView.setText(date);
                descriptionTextView.setText(description);
                maxTemperatureTextView.setText(maxTemperature);
                minTemperatureTextView.setText(minTemperature);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, forecast + "#SunshineApp");
                setShareIntent(shareIntent);
            }

            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.detailfragment, menu);

            MenuItem shareItem = menu.findItem(R.id.menu_item_share);
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        }

        private void setShareIntent(Intent intent){
            if(mShareActionProvider != null){
                mShareActionProvider.setShareIntent(intent);
            } else{
                Log.d("DetailActivity", "ShareActionProvider is null?");
            }
        }
    }
}
