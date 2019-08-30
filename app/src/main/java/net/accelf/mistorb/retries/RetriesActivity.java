package net.accelf.mistorb.retries;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.accelf.mistorb.R;
import net.accelf.mistorb.db.InstancePickUtil;
import net.accelf.mistorb.menu.GlobalMenuHelper;
import net.accelf.mistorb.network.MastodonSidekiqApi;
import net.accelf.mistorb.network.RetrofitHelper;
import net.accelf.mistorb.retries.components.retry.RetryModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static net.accelf.mistorb.menu.MenuUtil.setOptionsItemState;
import static net.accelf.mistorb.view.RecyclerViewHelper.setupRecyclerView;

public class RetriesActivity extends AppCompatActivity {

    private MastodonSidekiqApi sidekiqApi;

    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RetriesViewAdapter adapter;
    private ProgressBar loading;
    private TextView loadingTextView;

    private static final int FETCH_ONCE = 500;
    private int page;

    private String authenticityToken;
    private List<RetryModel> list = new ArrayList<>();

    public static Intent createIntent(Context context) {
        return new Intent(context, RetriesActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retries);

        InstancePickUtil instancePicker = new InstancePickUtil(this);
        sidekiqApi = RetrofitHelper.generateMastodonSidekiqApi(instancePicker.selectedInstance(), instancePicker.getCookies());

        setupLayoutVariables();
        setSupportActionBar(toolbar);
        swipeRefreshLayout.setOnRefreshListener(this::refreshRetries);
        adapter = new RetriesViewAdapter();
        setupRecyclerView(recyclerView, adapter);
        page = 0;
        list.clear();
        fetchRetries(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_retries, menu);
        getMenuInflater().inflate(R.menu.menu_shared, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Resources resources = getResources();
        Resources.Theme theme = getTheme();
        setOptionsItemState(menu, R.id.menu_retries_delete_all, authenticityToken != null, resources, theme);
        setOptionsItemState(menu, R.id.menu_retries_retry_all, authenticityToken != null, resources, theme);
        setOptionsItemState(menu, R.id.menu_retries_kill_all, authenticityToken != null, resources, theme);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_retries_delete_all: {
                sidekiqApi.deleteAllRetries(authenticityToken)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
                return true;
            }
            case R.id.menu_retries_retry_all: {
                sidekiqApi.retryAllRetries(authenticityToken)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
                return true;
            }
            case R.id.menu_retries_kill_all: {
                sidekiqApi.killAllRetries(authenticityToken)
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

    private void setupLayoutVariables() {
        swipeRefreshLayout = findViewById(R.id.activity_retries_swipe_refresh_layout);
        toolbar = findViewById(R.id.activity_retries_toolbar);
        recyclerView = findViewById(R.id.activity_retries_recycler_view);
        loading = findViewById(R.id.activity_retries_loading);
        loadingTextView = findViewById(R.id.activity_retries_loading_text_view);
    }

    private List<RetryModel> parseDocument(String body) {
        Document document = Jsoup.parse(body);

        Element input = document.select("form input[name=\"authenticity_token\"]").first();
        if (input != null) {
            authenticityToken = input.attr("value");
            invalidateOptionsMenu();
        }

        return RetryModel.toRetries(document);
    }

    @SuppressLint("CheckResult")
    private void fetchRetries(boolean refresh) {
        page++;
        //noinspection ResultOfMethodCallIgnored
        sidekiqApi.getRetries(page, FETCH_ONCE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> onFetchRetriesSuccess(response, refresh),
                        throwable -> onFetchRetriesFail(throwable, refresh));
        loadingTextView.setText(getString(R.string.activity_retries_loading_text_view, page,
                list.size() + 1, FETCH_ONCE * page));
    }

    private void onFetchRetriesSuccess(Response<ResponseBody> response, boolean refresh) {
        if (response.body() != null) {
            try {
                List<RetryModel> fetched = parseDocument(response.body().string());
                list.addAll(fetched);

                if (fetched.size() >= FETCH_ONCE) {
                    fetchRetries(refresh);
                } else {
                    viewRetries();
                    adapter.setList(list);
                }
            } catch (IOException e) {
                viewRetries();
                e.printStackTrace();
            }
        } else {
            viewRetries();
        }

        if (refresh) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void onFetchRetriesFail(Throwable throwable, boolean refresh) {
        throwable.printStackTrace();
        viewRetries();

        if (refresh) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void refreshRetries() {
        page = 0;
        list.clear();
        swipeRefreshLayout.setRefreshing(true);
        fetchRetries(true);
    }

    private void viewRetries() {
        loading.setVisibility(View.INVISIBLE);
        loadingTextView.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

}
