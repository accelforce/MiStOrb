package net.accelf.mistorb;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.accelf.mistorb.api.RetrofitHelper;
import net.accelf.mistorb.util.InstancePickUtil;
import net.accelf.mistorb.viewhelper.GlobalMenuHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private WebView webView;

    private String instanceDomain;
    private InstancePickUtil instancePicker;

    public static final String EXTRA_INSTANCE_DOMAIN = "instance_domain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupLayoutVariables();

        instancePicker = new InstancePickUtil(this);

        instanceDomain = getDomainFromIntent(getIntent());
        setupWebView();
        loadLoginPage(buildLoginUrl(instanceDomain));
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

    private String getDomainFromIntent(Intent intent) {
        return intent.getStringExtra(EXTRA_INSTANCE_DOMAIN);
    }

    private void setupLayoutVariables() {
        webView = findViewById(R.id.activity_login_web_view);
    }

    private static String buildLoginUrl(String instanceDomain) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority(instanceDomain)
                .path("/auth/sign_in")
                .build();
        return uri.toString();
    }

    private void setupWebView() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (checkSucceedPath(request.getUrl().getPath())) {
                    completeLogin();
                    return true;
                }
                return false;
            }
        });
    }

    private void loadLoginPage(String url) {
        webView.loadUrl(url);
    }

    private static boolean checkSucceedPath(@Nullable String path) {
        return "/".equals(path);
    }

    private void completeLogin() {
        CookieManager cookieManager = CookieManager.getInstance();
        String cookieHost = RetrofitHelper.generateEndpoint(instanceDomain).toString();
        String cookie = cookieManager.getCookie(cookieHost);
        instancePicker.addNewInstance(instanceDomain, cookie);

        startMainActivity();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
