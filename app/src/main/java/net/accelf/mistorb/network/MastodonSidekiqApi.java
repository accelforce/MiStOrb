package net.accelf.mistorb.network;

import net.accelf.mistorb.model.Stats;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MastodonSidekiqApi {
    @GET("sidekiq/stats")
    Single<Response<Stats>> getStats();

    @GET("sidekiq/busy")
    Single<Response<ResponseBody>> getProcesses();

    @FormUrlEncoded
    @POST("sidekiq/busy")
    Single<Response<Void>> quietAllProcesses(@Field("authenticity_token") String authenticityToken,
                                             @Field("quiet") boolean quiet);

    @FormUrlEncoded
    @POST("sidekiq/busy")
    Single<Response<Void>> killAllProcesses(@Field("authenticity_token") String authenticityToken,
                                            @Field("stop") boolean stop);

    @FormUrlEncoded
    @POST("sidekiq/busy")
    Single<Response<Void>> quietProcess(@Field("authenticity_token") String authenticityToken,
                                        @Field("identity") String identity,
                                        @Field("quiet") boolean quiet);

    @FormUrlEncoded
    @POST("sidekiq/busy")
    Single<Response<Void>> killProcess(@Field("authenticity_token") String authenticityToken,
                                       @Field("identity") String identity,
                                       @Field("stop") boolean stop);

    @GET("sidekiq/retries")
    Single<Response<ResponseBody>> getRetries(@Query("page") int page, @Query("count") int count);

    @FormUrlEncoded
    @POST("sidekiq/retries/all/delete")
    Single<Response<Void>> deleteAllRetries(@Field("authenticity_token") String authenticityToken);

    @FormUrlEncoded
    @POST("sidekiq/retries/all/retry")
    Single<Response<Void>> retryAllRetries(@Field("authenticity_token") String authenticityToken);

    @FormUrlEncoded
    @POST("sidekiq/retries/all/kill")
    Single<Response<Void>> killAllRetries(@Field("authenticity_token") String authenticityToken);
}
