package net.accelf.mistorb.login;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import net.accelf.mistorb.R;
import net.accelf.mistorb.db.InstancePickUtil;
import net.accelf.mistorb.main.MainActivity;
import net.accelf.mistorb.menu.GlobalMenuHelper;
import net.accelf.mistorb.network.RetrofitHelper;

public class LoginActivity extends AppCompatActivity {

    private WebView webView;

    private String instanceDomain;
    private InstancePickUtil instancePicker;

    public static final String EXTRA_INSTANCE_DOMAIN = "instance_domain";

    public static Intent createIntent(Context context, String instanceDomain) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(EXTRA_INSTANCE_DOMAIN, instanceDomain);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        webView = findViewById(R.id.activity_login_web_view);

        instancePicker = new InstancePickUtil(this);

        instanceDomain = getIntent().getStringExtra(EXTRA_INSTANCE_DOMAIN);
        CookieManager.getInstance()
                .removeAllCookies(null);
        setupWebView();
        webView.loadUrl(buildLoginUrl(instanceDomain));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shared, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (GlobalMenuHelper.onGlobalMenuItemSelected(this, item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                if ("/".equals(request.getUrl().getPath())) {
                    completeLogin();
                    return true;
                }
                return false;
            }
        });
    }

    private void completeLogin() {
        CookieManager cookieManager = CookieManager.getInstance();
        String cookieHost = RetrofitHelper.generateEndpoint(instanceDomain).toString();
        String cookie = cookieManager.getCookie(cookieHost);
        instancePicker.addNewInstance(instanceDomain, cookie);

        startActivity(MainActivity.createIntent(this, false));
        finish();
    }
}
