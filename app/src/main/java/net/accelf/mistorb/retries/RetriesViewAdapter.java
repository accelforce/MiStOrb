package net.accelf.mistorb.retries;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.accelf.mistorb.components.retry.RetryModel;
import net.accelf.mistorb.components.retry.RetryViewHolder;

import java.util.List;

public class RetriesViewAdapter extends RecyclerView.Adapter {

    private List<RetryModel> list;

    void setList(List<RetryModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        return new RetryViewHolder(inflater.inflate(RetryViewHolder.LAYOUT,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RetryViewHolder) holder).onBindItemViewHolder(list.get(position));
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

}
