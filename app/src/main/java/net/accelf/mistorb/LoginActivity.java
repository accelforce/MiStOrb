package net.accelf.mistorb;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.accelf.mistorb.util.InstancePickUtil;

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

    private String getDomainFromIntent(Intent intent) {
        return intent.getStringExtra(EXTRA_INSTANCE_DOMAIN);
    }

    private void setupLayoutVariables() {
        webView = findViewById(R.id.login_web_view);
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
        String cookie = cookieManager.getCookie(instanceDomain);
        instancePicker.addNewInstance(instanceDomain, cookie);

        startMainActivity();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
