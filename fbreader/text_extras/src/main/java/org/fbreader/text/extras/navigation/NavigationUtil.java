/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package org.fbreader.text.extras.navigation;

import android.view.View;
import android.view.ViewParent;
import android.widget.*;
import androidx.annotation.UiThread;

import org.fbreader.extras.util.ViewAnimationUtil;
import org.fbreader.text.Position;
import org.fbreader.text.extras.R;
import org.fbreader.text.widget.TextWidget;
import org.fbreader.toc.TOCTree;
import org.fbreader.util.ViewUtil;
import org.fbreader.view.PageInText;

public abstract class NavigationUtil {
	@UiThread
	public static boolean startNavigation(TextWidget widget) {
		final View panel = findOrCreatePanel(widget);
		if (panel == null) {
			return false;
		}

		panel.setTag(widget.currentPosition(TextWidget.ContentType.active));
		ViewAnimationUtil.showWithAlpha(panel);
		updatePanelInternal(panel, widget);
		return true;
	}

	@UiThread
	public static void updatePanel(TextWidget widget) {
		final View panel = findPanel(widget);
		if (panel != null && panel.getVisibility() == View.VISIBLE) {
			updatePanelInternal(panel, widget);
		}
	}

	@UiThread
	public static void stopNavigation(TextWidget widget) {
		final View panel = findPanel(widget);
		if (panel != null && panel.getVisibility() == View.VISIBLE) {
			widget.jumpFrom((Position)panel.getTag());
			ViewAnimationUtil.hideWithAlpha(panel);
		}
	}

	public static boolean isNavigationActive(TextWidget widget) {
		final View panel = findPanel(widget);
		return panel != null && panel.getVisibility() == View.VISIBLE;
	}

	private static View findPanel(TextWidget widget) {
		if (widget == null) {
			return null;
		}

		final ViewParent parent = widget.getParent();
		if (parent instanceof View) {
			return ViewUtil.findCachedView((View)parent, R.id.navigation_panel);
		} else {
			return null;
		}
	}

	private static View findOrCreatePanel(TextWidget widget) {
		if (widget == null) {
			return null;
		}

		final ViewParent parent = widget.getParent();
		if (!(parent instanceof RelativeLayout)) {
			return null;
		}

		final RelativeLayout root = (RelativeLayout)parent;
		return ViewUtil.findView(root, R.id.navigation_panel, () -> {
			root.inflate(root.getContext(), R.layout.fbreader_navigation_panel, root);
			final View panel = ViewUtil.findView(root, R.id.navigation_panel);

			final SeekBar slider = ViewUtil.findView(panel, R.id.navigation_slider);
			final TextView text = ViewUtil.findView(panel, R.id.navigation_text);
			slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				public void onProgressChanged(SeekBar slider, int progress, boolean fromUser) {
					if (!fromUser) {
						return;
					}
					widget.gotoPage(progress + 1);
					text.setText(makeProgressText(widget, progress + 1, slider.getMax() + 1));
				}

				public void onStartTrackingTouch(SeekBar slider) {
				}

				public void onStopTrackingTouch(SeekBar slider) {
				}
			});
			final View resetButton = ViewUtil.findView(panel, R.id.navigation_reset_button);
			resetButton.setOnClickListener((v) -> {
				widget.gotoPosition((Position)panel.getTag());
				updatePanelInternal(panel, widget);
			});

			return panel;
		});
	}

	private static void updatePanelInternal(View panel, TextWidget widget) {
		final SeekBar slider = ViewUtil.findView(panel, R.id.navigation_slider);
		final TextView text = ViewUtil.findView(panel, R.id.navigation_text);

		final PageInText pagePosition = widget.pageInText();

		if (slider.getMax() != pagePosition.total - 1 || slider.getProgress() != pagePosition.pageNo - 1) {
			slider.setMax(pagePosition.total - 1);
			slider.setProgress(pagePosition.pageNo - 1);
			text.setText(makeProgressText(widget, pagePosition.pageNo, pagePosition.total));
		}
		final View resetButton = ViewUtil.findView(panel, R.id.navigation_reset_button);
		final Position start = (Position)panel.getTag();
		resetButton.setEnabled(
			start != null && !start.equals(widget.currentPosition(TextWidget.ContentType.active))
		);
	}

	private static String makeProgressText(TextWidget widget, int page, int pagesNumber) {
		final StringBuilder builder = new StringBuilder();
		builder.append(page);
		builder.append("/");
		builder.append(pagesNumber);
		if (widget.isMainContentSelected()) {
			final TOCTree tocElement = widget.currentTOCElement();
			if (tocElement != null && tocElement.Text != null) {
				builder.append("  ");
				builder.append(tocElement.Text);
			}
		}
		return builder.toString();
	}
}
