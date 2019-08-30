package net.accelf.mistorb.network;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.accelf.mistorb.BuildConfig;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    public static MastodonSidekiqApi generateMastodonSidekiqApi(String domain, String cookie) {
        HttpUrl url = generateEndpoint(domain);

        Retrofit retrofit = new Retrofit.Builder()
                .client(generateOkHttpClient(url, cookie))
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(generateGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .build();

        return retrofit.create(MastodonSidekiqApi.class);
    }

    public static HttpUrl generateEndpoint(String domain) {
        HttpUrl.Builder builder = new HttpUrl.Builder()
                .scheme("https");
        if (domain.contains(":")) {
            String[] hostAndPort = domain.split(":");
            builder.host(hostAndPort[0])
                    .port(Integer.parseInt(hostAndPort[1]));
        } else {
            builder.host(domain);
        }
        return builder.build();
    }

    private static Gson generateGson() {
        return new GsonBuilder()
                .setLenient()
                .create();
    }

    private static OkHttpClient generateOkHttpClient(HttpUrl domain, String cookie) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BASIC
                : HttpLoggingInterceptor.Level.NONE);
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
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
