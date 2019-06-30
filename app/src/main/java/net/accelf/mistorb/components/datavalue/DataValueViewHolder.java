package net.accelf.mistorb.components.datavalue;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import net.accelf.mistorb.R;

public class DataValueViewHolder extends RecyclerView.ViewHolder {

    private View root;
    private TextView titleTextView;
    private TextView valueTextView;

    @LayoutRes
    public static final int LAYOUT = R.layout.item_data_value;

    public DataValueViewHolder(View item) {
        super(item);

        root = item;
        titleTextView = item.findViewById(R.id.item_data_value_title_text_view);
        valueTextView = item.findViewById(R.id.item_data_value_value_text_view);
    }

    public void onBindItemViewHolder(DataValueModel data) {
        root.setOnClickListener(data.onClickListener);

        titleTextView.setText(data.title);
        titleTextView.setPadding(data.layoutSize.getIndent(), data.layoutSize.getPadding(),
                data.layoutSize.getPadding(), data.layoutSize.getPadding());
        titleTextView.setTextSize(data.layoutSize.getTextSize());

        valueTextView.setText(data.value);
        valueTextView.setPadding(data.layoutSize.getPadding(), data.layoutSize.getPadding(),
                data.layoutSize.getPadding(), data.layoutSize.getPadding());
        valueTextView.setTextSize(data.layoutSize.getTextSize());
    }
}
