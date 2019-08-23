package net.accelf.mistorb.processes.components.process;

import android.content.res.Resources;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import net.accelf.mistorb.R;
import net.accelf.mistorb.compat.AndroidApiCompat;

public class ProcessViewHolder extends RecyclerView.ViewHolder {

    @LayoutRes
    public static final int LAYOUT = R.layout.item_process;
    private View root;
    private Button statusButton;
    private TextView idTextView;
    private TextView directoryTextView;
    private TextView timeTextView;
    private TextView queuesTextView;
    private TextView runningTextView;
    private TextView threadsTextView;

    private RefreshTimer timer;

    public ProcessViewHolder(View item) {
        super(item);

        root = item;
        statusButton = item.findViewById(R.id.item_process_status_button);
        idTextView = item.findViewById(R.id.item_process_id_text_view);
        directoryTextView = item.findViewById(R.id.item_process_directory_text_view);
        timeTextView = item.findViewById(R.id.item_process_time_text_view);
        queuesTextView = item.findViewById(R.id.item_process_queues_text_view);
        runningTextView = item.findViewById(R.id.item_process_running_text_view);
        threadsTextView = item.findViewById(R.id.item_process_threads_text_view);

        initializeTimer();
    }

    public void onBindItemViewHolder(ProcessModel data) {
        setStatus(root.getResources(), root.getContext().getTheme(), statusButton, data.status);
        idTextView.setText(data.id);
        directoryTextView.setText(data.directory);
        timer.setStartedAt(data.startedAt);
        queuesTextView.setText(data.queues);
        runningTextView.setText(data.running);
        threadsTextView.setText(data.threads);
    }

    private void setStatus(Resources resources, Resources.Theme theme, Button statusButton, ProcessModel.Status status) {
        switch (status) {
            case BUSY: {
                statusButton.setTextColor(AndroidApiCompat.getColor(resources, theme, R.color.colorProcessStatusBusy));
                break;
            }
            case RUNNING: {
                statusButton.setTextColor(AndroidApiCompat.getColor(resources, theme, R.color.colorProcessStatusRunning));
                break;
            }
            case QUIET: {
                statusButton.setTextColor(AndroidApiCompat.getColor(resources, theme, R.color.colorProcessStatusQuiet));
                break;
            }
        }
    }

    private void initializeTimer() {
        Handler timerHandler = new Handler();
        timer = new RefreshTimer(timerHandler, timeTextView);
        timerHandler.post(timer);
    }

}
