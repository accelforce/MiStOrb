package net.accelf.mistorb.network;

import net.accelf.mistorb.model.Stats;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface MastodonSidekiqApi {
    @GET("sidekiq/stats")
    Call<Stats> getStats();

    @GET("sidekiq/busy")
    Call<ResponseBody> getProcesses();
}
