package common;

import android.view.View;

public class ViewCache {
    private View baseView;

    public ViewCache() {
    }

    public ViewCache(View baseView) {
        this.baseView = baseView;
    }

    public View getBaseView() {
        return baseView;
    }

    public void setBaseView(View baseView) {
        this.baseView = baseView;
    }

}