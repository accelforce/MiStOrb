package net.accelf.mistorb.model;

import com.google.gson.annotations.SerializedName;

public class Sidekiq {

    public long processed;
    public long failed;
    public long busy;
    public long processes;
    public long enqueued;
    public long scheduled;
    public long retries;
    public long dead;
    @SerializedName("default_latency")
    public long defaultLatency;
}
