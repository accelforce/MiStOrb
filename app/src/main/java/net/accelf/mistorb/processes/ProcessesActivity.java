package net.accelf.mistorb.processes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
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

import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static net.accelf.mistorb.menu.MenuUtil.setOptionsItemState;
import static net.accelf.mistorb.view.RecyclerViewHelper.setupRecyclerView;

public class ProcessesActivity extends AppCompatActivity {

    private MastodonSidekiqApi sidekiqApi;

    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProcessesViewAdapter adapter;
    private ProgressBar loading;
    private ConstraintLayout bottomSheet;
    private BottomSheetUtil bottomSheetUtil;

    private String authenticityToken;

    public static Intent createIntent(Context context) {
        return new Intent(context, ProcessesActivity.class);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processes);

        InstancePickUtil instancePicker = new InstancePickUtil(this);
        sidekiqApi = RetrofitHelper.generateMastodonSidekiqApi(instancePicker.selectedInstance(), instancePicker.getCookies());

        setupLayoutVariables();
        setSupportActionBar(toolbar);
        swipeRefreshLayout.setOnRefreshListener(this::refreshProcesses);
        setupBottomSheet();
        adapter = new ProcessesViewAdapter(bottomSheetUtil);
        setupRecyclerView(recyclerView, adapter);
        //noinspection ResultOfMethodCallIgnored
        sidekiqApi.getProcesses()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> onFetchProcessesSuccess(response, false),
                        throwable -> onFetchProcessesFail(throwable, false));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_processes, menu);
        getMenuInflater().inflate(R.menu.menu_shared, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Resources resources = getResources();
        Resources.Theme theme = getTheme();
        setOptionsItemState(menu, R.id.menu_processes_quiet_all, authenticityToken != null, resources, theme);
        setOptionsItemState(menu, R.id.menu_processes_kill_all, authenticityToken != null, resources, theme);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_processes_quiet_all: {
                sidekiqApi.quietAllProcesses(authenticityToken, true)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
                return true;
            }
            case R.id.menu_processes_kill_all: {
                sidekiqApi.killAllProcesses(authenticityToken, true)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
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

    public void setupBottomSheet() {
        bottomSheetUtil = new BottomSheetUtil(bottomSheet, recyclerView, sidekiqApi);
        bottomSheetUtil.close();
    }

    private void onFetchProcessesSuccess(Response<ResponseBody> response, boolean refresh) {
        viewProcesses();
        if (response.body() != null) {
            try {
                parseDocument(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (refresh) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void onFetchProcessesFail(Throwable throwable, boolean refresh) {
        throwable.printStackTrace();
        viewProcesses();

        if (refresh) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void parseDocument(String body) {
        Document document = Jsoup.parse(body);

        Element input = document.select("form input[name=\"authenticity_token\"]").first();
        authenticityToken = input.attr("value");
        invalidateOptionsMenu();

        adapter.setList(ProcessModel.toProcesses(document));
    }

    @SuppressLint("CheckResult")
    private void refreshProcesses() {
        swipeRefreshLayout.setRefreshing(true);
        //noinspection ResultOfMethodCallIgnored
        sidekiqApi.getProcesses()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> onFetchProcessesSuccess(response, true),
                        throwable -> onFetchProcessesFail(throwable, true));
    }

    private void viewProcesses() {
        loading.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

}
