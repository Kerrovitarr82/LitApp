/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package org.fbreader.text.extras.controller;

import java.util.Collections;
import java.util.List;

import android.content.Context;

import org.fbreader.book.*;
import org.fbreader.text.Position;
import org.fbreader.text.widget.TextBookController;

/**
 * Empty implementation of {@link TextBookController}
 *
 * Use this class as the base if you don't want to implement all TextBookController methods.
 */
public class AbstractTextBookController extends TextBookController {
	public AbstractTextBookController(Context context, Book book) {
		super(context, book);
	}

	/* +++++++++++ BOOKMARKS ++++++++++ */
	@Override
	public int defaultHighlightingStyleId() {
		return 1;
	}
	@Override
	public List<HighlightingStyle> highlightingStyles() {
		return Collections.emptyList();
	}
	@Override
	public void saveBookmark(Bookmark bookmark) {
	}
	@Override
	public void deleteBookmark(Bookmark bookmark) {
	}
	@Override
	public void iterateVisibleBookmarks(BookmarksConsumer consumer) {
	}
	@Override
	public List<Bookmark> hiddenBookmarks() {
		return Collections.emptyList();
	}
	/* ----------- BOOKMARKS ---------- */

	/* +++++++++++ CURRENT POSITION ++++++++++ */
	@Override
	public Position position() {
		return null;
	}
	@Override
	public void storePosition(Position position) {
	}
	/* ----------- CURRENT POSITION ---------- */

	@Override
	public void markHyperlinkAsVisited(String id) {
	}
	@Override
	public boolean isHyperlinkVisited(String id) {
		return false;
	}
}
