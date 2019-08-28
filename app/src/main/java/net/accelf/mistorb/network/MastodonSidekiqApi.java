package net.accelf.mistorb.network;

import net.accelf.mistorb.model.Stats;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

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

    @FormUrlEncoded
    @POST("sidekiq/busy")
    Call<Void> quietProcess(@Field("authenticity_token") String authenticityToken,
                            @Field("identity") String identity,
                            @Field("quiet") boolean quiet);

    @FormUrlEncoded
    @POST("sidekiq/busy")
    Call<Void> killProcess(@Field("authenticity_token") String authenticityToken,
                           @Field("identity") String identity,
                           @Field("stop") boolean stop);

    @GET("sidekiq/retries")
    Call<ResponseBody> getRetries(@Query("page") int page, @Query("count") int count);

    @FormUrlEncoded
    @POST("sidekiq/retries/all/delete")
    Call<Void> deleteAllRetries(@Field("authenticity_token") String authenticityToken);

    @FormUrlEncoded
    @POST("sidekiq/retries/all/retry")
    Call<Void> retryAllRetries(@Field("authenticity_token") String authenticityToken);

    @FormUrlEncoded
    @POST("sidekiq/retries/all/kill")
    Call<Void> killAllRetries(@Field("authenticity_token") String authenticityToken);
}
