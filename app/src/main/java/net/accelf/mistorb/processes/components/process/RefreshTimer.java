package net.accelf.mistorb.processes.components.process;

import android.os.Handler;
import android.widget.TextView;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.Date;

public class RefreshTimer implements Runnable {

    private Handler handler;
    private TextView target;
    private Date startedAt;

    RefreshTimer(Handler handler, TextView target) {
        this.handler = handler;
        this.target = target;
    }

    void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    @Override
    public void run() {
        if (startedAt != null) {
            target.setText(calcTimeDiff(startedAt));
            handler.postDelayed(this, 1000);
        }
    }

    private String calcTimeDiff(Date startedAt) {
        return DurationFormatUtils.formatDuration(System.currentTimeMillis() - startedAt.getTime(),
                "HH:mm:ss", true);
    }

}
