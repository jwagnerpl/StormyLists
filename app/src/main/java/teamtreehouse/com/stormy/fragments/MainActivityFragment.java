package teamtreehouse.com.stormy.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import teamtreehouse.com.stormy.R;
import teamtreehouse.com.stormy.ui.MainActivity;
import teamtreehouse.com.stormy.weather.Current;
import teamtreehouse.com.stormy.weather.Day;
import teamtreehouse.com.stormy.weather.Forecast;
import teamtreehouse.com.stormy.weather.Hour;


public class MainActivityFragment extends android.support.v4.app.Fragment {
    MainActivity mainActivity = new MainActivity();
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String DAILY_FORECAST = "DAILY_FORECAST";
    public static final String HOURLY_FORECAST = "HOURLY_FORECAST";
    private Forecast mForecast;

    @InjectView(R.id.dailyButton) Button dailyButton;
    @InjectView(R.id.hourlyButton) Button hourlyButton;
    @InjectView(R.id.timeLabel) TextView mTimeLabel;
    @InjectView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @InjectView(R.id.humidityValue) TextView mHumidityValue;
    @InjectView(R.id.precipValue) TextView mPrecipValue;
    @InjectView(R.id.summaryLabel) TextView mSummaryLabel;
    @InjectView(R.id.iconImageView) ImageView mIconImageView;
    @InjectView(R.id.refreshImageView) ImageView mRefreshImageView;
    @InjectView(R.id.progressBar) ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_main_fragment, container, false);
        ButterKnife.inject(this, view);
        setRetainInstance(true);

        mProgressBar.setVisibility(View.INVISIBLE);
        final double latitude = 37.8267;
        final double longitude = -122.423;

        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast(latitude, longitude);
            }
        });

        getForecast(latitude, longitude);

        if (MainActivity.isTablet) {
            hourlyButton.setVisibility(View.GONE);
            dailyButton.setText(R.string.DailyHourlyForecast);
        }

        return view;
    }

    private void toggleRefresh() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplay() {
        Current current = mForecast.getCurrent();

        mTemperatureLabel.setText(current.getTemperature() + "");
        mTimeLabel.setText("At " + current.getFormattedTime() + " it will be");
        mHumidityValue.setText(current.getHumidity() + "");
        mPrecipValue.setText(current.getPrecipChance() + "%");
        mSummaryLabel.setText(current.getSummary());

        Drawable drawable = getResources().getDrawable(current.getIconId());
        mIconImageView.setImageDrawable(drawable);
    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();

        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));

        return forecast;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day[] days = new Day[data.length()];

        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonDay = data.getJSONObject(i);
            Day day = new Day();

            day.setSummary(jsonDay.getString("summary"));
            day.setIcon(jsonDay.getString("icon"));
            day.setTemperatureMax(jsonDay.getDouble("temperatureMax"));
            day.setTime(jsonDay.getLong("time"));
            day.setTimezone(timezone);

            days[i] = day;
        }

        return days;
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");

        Hour[] hours = new Hour[data.length()];

        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonHour = data.getJSONObject(i);
            Hour hour = new Hour();

            hour.setSummary(jsonHour.getString("summary"));
            hour.setIcon(jsonHour.getString("icon"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setTime(jsonHour.getLong("time"));
            hour.setTimezone(timezone);

            hours[i] = hour;
        }

        return hours;
    }


    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON: " + timezone);

        JSONObject currently = forecast.getJSONObject("currently");

        Current current = new Current();
        current.setHumidity(currently.getDouble("humidity"));
        current.setTime(currently.getLong("time"));
        current.setIcon(currently.getString("icon"));
        current.setPrecipChance(currently.getDouble("precipProbability"));
        current.setSummary(currently.getString("summary"));
        current.setTemperature(currently.getDouble("temperature"));
        current.setTimeZone(timezone);

        Log.d(TAG, current.getFormattedTime());

        return current;
    }

    @OnClick(R.id.dailyButton)
    public void startDailyActivity(View view) {
        if (!MainActivity.isTablet) {
            android.support.v4.app.Fragment dailyForecastFragment = new DailyForecastFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArray(DAILY_FORECAST, mForecast.getDailyForecast());
            dailyForecastFragment.setArguments(bundle);
            FragmentManager fm = getFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.placeHolder, dailyForecastFragment).commit();
        }

        else {
            android.support.v4.app.Fragment dualPaneFragment = new DualPaneFragment();
            android.support.v4.app.Fragment dailyForecastFragment = new DailyForecastFragment();
            android.support.v4.app.Fragment hourlyForecastFragment = new HourlyForecastFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelableArray(HOURLY_FORECAST, mForecast.getHourlyForecast());
            bundle.putParcelableArray(DAILY_FORECAST, mForecast.getDailyForecast());
            dailyForecastFragment.setArguments(bundle);
            hourlyForecastFragment.setArguments(bundle);
            FragmentManager fm = getFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.placeHolder, dualPaneFragment);
            fragmentTransaction.replace(R.id.rightPlaceholder, hourlyForecastFragment).replace(R.id.leftPlaceholder, dailyForecastFragment).addToBackStack(null).commit();
        }
    }

    @OnClick(R.id.hourlyButton)
    public void startHourlyActivity(View view) {

        android.support.v4.app.Fragment hourlyForecastFragment = new HourlyForecastFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArray(HOURLY_FORECAST, mForecast.getHourlyForecast());
        hourlyForecastFragment.setArguments(bundle);
        FragmentManager fm = getFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.placeHolder, hourlyForecastFragment).commit();
    }

    public void getForecast(double latitude, double longitude) {
        String apiKey = "d28f052c1124d2fb4fd433e0f130792b"; // TODO: Replace this with your own API key from forecast.io
        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey +
                "/" + latitude + "," + longitude;

        if (isNetworkAvailable()) {
            toggleRefresh();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    //mainActivity.alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });

                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mForecast = parseForecastDetails(jsonData);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            //alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), getString(R.string.network_unavailable_message),
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }
}