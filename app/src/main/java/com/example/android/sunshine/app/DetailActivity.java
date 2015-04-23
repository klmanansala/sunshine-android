package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import com.example.android.sunshine.app.data.WeatherContract;

/**
Added class comment
*/
public class DetailActivity extends ActionBarActivity{

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
    public static class PlaceholderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

        private static final String LOG_TAG = DetailActivity.class.getSimpleName();
        private static int DETAILS_LOADER_ID = 1;

        private static final String[] FORECAST_COLUMNS = {
                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATE,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
        };

        // these constants correspond to the projection defined above, and must change if the
        // projection changes
        private static final int COL_WEATHER_ID = 0;
        private static final int COL_WEATHER_DATE = 1;
        private static final int COL_WEATHER_DESC = 2;
        private static final int COL_WEATHER_MAX_TEMP = 3;
        private static final int COL_WEATHER_MIN_TEMP = 4;

        private ShareActionProvider mShareActionProvider;

        public PlaceholderFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_detail, container, false);

        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            getLoaderManager().initLoader(DETAILS_LOADER_ID, null, this);
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

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.v(LOG_TAG, "In onCreateLoader");
            Intent intent = getActivity().getIntent();
            if (intent == null) {
                return null;
            }

            return new CursorLoader(getActivity()
                    , intent.getData()
                    , FORECAST_COLUMNS
                    , null
                    , null
                    , null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.v(LOG_TAG, "In onLoadFinished");
            if (!data.moveToFirst()) { return; }

            View rootView = getView();

            TextView dayTextView = (TextView) rootView.findViewById(R.id.detail_day_textview);
            TextView dateTextView = (TextView) rootView.findViewById(R.id.detail_date_textview);
            TextView maxTemperatureTextView = (TextView) rootView.findViewById(R.id.detail_max_temperature_textview);
            TextView minTemperatureTextView = (TextView) rootView.findViewById(R.id.detail_min_temperature_textview);
            TextView descriptionTextView = (TextView) rootView.findViewById(R.id.detail_description);

            String date = Utility.formatDate(data.getLong(COL_WEATHER_DATE));
            String maxTemperature = Utility.formatTemperature(data.getDouble(COL_WEATHER_MAX_TEMP)
                    , Utility.isMetric(getActivity()));
            String minTemperature = Utility.formatTemperature(data.getDouble(COL_WEATHER_MIN_TEMP)
                    , Utility.isMetric(getActivity()));

            dayTextView.setText(date);
            dateTextView.setText(date);
            descriptionTextView.setText(data.getString(COL_WEATHER_DESC));
            maxTemperatureTextView.setText(maxTemperature);
            minTemperatureTextView.setText(minTemperature);

            String forecast = date +
                    " - " + data.getString(COL_WEATHER_DESC) +
                    " - " + maxTemperature + " / " + minTemperature;

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            setShareIntent(createShareForecastIntent(forecast));

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

        private Intent createShareForecastIntent(String forecast) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, forecast + " #SunshineApp");
            return shareIntent;
        }
    }
}
