/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package org.fbreader.text.extras.search;

import android.view.View;

import org.fbreader.text.extras.R;
import org.fbreader.text.widget.TextWidget;

public final class TextSearchPanelListener implements View.OnClickListener {
	private final TextWidget widget;

	public TextSearchPanelListener(TextWidget widget) {
		this.widget = widget;
	}

	public void onClick(View view) {
		final int id = view.getId();
		if (id == org.fbreader.extras.R.id.search_panel_previous) {
			this.widget.findPrevious();
		} else if (id == org.fbreader.extras.R.id.search_panel_next) {
			this.widget.findNext();
		} else if (id == org.fbreader.extras.R.id.search_panel_close) {
			this.widget.clearSearchResults();
		}
	}
}
