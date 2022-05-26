/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package com.example.litapp;

import org.fbreader.book.Book;
import org.fbreader.book.Bookmark;
import org.fbreader.book.HighlightingStyle;
import org.fbreader.book.SerializerUtil;
import org.fbreader.config.ConfigInterface;
import org.fbreader.config.StringOption;
import org.fbreader.text.FixedPosition;
import org.fbreader.text.Position;
import org.fbreader.text.widget.TextBookController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

public class TextBookControllerImpl extends TextBookController {
    private final TextWidgetExt widget;

    public TextBookControllerImpl(TextWidgetExt widget, Book book) {
        super(widget.getContext(), book);
        this.widget = widget;
    }

    /* +++++++++++ CURRENT POSITION ++++++++++ */
    private StringOption positionOption() {
        return ConfigInterface.instance(this.applicationContext).stringOption(
                "book-position", String.valueOf(book.getId()), ""
        );
    }

    @Override
    public Position position() {
        try {
            final String[] split = this.positionOption().getValue().split(";");
            return new FixedPosition(
                    Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2])
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void storePosition(Position p) {
        if (p != null) {
            this.positionOption().setValue(
                    p.getParagraphIndex() + ";" + p.getElementIndex() + ";" + p.getCharIndex()
            );
        }
    }
    /* ----------- CURRENT POSITION ---------- */

    /* +++++++++++ BOOKMARKS ++++++++++ */
    private final HighlightingStyle style = new HighlightingStyle(
            1, // id
            0, // creation timestamp
            "", // name
            0xFF0000, // bg color: red
            0x0000FF // fg color: blue
    );
    private volatile long firstUnusedId = 0;
    private final TreeMap<Long, String> bookmarks = new TreeMap<Long, String>();

    @Override
    public int defaultHighlightingStyleId() {
        return 1;
    }

    @Override
    public List<HighlightingStyle> highlightingStyles() {
        return Collections.singletonList(this.style);
    }

    @Override
    public void saveBookmark(Bookmark bookmark) {
        if (bookmark.getId() == -1) {
            bookmark.setId(this.firstUnusedId);
            this.firstUnusedId += 1;
        }
        this.bookmarks.put(bookmark.getId(), SerializerUtil.serialize(bookmark));
        if (bookmark.isVisible) {
            this.widget.reloadBookmarks();
        }
    }

    @Override
    public void deleteBookmark(Bookmark bookmark) {
        this.bookmarks.remove(bookmark.getId());
        if (bookmark.isVisible) {
            this.widget.reloadBookmarks();
        }
    }

    @Override
    public void iterateVisibleBookmarks(BookmarksConsumer consumer) {
        final ArrayList<Bookmark> collected = new ArrayList<Bookmark>();
        for (String description : this.bookmarks.values()) {
            final Bookmark b = SerializerUtil.deserializeBookmark(description);
            if (b.isVisible) {
                collected.add(b);
            }
            if (collected.size() == 10) {
                consumer.accept(collected);
                collected.clear();
            }
        }
        if (collected.size() > 0) {
            consumer.accept(collected);
        }
    }

    @Override
    public List<Bookmark> hiddenBookmarks() {
        final ArrayList<Bookmark> collected = new ArrayList<Bookmark>();
        for (String description : this.bookmarks.values()) {
            final Bookmark b = SerializerUtil.deserializeBookmark(description);
            if (!b.isVisible) {
                collected.add(b);
            }
        }
        return collected;
    }
    /* ----------- BOOKMARKS ---------- */

    /* +++++++++++ VISITED HYPERLINKS ++++++++++ */
    private final HashSet<String> visitedHyperlinks = new HashSet<String>();

    public void markHyperlinkAsVisited(String id) {
        this.visitedHyperlinks.add(id);
    }

    public boolean isHyperlinkVisited(String id) {
        return this.visitedHyperlinks.contains(id);
    }
    /* ----------- VISITED HYPERLINKS ---------- */
}
