/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package org.fbreader.text.extras.search;

import android.view.View;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import org.fbreader.extras.util.WidgetProgressTask;
import org.fbreader.text.Position;
import org.fbreader.text.extras.R;
import org.fbreader.text.widget.TextWidget;
import org.fbreader.util.ViewUtil;

public abstract class TextSearchUtil {
	public static class SearchTask extends WidgetProgressTask<TextWidget,Void,Void,Boolean> {
		private final String pattern;

		public SearchTask(TextWidget widget, String pattern) {
			super(widget);
			this.pattern = pattern;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			final View panel = findOrCreatePanel(this.widget);
			if (panel != null) {
				panel.setTag(this.widget.isMainContentSelected() ? this.widget.currentPosition(TextWidget.ContentType.main) : null);
				final TextSearchPanelListener listener = new TextSearchPanelListener(this.widget);
				for (int id : new int[] { org.fbreader.extras.R.id.search_panel_previous, org.fbreader.extras.R.id.search_panel_close, org.fbreader.extras.R.id.search_panel_next }) {
					ViewUtil.findView(panel, id).setOnClickListener(listener);
				}
			}
		}

		@Override
		protected Boolean doInBackground(Void ... params) {
			return this.widget.search(this.pattern) > 0;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				this.widget.findClosest();
				final View panel = findPanel(this.widget);
				if (panel != null) {
					panel.setVisibility(View.VISIBLE);
					enableButtons(this.widget, panel);
				}
			} else {
				this.widget.showPopup(org.fbreader.extras.R.string.text_search_not_found_message);
			}
			super.onPostExecute(result);
		}
	}

	@UiThread
	public static void performSearch(final @NonNull TextWidget widget, final @NonNull String pattern) {
		new SearchTask(widget, pattern).execute();
	}

	public static void hidePanel(@NonNull TextWidget widget) {
		final View panel = findPanel(widget);
		if (panel != null && panel.getVisibility() == View.VISIBLE) {
			widget.jumpFrom((Position)panel.getTag());
			widget.post(() -> panel.setVisibility(View.GONE));
		}
	}

	private static void enableButtons(TextWidget widget, View panel) {
		ViewUtil.findView(panel, org.fbreader.extras.R.id.search_panel_previous).setEnabled(
			widget.canFindPrevious()
		);
		ViewUtil.findView(panel, org.fbreader.extras.R.id.search_panel_next).setEnabled(
			widget.canFindNext()
		);
	}

	public static void updatePanel(@NonNull TextWidget widget) {
		final View panel = findPanel(widget);
		if (panel != null && panel.getVisibility() == View.VISIBLE) {
			enableButtons(widget, panel);
		}
	}

	private static View findPanel(View widget) {
		final ViewParent parent = widget.getParent();
		if (parent instanceof View) {
			return ViewUtil.findCachedView((View)parent, org.fbreader.extras.R.id.search_panel);
		} else {
			return null;
		}
	}

	private static View findOrCreatePanel(View widget) {
		final ViewParent parent = widget.getParent();
		if (!(parent instanceof RelativeLayout)) {
			return null;
		}

		final RelativeLayout root = (RelativeLayout)parent;
		return ViewUtil.findView(root, org.fbreader.extras.R.id.search_panel, () -> {
			root.inflate(root.getContext(), org.fbreader.extras.R.layout.fbreader_search_panel, root);
			return ViewUtil.findView(root, org.fbreader.extras.R.id.search_panel);
		});
	}
}
