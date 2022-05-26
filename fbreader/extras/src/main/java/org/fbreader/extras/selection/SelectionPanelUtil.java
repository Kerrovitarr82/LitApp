/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package org.fbreader.extras.selection;

import java.util.List;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;

import org.fbreader.extras.R;
import org.fbreader.util.ViewUtil;

public abstract class SelectionPanelUtil {
	public static void showPanel(@NonNull View widget, List<Rect> selectionRects, View.OnClickListener listener) {
		widget.post(() -> {
			final View panel = findOrCreatePanel(widget);
			if (panel == null) {
				return;
			}

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT
			);
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

			final int verticalPosition;
			if (selectionRects.isEmpty()) {
				verticalPosition = RelativeLayout.CENTER_VERTICAL;
			} else {
				int top = Integer.MAX_VALUE;
				int bottom = Integer.MIN_VALUE;
				for (Rect r : selectionRects) {
					top = Math.min(top, r.top);
					bottom = Math.max(bottom, r.bottom);
				}
				final int spaceTop = top;
				final int spaceBottom = widget.getHeight() - bottom;
				if (spaceTop > spaceBottom) {
					verticalPosition = spaceTop > panel.getHeight() + 20
						? RelativeLayout.ALIGN_PARENT_TOP : RelativeLayout.CENTER_VERTICAL;
				} else {
					verticalPosition = spaceBottom > panel.getHeight() + 20
						? RelativeLayout.ALIGN_PARENT_BOTTOM : RelativeLayout.CENTER_VERTICAL;
				}
			}
			layoutParams.addRule(verticalPosition);
			panel.setLayoutParams(layoutParams);

			for (int id : new int[] { R.id.selection_panel_copy, R.id.selection_panel_share, R.id.selection_panel_translate, R.id.selection_panel_browse, R.id.selection_panel_bookmark }) {
				ViewUtil.findView(panel, id).setOnClickListener(listener);
			}

			panel.setVisibility(View.VISIBLE);
			panel.requestFocus();
		});
	}

	public static void hidePanel(@NonNull View widget) {
		final View panel = findPanel(widget);
		if (panel != null && panel.getVisibility() == View.VISIBLE) {
			panel.post(() -> panel.setVisibility(View.GONE));
		}
	}

	private static View findPanel(View widget) {
		final ViewParent parent = widget.getParent();
		if (parent instanceof View) {
			return ViewUtil.findCachedView((View)parent, R.id.selection_panel);
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
		return ViewUtil.findView(root, R.id.selection_panel, () -> {
			root.inflate(root.getContext(), R.layout.fbreader_selection_panel, root);
			return ViewUtil.findView(root, R.id.selection_panel);
		});
	}
}
