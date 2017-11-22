package teamtreehouse.com.stormy.fragments;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.Arrays;
import butterknife.ButterKnife;
import butterknife.InjectView;
import teamtreehouse.com.stormy.R;
import teamtreehouse.com.stormy.adapters.HourAdapter;
import teamtreehouse.com.stormy.weather.Forecast;
import teamtreehouse.com.stormy.weather.Hour;

public class HourlyForecastFragment extends android.support.v4.app.Fragment{
    private Forecast mForecast;
    private Hour[] mHours;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

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


