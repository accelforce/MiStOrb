package net.accelf.mistorb.main;

import android.content.Context;

import net.accelf.mistorb.R;
import net.accelf.mistorb.main.components.datavalue.DataValueModel;
import net.accelf.mistorb.main.components.singletitle.SingleTitleModel;
import net.accelf.mistorb.main.listlayout.LayoutSize;
import net.accelf.mistorb.model.Sidekiq;
import net.accelf.mistorb.model.Stats;
import net.accelf.mistorb.processes.ProcessesActivity;
import net.accelf.mistorb.retries.RetriesActivity;

import java.text.NumberFormat;
import java.util.List;

class ViewStatsHelper {

    private Context context;
    private StatsViewAdapter adapter;

    ViewStatsHelper(Context context, StatsViewAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
    }

    private static void updateListItem(List<Object> list, int index, Object item) {
        while (true) {
            if (index > list.size()) {
                list.add(new SingleTitleModel("", new LayoutSize(0)));
            } else {
                break;
            }
        }

        if (index == list.size()) {
            list.add(item);
            return;
        }

        list.set(index, item);
    }

    void setServerDomain(String domain) {
        List<Object> list = adapter.getList();
        updateListItem(list, 0, new SingleTitleModel(domain, new LayoutSize(0)));
        adapter.setList(list);
    }

    void updateStats() {
        updateStats(new Stats());
    }

    void updateStats(Stats data) {
        List<Object> list = adapter.getList();
        NumberFormat format = NumberFormat.getNumberInstance();

        Sidekiq sidekiq = data.sidekiq;
        updateListItem(list, 1, new SingleTitleModel(context.getString(R.string.activity_main_stats_sidekiq), new LayoutSize(1)));
        updateListItem(list, 2, new DataValueModel(context.getString(R.string.activity_main_stats_processed),
                format.format(sidekiq.processed), null, new LayoutSize(2)));
        updateListItem(list, 3, new DataValueModel(context.getString(R.string.activity_main_stats_failed),
                format.format(sidekiq.failed), null, new LayoutSize(2)));
        updateListItem(list, 4, new DataValueModel(context.getString(R.string.activity_main_stats_busy),
                format.format(sidekiq.busy), null, new LayoutSize(2)));
        updateListItem(list, 5, new DataValueModel(context.getString(R.string.activity_main_stats_processes),
                format.format(sidekiq.processes),
                v -> context.startActivity(ProcessesActivity.createIntent(v.getContext())),
                new LayoutSize(2)));
        updateListItem(list, 6, new DataValueModel(context.getString(R.string.activity_main_stats_enqueued),
                format.format(sidekiq.enqueued), null, new LayoutSize(2)));
        updateListItem(list, 7, new DataValueModel(context.getString(R.string.activity_main_stats_scheduled),
                format.format(sidekiq.scheduled), null, new LayoutSize(2)));
        updateListItem(list, 8, new DataValueModel(context.getString(R.string.activity_main_stats_retries),
                format.format(sidekiq.retries),
                v -> context.startActivity(RetriesActivity.createIntent(v.getContext())),
                new LayoutSize(2)));
        updateListItem(list, 9, new DataValueModel(context.getString(R.string.activity_main_stats_dead),
                format.format(sidekiq.dead), null, new LayoutSize(2)));

        adapter.setList(list);
    }

}
