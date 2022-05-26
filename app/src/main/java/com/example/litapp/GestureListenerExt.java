/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package com.example.litapp;

import android.graphics.Point;

import org.fbreader.text.extras.gesture.GestureListenerImpl;
import org.fbreader.text.extras.navigation.NavigationUtil;
import org.fbreader.util.PageIndex;
import org.fbreader.util.ViewUtil;

class GestureListenerExt extends GestureListenerImpl {
    GestureListenerExt(TextWidgetExt widget) {
        super(widget);
    }

    @Override
    protected boolean onFingerSingleTap(Point pt) {
        if (super.onFingerSingleTap(pt)) {
            return true;
        }

        if (pt.x <= this.widget().getWidth() / 3) {
            this.widget().startAnimatedScrolling(PageIndex.previous);
        } else if (pt.x >= 2 * this.widget().getWidth() / 3) {
            this.widget().startAnimatedScrolling(PageIndex.next);
        } else if (!this.widget().hasSearchResults()) {
            if (NavigationUtil.isNavigationActive(this.widget())) {
                FullScreenUtil.hideSystemUI(ViewUtil.findContainingActivity(this.widget()));
                NavigationUtil.stopNavigation(this.widget());
            } else {
                FullScreenUtil.showSystemUI(ViewUtil.findContainingActivity(this.widget()));
                this.widget().clearSearchResults();
                this.widget().clearSelection();
                NavigationUtil.startNavigation(this.widget());
            }
        }
        return true;
    }

    @Override
    public boolean isDoubleTapSupported() {
        return false;
    }
}
