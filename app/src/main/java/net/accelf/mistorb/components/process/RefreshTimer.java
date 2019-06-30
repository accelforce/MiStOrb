package net.accelf.mistorb.components.process;

import android.os.Handler;
import android.widget.TextView;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.Date;

public class RefreshTimer implements Runnable {

    private Handler handler;
    private TextView target;
    private Date startedAt;

    RefreshTimer(Handler handler, TextView target, Date startedAt) {
        this.handler = handler;
        this.target = target;
        this.startedAt = startedAt;
    }

    @Override
    public void run() {
        target.setText(calcTimeDiff(startedAt));
        handler.postDelayed(this, 1000);
    }

    private String calcTimeDiff(Date startedAt) {
        return DurationFormatUtils.formatDuration(System.currentTimeMillis() - startedAt.getTime(),
                "HH:mm:ss", true);
    }

}
