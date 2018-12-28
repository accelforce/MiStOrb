package net.accelf.mistorb.api;

import com.google.gson.annotations.SerializedName;

public class Redis {

    @SerializedName("redis_version")
    private String redisVersion;
    @SerializedName("uptime_in_days")
    private String uptimeInDays;
    @SerializedName("connected_clients")
    private String connectedClients;
    @SerializedName("used_memory_human")
    private String usedMemoryHuman;
    @SerializedName("used_memory_peak_human")
    private String usedMemoryPeakHuman;
}
