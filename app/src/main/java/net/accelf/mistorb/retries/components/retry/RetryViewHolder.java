package net.accelf.mistorb.retries.components.retry;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import net.accelf.mistorb.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class RetryViewHolder extends RecyclerView.ViewHolder {

    @LayoutRes
    public static final int LAYOUT = R.layout.item_retry;
    private static SimpleDateFormat dateFormat =
            new SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault());
    private View root;
    private TextView queueTextView;
    private TextView workerTextView;
    private TextView countTextView;
    private TextView timeTextView;
    private TextView errorTextView;

    public RetryViewHolder(View item) {
        super(item);

        root = item;
        queueTextView = item.findViewById(R.id.item_retry_queue_text_view);
        workerTextView = item.findViewById(R.id.item_retry_worker_text_view);
        countTextView = item.findViewById(R.id.item_retry_count_text_view);
        timeTextView = item.findViewById(R.id.item_retry_time_text_view);
        errorTextView = item.findViewById(R.id.item_retry_error_text_view);
    }

    public void onBindItemViewHolder(RetryModel data) {
        queueTextView.setText(data.queue);
        workerTextView.setText(data.worker);
        countTextView.setText(String.format(root.getContext().getString(R.string.item_retry_count),
                data.count));
        timeTextView.setText(dateFormat.format(data.retryAt));
        errorTextView.setText(data.error);
    }

}
