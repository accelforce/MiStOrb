package net.accelf.mistorb.retries;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import net.accelf.mistorb.retries.components.retry.RetryModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private Callback<ResponseBody> fetchRetriesCallback;
    private Callback<ResponseBody> refreshRetriesCallback;

    private String authenticityToken;
    private List<RetryModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retries);

        InstancePickUtil instancePicker = new InstancePickUtil(this);
        sidekiqApi = RetrofitHelper.generateMastodonSidekiqApi(instancePicker.selectedInstance(), instancePicker.getCookies());

        setupLayoutVariables();
        setupToolbar();
        setupSwipeRefreshLayout();
        setupRecyclerView();
        setupCallback();
        page = 0;
        list.clear();
        fetchRetries(fetchRetriesCallback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_retries, menu);
        getMenuInflater().inflate(R.menu.menu_shared, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        setOptionsItemState(menu, R.id.menu_retries_delete_all, authenticityToken != null);
        setOptionsItemState(menu, R.id.menu_retries_retry_all, authenticityToken != null);
        setOptionsItemState(menu, R.id.menu_retries_kill_all, authenticityToken != null);
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
            case R.id.menu_retries_delete_all: {
                sidekiqApi.deleteAllRetries(authenticityToken)
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
            case R.id.menu_retries_retry_all: {
                sidekiqApi.retryAllRetries(authenticityToken)
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
            case R.id.menu_retries_kill_all: {
                sidekiqApi.killAllRetries(authenticityToken)
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

    private void setupLayoutVariables() {
        swipeRefreshLayout = findViewById(R.id.activity_retries_swipe_refresh_layout);
        toolbar = findViewById(R.id.activity_retries_toolbar);
        recyclerView = findViewById(R.id.activity_retries_recycler_view);
        loading = findViewById(R.id.activity_retries_loading);
        loadingTextView = findViewById(R.id.activity_retries_loading_text_view);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this::refreshRetries);
    }

    private void setupRecyclerView() {
        adapter = new RetriesViewAdapter();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(
                this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(divider);
        recyclerView.setAdapter(adapter);
    }

    private void setupCallback() {
        fetchRetriesCallback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.body() == null) {
                    viewRetries();
                    return;
                }
                try {
                    List<RetryModel> fetched = parseDocument(response.body().string());
                    list.addAll(fetched);

                    if (fetched.size() >= FETCH_ONCE) {
                        fetchRetries(fetchRetriesCallback);
                    } else {
                        viewRetries();
                        adapter.setList(list);
                    }
                } catch (IOException e) {
                    viewRetries();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                viewRetries();
            }
        };
        refreshRetriesCallback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.body() == null) {
                    viewRetries();
                    return;
                }
                try {
                    List<RetryModel> fetched = parseDocument(response.body().string());
                    list.addAll(fetched);

                    if (fetched.size() >= FETCH_ONCE) {
                        fetchRetries(refreshRetriesCallback);
                    } else {
                        viewRetries();
                        adapter.setList(list);
                    }
                } catch (IOException e) {
                    viewRetries();
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                viewRetries();
                swipeRefreshLayout.setRefreshing(false);
            }
        };
    }

    private List<RetryModel> parseDocument(String body) {
        Document document = Jsoup.parse(body);

        Element input = document.select("form input[name=\"authenticity_token\"]").first();
        authenticityToken = input.attr("value");
        invalidateOptionsMenu();

        return RetryModel.toRetries(document);
    }

    private void fetchRetries(Callback<ResponseBody> callback) {
        page++;
        sidekiqApi.getRetries(page, FETCH_ONCE).enqueue(callback);
        updateLoadingTextView();
    }

    private void refreshRetries() {
        page = 0;
        list.clear();
        swipeRefreshLayout.setRefreshing(true);
        fetchRetries(refreshRetriesCallback);
    }

    private void viewRetries() {
        loading.setVisibility(View.INVISIBLE);
        loadingTextView.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void updateLoadingTextView() {
        loadingTextView.setText(getString(R.string.activity_retries_loading_text_view, page,
                list.size() + 1, FETCH_ONCE * page));
    }

}
