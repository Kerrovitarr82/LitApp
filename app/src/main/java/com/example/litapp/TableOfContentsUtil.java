/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package com.example.litapp;

import android.content.Intent;

import org.fbreader.book.Book;
import org.fbreader.text.widget.TextWidget;
import org.fbreader.toc.TableOfContents;

public class TableOfContentsUtil {
    public enum Key {
        book_title,
        file_path,
        reference,
        page_map
    }

    public static boolean isAvailable(TextWidget widget) {
        return widget.book() != null && widget.tableOfContents() != null;
    }

    public static Intent intent(TextWidget widget) {
        final Book book = widget.book();
        final TableOfContents toc = widget.tableOfContents();
        if (book == null || toc == null) {
            return null;
        }

        final Intent intent = new Intent(widget.getContext(), TableOfContentsActivity.class);
        intent.putExtra(String.valueOf(Key.book_title), book.getTitle());
        intent.putExtra(String.valueOf(Key.reference), widget.currentTOCReference());
        intent.putExtra(String.valueOf(Key.file_path), toc.path);
        intent.putExtra(String.valueOf(Key.page_map), widget.pageMap(toc));
        return intent;
    }
}
