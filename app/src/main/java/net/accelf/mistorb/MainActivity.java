package net.accelf.mistorb;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import net.accelf.mistorb.api.MastodonSidekiqApi;
import net.accelf.mistorb.api.RetrofitHelper;
import net.accelf.mistorb.api.Stats;
import net.accelf.mistorb.util.InstancePickUtil;
import net.accelf.mistorb.viewhelper.GlobalMenuHelper;
import net.accelf.mistorb.viewhelper.ViewStatsHelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private InstancePickUtil instancePicker;
    private String selectedDomain;
    private String cookies;
    private MastodonSidekiqApi sidekiqApi;

    private SwipeRefreshLayout swipeRefreshLayout;
    private AppCompatTextView serverInfo;
    private ProgressBar loading;
    private RelativeLayout statsContainer;
    private ViewStatsHelper viewStatsHelper;

    private Callback<Stats> fetchStatsCallback;
    private Callback<Stats> refreshStatsCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instancePicker = new InstancePickUtil(this);

        if (!checkLoggedIn()) {
            requestInstanceDomain();
            return;
        }

        getCookies();
        setupLayoutVariables();
        setupSwipeRefreshLayout();
        setupServerInfo();
        sidekiqApi = RetrofitHelper.generateMastodonSidekiqApi(selectedDomain, cookies);
        setupCallback();
        fetchStats();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shared, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (GlobalMenuHelper.onGlobalMenuItemSelected(this, item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkLoggedIn() {
        if (instancePicker.selectAnyway()) {
            selectedDomain = instancePicker.selectedInstance();
            return true;
        }
        return false;
    }

    private void requestInstanceDomain() {
        Intent intent = new Intent(this, DomainInputActivity.class);
        startActivity(intent);
        finish();
    }

    private void getCookies() {
        cookies = instancePicker.getCookies();
    }

    private void setupLayoutVariables() {
        swipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        serverInfo = findViewById(R.id.activity_main_server_info);
        loading = findViewById(R.id.activity_main_loading);
        statsContainer = findViewById(R.id.activity_main_stats_container);
        viewStatsHelper = new ViewStatsHelper(statsContainer);
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this::refreshStats);
    }

    private void refreshStats() {
        swipeRefreshLayout.setRefreshing(true);
        sidekiqApi.getStats().enqueue(refreshStatsCallback);
    }

    private void setupServerInfo() {
        serverInfo.setText(selectedDomain);
    }

    private void setupCallback() {
        fetchStatsCallback = new Callback<Stats>() {
            @Override
            public void onResponse(@NonNull Call<Stats> call, @NonNull Response<Stats> response) {
                if (response.body() == null) {
                    viewStats();
                    viewStatsHelper.updateStats(new Stats());
                    return;
                }
                viewStats();
                viewStatsHelper.updateStats(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Stats> call, @NonNull Throwable t) {
                viewStats();
                viewStatsHelper.updateStats(new Stats());
            }
        };
        refreshStatsCallback = new Callback<Stats>() {
            @Override
            public void onResponse(@NonNull Call<Stats> call, @NonNull Response<Stats> response) {
                if (response.body() != null) {
                    viewStatsHelper.updateStats(response.body());
                }
                viewStats();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<Stats> call, @NonNull Throwable t) {
                viewStats();
                swipeRefreshLayout.setRefreshing(false);
            }
        };
    }

    private void fetchStats() {
        sidekiqApi.getStats().enqueue(fetchStatsCallback);
    }

    private void viewStats() {
        loading.setVisibility(View.INVISIBLE);
        statsContainer.setVisibility(View.VISIBLE);
    }
}
