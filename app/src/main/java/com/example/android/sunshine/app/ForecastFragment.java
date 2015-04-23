package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by kevin on 3/16/15.
 */
public class ForecastFragment extends Fragment {

    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();

    private ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);

        mForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast
                , R.id.list_item_forecast_textview, new ArrayList<String>());

        ListView forecastListVew = (ListView) rootView.findViewById(R.id.listview_forecast);
        forecastListVew.setAdapter(mForecastAdapter);
        forecastListVew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String dayClicked = mForecastAdapter.getItem(position);
                //Toast.makeText(getActivity(), dayClicked, Toast.LENGTH_SHORT).show();
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, dayClicked);
                startActivity(detailIntent);
            }
        });

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            updateWeather();
            return true;
        } else if(id == R.id.action_view_location){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String locationValue = sharedPref.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
            Uri geoLocation = Uri.parse("geo:0,0").buildUpon()
                    .appendQueryParameter("q", locationValue)
                    .build();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(geoLocation);
            if(intent.resolveActivity(getActivity().getPackageManager()) != null){
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    private void updateWeather(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String locationValue = sharedPref.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        new FetchWeatherTask(getActivity(), mForecastAdapter).execute(locationValue);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String temperatureUnitValue = sharedPref.getString(getString(R.string.pref_temperature_units_key), getString(R.string.pref_temperature_unit_default));

        if(!temperatureUnitValue.equals(getString(R.string.pref_temperature_unit_default))){ //if Imperial
            high = (high * 9/5.0) + 32;
            low = (low * 9/5.0) + 32;
        }

        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }
}
