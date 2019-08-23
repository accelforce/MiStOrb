package net.accelf.mistorb.processes;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.accelf.mistorb.processes.components.process.ProcessModel;
import net.accelf.mistorb.processes.components.process.ProcessViewHolder;

import java.util.List;

public class ProcessesViewAdapter extends RecyclerView.Adapter {

    private List<ProcessModel> list;

    void setList(List<ProcessModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        return new ProcessViewHolder(inflater.inflate(ProcessViewHolder.LAYOUT,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ProcessViewHolder) holder).onBindItemViewHolder(list.get(position));
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

}
