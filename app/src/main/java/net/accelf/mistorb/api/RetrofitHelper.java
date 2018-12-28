package net.accelf.mistorb.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.accelf.mistorb.BuildConfig;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    public static MastodonSidekiqApi generateMastodonSidekiqApi(String domain, String cookie) {
        HttpUrl url = generateEndpoint(domain);

        Retrofit retrofit = new Retrofit.Builder()
                .client(generateOkHttpClient(url, cookie))
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(generateGson()))
                .build();

        return retrofit.create(MastodonSidekiqApi.class);
    }

    public static HttpUrl generateEndpoint(String domain) {
        return new HttpUrl.Builder()
                .scheme("https")
                .host(domain)
                .build();
    }

    private static Gson generateGson() {
        return new GsonBuilder()
                .setLenient()
                .create();
    }

    private static OkHttpClient generateOkHttpClient(HttpUrl domain, String cookie) {
        return new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY
                                : HttpLoggingInterceptor.Level.NONE))
                .cookieJar(generateCookieJar(domain, cookie))
                .build();
    }

    private static CookieJar generateCookieJar(HttpUrl domain, String cookies) {
        return new SavedCookieJar()
                .setCookies(domain, cookies);
    }

    private static class SavedCookieJar implements CookieJar {

        List<Cookie> cookies;

        CookieJar setCookies(HttpUrl url, String cookieString) {
            if (cookies == null) {
                cookies = new ArrayList<>();
            }
            for (String cookie : cookieString.split(";")) {
                cookies.add(Cookie.parse(url, cookie));
            }
            return this;
        }

        @Override
        public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
            this.cookies = cookies;
        }

        @NonNull
        @Override
        public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
            if (cookies == null) {
                return new ArrayList<>();
            }
            return cookies;
        }
    }
}
