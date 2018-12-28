package net.accelf.mistorb.viewhelper;

import android.view.View;

import net.accelf.mistorb.R;
import net.accelf.mistorb.api.Sidekiq;
import net.accelf.mistorb.api.Stats;

import java.text.NumberFormat;

import androidx.appcompat.widget.AppCompatTextView;

public class ViewStatsHelper {

    private View root;

    private AppCompatTextView statsDataProcessed;
    private AppCompatTextView statsDataFailed;
    private AppCompatTextView statsDataBusy;
    private AppCompatTextView statsDataProcesses;
    private AppCompatTextView statsDataEnqueued;
    private AppCompatTextView statsDataScheduled;
    private AppCompatTextView statsDataRetries;
    private AppCompatTextView statsDataDead;

    public ViewStatsHelper(View root) {
        this.root = root;
        setupLayoutVariables();
    }

    private void setupLayoutVariables() {
        statsDataProcessed = root.findViewById(R.id.activity_main_stats_data_processed);
        statsDataFailed = root.findViewById(R.id.activity_main_stats_data_failed);
        statsDataBusy = root.findViewById(R.id.activity_main_stats_data_busy);
        statsDataProcesses = root.findViewById(R.id.activity_main_stats_data_processes);
        statsDataEnqueued = root.findViewById(R.id.activity_main_stats_data_enqueued);
        statsDataScheduled = root.findViewById(R.id.activity_main_stats_data_scheduled);
        statsDataRetries = root.findViewById(R.id.activity_main_stats_data_retries);
        statsDataDead = root.findViewById(R.id.activity_main_stats_data_dead);
    }

    public void updateStats(Stats stats) {
        Sidekiq sidekiq = stats.sidekiq;
        NumberFormat numberFormat = NumberFormat.getNumberInstance();

        statsDataProcessed.setText(numberFormat.format(sidekiq.processed));
        statsDataFailed.setText(numberFormat.format(sidekiq.failed));
        statsDataBusy.setText(numberFormat.format(sidekiq.busy));
        statsDataProcesses.setText(numberFormat.format(sidekiq.processes));
        statsDataEnqueued.setText(numberFormat.format(sidekiq.enqueued));
        statsDataScheduled.setText(numberFormat.format(sidekiq.scheduled));
        statsDataRetries.setText(numberFormat.format(sidekiq.retries));
        statsDataDead.setText(numberFormat.format(sidekiq.dead));
    }
}
