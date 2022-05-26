/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package org.fbreader.extras.info;

import android.view.View;
import android.view.ViewParent;
import android.widget.RelativeLayout;

import org.fbreader.extras.R;
import org.fbreader.util.ViewUtil;

public abstract class InfoUtil {
	public static void showInfo(View widget, String text, Integer colorLevel) {
		final ViewParent parent = widget.getParent();
		if (!(parent instanceof RelativeLayout)) {
			return;
		}
		final RelativeLayout root = (RelativeLayout)parent;
		if (text != null) {
			final InfoView view = ViewUtil.findView(root, R.id.info_view, () -> {
				root.inflate(root.getContext(), R.layout.fbreader_info_view, root);
				return ViewUtil.findView(root, R.id.info_view);
			});
			if (view != null) {
				view.showInfo(text, colorLevel);
			}
		} else {
			final InfoView view = ViewUtil.findCachedView(root, R.id.info_view);
			if (view != null && view.getVisibility() == View.VISIBLE) {
				view.post(() -> view.setVisibility(View.GONE));
			}
		}
	}
}
