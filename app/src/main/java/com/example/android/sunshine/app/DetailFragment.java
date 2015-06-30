package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static int DETAILS_LOADER_ID = 1;

    private Uri mUri;

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_WIND_SPEED = 5;
    private static final int COL_WEATHER_DEGREES = 6;
    private static final int COL_WEATHER_HUMIDITY = 7;
    private static final int COL_WEATHER_PRESSURE = 8;
    private static final int COL_WEATHER_STATUS_ID = 9;

    private ShareActionProvider mShareActionProvider;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    public static DetailFragment newInstance(Uri uri) {
        DetailFragment f = new DetailFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("uri", uri.toString());
        f.setArguments(args);

        return f;
    }

    Uri getUriPassedToFragment(){
        Intent intent = getActivity().getIntent();
        if(getArguments() != null) {
            String uriString = getArguments().getString("uri");
            return Uri.parse(uriString);
        } else if(intent != null && intent.getData() != null){
            return intent.getData();
        } else {
            return null;
        }
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
            //Log.d("DetailActivity", "ShareActionProvider is null?");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Log.v(LOG_TAG, "In onCreateLoader");
        if(mUri == null && getUriPassedToFragment() != null) {
            mUri = getUriPassedToFragment();
        } else if (mUri == null && getUriPassedToFragment() == null){
            return null;
        }

        return new CursorLoader(getActivity()
                , mUri
                , FORECAST_COLUMNS
                , null
                , null
                , null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) { return; }

        View rootView = getView();

        ImageView iconView = (ImageView) rootView.findViewById(R.id.detail_imageview);
        TextView dayTextView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        TextView dateTextView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        TextView maxTemperatureTextView = (TextView) rootView.findViewById(R.id.detail_max_temperature_textview);
        TextView minTemperatureTextView = (TextView) rootView.findViewById(R.id.detail_min_temperature_textview);
        TextView descriptionTextView = (TextView) rootView.findViewById(R.id.detail_description);
        TextView windTextView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        TextView humidityTextView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        TextView pressureTextView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);

        Long date = data.getLong(COL_WEATHER_DATE);
        String maxTemperature = Utility.formatTemperature(getActivity()
                ,data.getDouble(COL_WEATHER_MAX_TEMP));

        String minTemperature = Utility.formatTemperature(getActivity()
                ,data.getDouble(COL_WEATHER_MIN_TEMP));

        int statusId = data.getInt(COL_WEATHER_STATUS_ID);
        iconView.setImageResource(Utility.getArtResourceForWeatherCondition(statusId));

        dayTextView.setText(Utility.getDayName(getActivity(), date));
        dateTextView.setText(Utility.getFormattedMonthDay(getActivity(), date));
        descriptionTextView.setText(data.getString(COL_WEATHER_DESC));
        maxTemperatureTextView.setText(maxTemperature);
        minTemperatureTextView.setText(minTemperature);
        windTextView.setText(Utility.getFormattedWind(getActivity()
                , data.getFloat(COL_WEATHER_WIND_SPEED), data.getFloat(COL_WEATHER_DEGREES)).trim());
        humidityTextView.setText(getActivity().getString(R.string.format_humidity
                , data.getFloat(COL_WEATHER_HUMIDITY)));
        pressureTextView.setText(getActivity().getString(R.string.format_pressure
                , data.getFloat(COL_WEATHER_PRESSURE)));

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

    void onLocationChanged( String newLocation ) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAILS_LOADER_ID, null, this);
        }
    }
}
