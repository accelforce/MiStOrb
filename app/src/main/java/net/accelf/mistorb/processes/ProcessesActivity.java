package net.accelf.mistorb.processes;

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

import net.accelf.mistorb.R;
import net.accelf.mistorb.components.process.ProcessModel;
import net.accelf.mistorb.db.InstancePickUtil;
import net.accelf.mistorb.menu.GlobalMenuHelper;
import net.accelf.mistorb.network.MastodonSidekiqApi;
import net.accelf.mistorb.network.RetrofitHelper;

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

    private Callback<ResponseBody> fetchProcessesCallback;
    private Callback<ResponseBody> refreshProcessesCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processes);

        InstancePickUtil instancePicker = new InstancePickUtil(this);
        sidekiqApi = RetrofitHelper.generateMastodonSidekiqApi(instancePicker.selectedInstance(), instancePicker.getCookies());

        setupLayoutVariables();
        setupToolbar();
        setupSwipeRefreshLayout();
        setupRecyclerView();
        setupCallback();
        fetchProcesses();
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

    private void setupLayoutVariables() {
        swipeRefreshLayout = findViewById(R.id.activity_processes_swipe_refresh_layout);
        toolbar = findViewById(R.id.activity_processes_toolbar);
        recyclerView = findViewById(R.id.activity_processes_recycler_view);
        loading = findViewById(R.id.activity_processes_loading);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this::refreshProcesses);
    }

    private void setupRecyclerView() {
        adapter = new ProcessesViewAdapter();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(
                this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(divider);
        recyclerView.setAdapter(adapter);
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
                    adapter.setList(ProcessModel.toProcesses(response.body().string()));
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
                    adapter.setList(ProcessModel.toProcesses(response.body().string()));
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
