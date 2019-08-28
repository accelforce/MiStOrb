package net.accelf.mistorb.main.listlayout;

public class LayoutSize {

    private int id;

    private static final float[][] layoutSizes = {
            //{ padding, indent, textSize }
            { 8.0f,  8.0f, 24.0f },
            { 6.0f, 32.0f, 20.0f },
            { 8.0f, 56.0f, 18.0f }
    };

    public LayoutSize(int id) {
        this.id = id;
    }

    public int getPadding() {
        return (int) layoutSizes[id][0];
    }

    public int getIndent() {
        return (int) layoutSizes[id][1];
    }

    public float getTextSize() {
        return layoutSizes[id][2];
    }

}
