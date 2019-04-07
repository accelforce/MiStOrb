package net.accelf.mistorb.network;

import net.accelf.mistorb.model.Stats;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MastodonSidekiqApi {
    @GET("sidekiq/stats")
    Call<Stats> getStats();
}
