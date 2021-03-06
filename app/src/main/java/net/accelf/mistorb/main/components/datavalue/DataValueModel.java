package net.accelf.mistorb.main.components.datavalue;

import android.view.View;

import net.accelf.mistorb.main.listlayout.LayoutSize;

public class DataValueModel {

    String title;
    String value;
    View.OnClickListener onClickListener;
    LayoutSize layoutSize;

    public DataValueModel(String title, String value, View.OnClickListener onClickListener, LayoutSize layoutSize) {
        this.title = title;
        this.value = value;
        this.onClickListener = onClickListener;
        this.layoutSize = layoutSize;
    }

}
