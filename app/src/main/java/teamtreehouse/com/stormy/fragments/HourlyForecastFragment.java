package teamtreehouse.com.stormy.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import teamtreehouse.com.stormy.R;
import teamtreehouse.com.stormy.adapters.HourAdapter;
import teamtreehouse.com.stormy.ui.AlertDialogFragment;
import teamtreehouse.com.stormy.ui.DailyForecastActivity;
import teamtreehouse.com.stormy.ui.HourlyForecastActivity;
import teamtreehouse.com.stormy.ui.MainActivity;
import teamtreehouse.com.stormy.weather.Current;
import teamtreehouse.com.stormy.weather.Day;
import teamtreehouse.com.stormy.weather.Forecast;
import teamtreehouse.com.stormy.weather.Hour;


public class HourlyForecastFragment extends android.support.v4.app.Fragment {
    private Forecast mForecast;
    private Hour[] mHours;

    @InjectView(R.id.reyclerView)
    RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_hourly_forecast,container, false);
        ButterKnife.inject(this, view);

        Bundle bundle = this.getArguments();
        Parcelable[] hForecast = bundle.getParcelableArray("HOURLY_FORECAST");
        mHours = Arrays.copyOf(hForecast, hForecast.length, Hour[].class);

        HourAdapter adapter = new HourAdapter(getActivity(), mHours);
        mRecyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        return view;
    }
}


