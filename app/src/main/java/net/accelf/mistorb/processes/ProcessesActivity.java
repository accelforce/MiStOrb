package net.accelf.mistorb.processes;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.accelf.mistorb.R;
import net.accelf.mistorb.db.InstancePickUtil;
import net.accelf.mistorb.menu.GlobalMenuHelper;
import net.accelf.mistorb.network.MastodonSidekiqApi;
import net.accelf.mistorb.network.RetrofitHelper;
import net.accelf.mistorb.processes.components.process.ProcessModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProcessesActivity extends AppCompatActivity {

    private MastodonSidekiqApi sidekiqApi;

    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProcessesViewAdapter adapter;
    private ProgressBar loading;
    private ConstraintLayout bottomSheet;
    private BottomSheetUtil bottomSheetUtil;

    private Callback<ResponseBody> fetchProcessesCallback;
    private Callback<ResponseBody> refreshProcessesCallback;

    private String authenticityToken;

    public static Intent createIntent(Context context) {
        return new Intent(context, ProcessesActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processes);

        InstancePickUtil instancePicker = new InstancePickUtil(this);
        sidekiqApi = RetrofitHelper.generateMastodonSidekiqApi(instancePicker.selectedInstance(), instancePicker.getCookies());

        setupLayoutVariables();
        setupToolbar();
        setupSwipeRefreshLayout();
        setupBottomSheet();
        setupRecyclerView();
        setupCallback();
        fetchProcesses();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_processes, menu);
        getMenuInflater().inflate(R.menu.menu_shared, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        setOptionsItemState(menu, R.id.menu_processes_quiet_all, authenticityToken != null);
        setOptionsItemState(menu, R.id.menu_processes_kill_all, authenticityToken != null);
        return super.onPrepareOptionsMenu(menu);
    }

    private void setOptionsItemState(Menu menu, @IdRes int id, boolean state) {
        MenuItem item = menu.findItem(id);
        item.setEnabled(state);
        Drawable icon = DrawableCompat.wrap(item.getIcon());
        DrawableCompat.setTint(icon, state ? getColor(R.color.colorMenuEnabled) : getColor(R.color.colorMenuDisabled));
        item.setIcon(icon);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_processes_quiet_all: {
                sidekiqApi.quietAllProcesses(authenticityToken, true)
                        .enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            }

                            @Override
                            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            }
                        });
                return true;
            }
            case R.id.menu_processes_kill_all: {
                sidekiqApi.killAllProcesses(authenticityToken, true)
                        .enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            }

                            @Override
                            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            }
                        });
                return true;
            }
        }
        if (GlobalMenuHelper.onGlobalMenuItemSelected(this, item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetUtil.isOpening()) {
            bottomSheetUtil.close();
            return;
        }
        super.onBackPressed();
    }

    private void setupLayoutVariables() {
        swipeRefreshLayout = findViewById(R.id.activity_processes_swipe_refresh_layout);
        toolbar = findViewById(R.id.activity_processes_toolbar);
        recyclerView = findViewById(R.id.activity_processes_recycler_view);
        loading = findViewById(R.id.activity_processes_loading);
        bottomSheet = findViewById(R.id.activity_processes_bottom_sheet);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this::refreshProcesses);
    }

    private void setupRecyclerView() {
        adapter = new ProcessesViewAdapter(bottomSheetUtil);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(
                this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(divider);
        recyclerView.setAdapter(adapter);
    }

    public void setupBottomSheet() {
        bottomSheetUtil = new BottomSheetUtil(bottomSheet, recyclerView, sidekiqApi);
        bottomSheetUtil.close();
    }

    private void setupCallback() {
        fetchProcessesCallback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                viewProcesses();
                if (response.body() == null) {
                    return;
                }
                try {
                    parseDocument(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                viewProcesses();
            }
        };
        refreshProcessesCallback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                viewProcesses();
                if (response.body() == null) {
                    return;
                }
                try {
                    parseDocument(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                viewProcesses();
                swipeRefreshLayout.setRefreshing(false);
            }
        };
    }

    private void parseDocument(String body) {
        Document document = Jsoup.parse(body);

        Element input = document.select("form input[name=\"authenticity_token\"]").first();
        authenticityToken = input.attr("value");
        invalidateOptionsMenu();

        adapter.setList(ProcessModel.toProcesses(document));
    }

    private void fetchProcesses() {
        sidekiqApi.getProcesses().enqueue(fetchProcessesCallback);
    }

    private void refreshProcesses() {
        swipeRefreshLayout.setRefreshing(true);
        sidekiqApi.getProcesses().enqueue(refreshProcessesCallback);
    }

    private void viewProcesses() {
        loading.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

}
