package net.accelf.mistorb.api;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MastodonSidekiqApi {
    @GET("sidekiq/stats")
    Call<Stats> getStats();
}
