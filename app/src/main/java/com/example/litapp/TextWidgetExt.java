/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package com.example.litapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.Toast;

import org.fbreader.book.Book;
import org.fbreader.extras.info.InfoUtil;
import org.fbreader.extras.selection.SelectionCursorUtil;
import org.fbreader.extras.selection.SelectionPanelUtil;
import org.fbreader.text.extras.navigation.NavigationUtil;
import org.fbreader.text.extras.opener.ExternalHyperlinkOpener;
import org.fbreader.text.extras.opener.InternalHyperlinkOpener;
import org.fbreader.text.extras.search.TextSearchUtil;
import org.fbreader.text.view.SelectionData;
import org.fbreader.text.widget.TextWidget;
import org.fbreader.util.ViewUtil;

public class TextWidgetExt extends TextWidget {
    {
        this.registerOpener(new InternalHyperlinkOpener(this));
        this.registerOpener(new ExternalHyperlinkOpener(this));
        this.registerOpener(new BookmarkOpener(this));
    }

    public TextWidgetExt(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TextWidgetExt(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextWidgetExt(Context context) {
        super(context);
    }

    @Override
    protected GestureListenerExt createGestureListener() {
        return new GestureListenerExt(this);
    }

    @Override
    protected TextBookControllerImpl createController(Book book) {
        return new TextBookControllerImpl(this, book);
    }

    @Override
    public void drawSelectionCursor(Canvas canvas, Point pt, boolean startNotEnd) {
        SelectionCursorUtil.drawCursor(this, canvas, pt, startNotEnd);
    }

    @Override
    protected void showSelectionPanel() {
        final SelectionData data = this.selectionData();
        if (data != null) {
            SelectionPanelUtil.showPanel(
                    this, data.rects, new SelectionPanelListener(this)
            );
        }
    }

    @Override
    protected void hideSelectionPanel() {
        SelectionPanelUtil.hidePanel(this);
    }

    @Override
    protected void showInfo(String text) {
        InfoUtil.showInfo(this, text, this.colorLevel);
    }

    @Override
    public void searchInText(String pattern) {
        FullScreenUtil.hideSystemUI(ViewUtil.findContainingActivity(this));
        this.post(() -> NavigationUtil.stopNavigation(this));
        TextSearchUtil.performSearch(this, pattern);
    }

    @Override
    protected void hideTextSearchPanel() {
        TextSearchUtil.hidePanel(this);
    }

    @Override
    public void showPopup(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onContentUpdated() {
        TextSearchUtil.updatePanel(this);
        NavigationUtil.updatePanel(this);
    }
}
