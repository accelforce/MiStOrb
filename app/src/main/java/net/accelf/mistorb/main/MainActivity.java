package net.accelf.mistorb.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import net.accelf.mistorb.R;
import net.accelf.mistorb.db.InstancePickUtil;
import net.accelf.mistorb.drawer.DrawerHelper;
import net.accelf.mistorb.login.DomainInputActivity;
import net.accelf.mistorb.login.LoginActivity;
import net.accelf.mistorb.menu.GlobalMenuHelper;
import net.accelf.mistorb.model.Stats;
import net.accelf.mistorb.network.MastodonSidekiqApi;
import net.accelf.mistorb.network.RetrofitHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private InstancePickUtil instancePicker;
    private String selectedDomain;
    private String cookies;
    private MastodonSidekiqApi sidekiqApi;

    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressBar loading;
    private ViewStatsHelper viewStatsHelper;
    private Drawer drawer;

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
        setupToolbar();
        setupDrawer();
        swipeRefreshLayout.setOnRefreshListener(this::refreshStats);
        setupRecyclerView();
        viewStatsHelper.setServerDomain(selectedDomain);
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
        toolbar = findViewById(R.id.activity_main_toolbar);
        recyclerView = findViewById(R.id.activity_main_recycler_view);
        loading = findViewById(R.id.activity_main_loading);
    }

    private void setupDrawer() {
        AccountHeader.OnAccountHeaderListener accountHeaderListener =
                (view, profile, current) -> handleProfileChanged(profile, current);
        Drawer.OnDrawerItemClickListener drawerItemClickListener =
                (view, position, drawerItem) -> handleDrawerItemClick(drawerItem);

        DrawerHelper helper = new DrawerHelper(this);
        helper.initializeImageLoader();
        drawer = helper.buildDrawer(accountHeaderListener, drawerItemClickListener, toolbar);
    }

    private boolean handleProfileChanged(IProfile profile, boolean current) {
        drawer.closeDrawer();
        if (current) {
            return true;
        }
        if (DrawerHelper.DrawerItem.fromId((int) profile.getIdentifier())
                == DrawerHelper.DrawerItem.ITEM_ADD_NEW) {
            requestInstanceDomain();
            return true;
        }
        instancePicker.setSelectedInstance(profile.getName().toString());
        reloadMainActivity();
        return false;
    }

    private void reloadMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }

    private boolean handleDrawerItemClick(IDrawerItem drawerItem) {
        DrawerHelper.DrawerItem id = DrawerHelper.DrawerItem.fromId((int) drawerItem.getIdentifier());
        if (id == null) {
            return false;
        }
        switch (id) {
            case ITEM_RE_LOGIN:
                startReLogin();
                return true;
        }
        return false;
    }

    private void startReLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_INSTANCE_DOMAIN, selectedDomain);
        startActivity(intent);

        finish();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setupRecyclerView() {
        StatsViewAdapter adapter = new StatsViewAdapter();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(
                this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(divider);
        recyclerView.setAdapter(adapter);
        viewStatsHelper = new ViewStatsHelper(this, adapter);
    }

    private void refreshStats() {
        swipeRefreshLayout.setRefreshing(true);
        sidekiqApi.getStats().enqueue(refreshStatsCallback);
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
        recyclerView.setVisibility(View.VISIBLE);
    }
}
