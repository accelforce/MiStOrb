package net.accelf.mistorb.api;

import com.google.gson.annotations.SerializedName;

public class Stats {

    public Sidekiq sidekiq;
    public Redis redis;
    @SerializedName("server_utc_time")
    public String serverUtcTime;

    public Stats() {
        sidekiq = new Sidekiq();
        redis = new Redis();
    }
}
