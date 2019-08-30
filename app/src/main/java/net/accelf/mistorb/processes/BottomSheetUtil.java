package net.accelf.mistorb.processes;

import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;

import net.accelf.mistorb.R;
import net.accelf.mistorb.network.MastodonSidekiqApi;
import net.accelf.mistorb.processes.components.process.ProcessModel;
import net.accelf.mistorb.processes.components.process.ProcessViewHolder;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class BottomSheetUtil {

    private ConstraintLayout bottomSheet;
    private RecyclerView mainView;
    private MastodonSidekiqApi sidekiqApi;
    private BottomSheetBehavior bottomSheetBehavior;
    private ProcessViewHolder viewHolder;
    private MaterialButton quietButton;
    private MaterialButton killButton;

    BottomSheetUtil(ConstraintLayout bottomSheet, RecyclerView mainView, MastodonSidekiqApi sidekiqApi) {
        this.bottomSheet = bottomSheet;
        this.mainView = mainView;
        this.sidekiqApi = sidekiqApi;

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        quietButton = bottomSheet.findViewById(R.id.activity_processes_button_quiet);
        killButton = bottomSheet.findViewById(R.id.activity_processes_button_kill);
        View processView = bottomSheet.findViewById(R.id.activity_processes_process_item_selected);

        viewHolder = new ProcessViewHolder(processView);
    }

    public void open(ProcessModel data) {
        viewHolder.onBindItemViewHolder(data, null);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        setupOnClickListener(data.authenticityToken, data.identity);
        mainView.setPadding(0, 0, 0, bottomSheet.getHeight());
    }

    private void setupOnClickListener(String authenticityToken, String identity) {
        quietButton.setOnClickListener(v -> sidekiqApi.quietProcess(authenticityToken, identity, true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
        killButton.setOnClickListener(v -> sidekiqApi.killProcess(authenticityToken, identity, true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    void close() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mainView.setPadding(0, 0, 0, 0);
    }

    boolean isOpening() {
        return bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED;
    }

}
