package net.accelf.mistorb.main.components.singletitle;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import net.accelf.mistorb.R;

public class SingleTitleViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;

    @LayoutRes
    public static final int LAYOUT = R.layout.item_single_title;

    public SingleTitleViewHolder(View item) {
        super(item);

        textView = item.findViewById(R.id.item_single_title_text_view);
    }

    public void onBindItemViewHolder(SingleTitleModel data) {
        textView.setText(data.title);
        textView.setPadding(data.layoutSize.getIndent(), data.layoutSize.getPadding(),
                data.layoutSize.getPadding(), data.layoutSize.getPadding());
        textView.setTextSize(data.layoutSize.getTextSize());
    }
}
