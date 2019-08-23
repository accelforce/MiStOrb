package net.accelf.mistorb.main;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.accelf.mistorb.main.components.datavalue.DataValueModel;
import net.accelf.mistorb.main.components.datavalue.DataValueViewHolder;
import net.accelf.mistorb.main.components.singletitle.SingleTitleModel;
import net.accelf.mistorb.main.components.singletitle.SingleTitleViewHolder;

import java.util.ArrayList;
import java.util.List;

public class StatsViewAdapter extends RecyclerView.Adapter {

    private List<Object> list;

    private static final int ID_SINGLE_TITLE = 1;
    private static final int ID_DATA_VALUE = 2;

    List<Object> getList() {
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    void setList(List<Object> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = list.get(position);
        if (item instanceof SingleTitleModel) {
            return ID_SINGLE_TITLE;
        } else if (item instanceof DataValueModel) {
            return ID_DATA_VALUE;
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            default:
            case ID_SINGLE_TITLE: {
                return new SingleTitleViewHolder(inflater.inflate(SingleTitleViewHolder.LAYOUT,
                        parent, false));
            }
            case ID_DATA_VALUE: {
                return new DataValueViewHolder(inflater.inflate(DataValueViewHolder.LAYOUT,
                        parent, false));
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = list.get(position);
        switch (getItemViewType(position)) {
            default:
            case ID_SINGLE_TITLE: {
                ((SingleTitleViewHolder) holder).onBindItemViewHolder((SingleTitleModel) item);
                break;
            }
            case ID_DATA_VALUE: {
                ((DataValueViewHolder) holder).onBindItemViewHolder((DataValueModel) item);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
