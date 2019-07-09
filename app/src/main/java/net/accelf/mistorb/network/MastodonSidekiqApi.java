package net.accelf.mistorb.network;

import net.accelf.mistorb.model.Stats;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MastodonSidekiqApi {
    @GET("sidekiq/stats")
    Call<Stats> getStats();

    @GET("sidekiq/busy")
    Call<ResponseBody> getProcesses();

    @FormUrlEncoded
    @POST("sidekiq/busy")
    Call<Void> quietAllProcesses(@Field("authenticity_token") String authenticityToken,
                                 @Field("quiet") boolean quiet);

    @FormUrlEncoded
    @POST("sidekiq/busy")
    Call<Void> killAllProcesses(@Field("authenticity_token") String authenticityToken,
                                @Field("stop") boolean stop);
}
