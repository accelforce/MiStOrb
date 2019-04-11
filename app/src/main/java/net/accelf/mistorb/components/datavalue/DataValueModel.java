package net.accelf.mistorb.components.datavalue;

import net.accelf.mistorb.listlayout.LayoutSize;

public class DataValueModel {

    String title;
    String value;
    LayoutSize layoutSize;

    public DataValueModel(String title, String value, LayoutSize layoutSize) {
        this.title = title;
        this.value = value;
        this.layoutSize = layoutSize;
    }

}
