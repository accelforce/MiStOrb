package net.accelf.mistorb.model;

import com.google.gson.annotations.SerializedName;

public class Redis {

    @SerializedName("redis_version")
    public String redisVersion;
    @SerializedName("uptime_in_days")
    public String uptimeInDays;
    @SerializedName("connected_clients")
    public String connectedClients;
    @SerializedName("used_memory_human")
    public String usedMemoryHuman;
    @SerializedName("used_memory_peak_human")
    public String usedMemoryPeakHuman;
}
