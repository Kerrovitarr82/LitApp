/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package org.fbreader.text.extras.gesture;

import android.graphics.Point;
import android.view.HapticFeedbackConstants;
import android.view.ViewConfiguration;

import org.fbreader.extras.gesture.GestureListenerBase;
import org.fbreader.text.entity.*;
import org.fbreader.text.view.Highlighting;
import org.fbreader.text.view.Region;
import org.fbreader.text.widget.*;

public class GestureListenerImpl extends GestureListenerBase {
	private enum SlideMode {
		none,
		brightnessAdjustment,
		pageTurning
	}
	private SlideMode slideMode = SlideMode.none;
	private Point startPoint = new Point();
	private int startBrightnessLevel;

	protected GestureListenerImpl(TextWidget widget) {
		super(widget);
	}

	@Override
	public TextWidget widget() {
		return (TextWidget)super.widget();
	}

	protected boolean isFlickScrollingEnabled() {
		return true;
	}

	protected boolean isBrightnessAdjustmentEnabled() {
		return true;
	}

	/* +++++++ TOUCH EVENTS +++++++ */
	@Override
	protected boolean onFingerSingleTap(Point pt) {
		for (EntityOpener opener : this.widget().openersFor(HyperlinkEntity.class, VideoEntity.class)) {
			if (opener.openAt(pt)) {
				return true;
			}
		}

		final Highlighting hilite = this.widget().findHighlighting(pt);
		if (hilite instanceof BookmarkHighlighting) {
			if (this.widget().openBookmark(((BookmarkHighlighting)hilite).bookmark)) {
				return true;
			}
		}

		if (this.widget().hidePopup()) {
			return true;
		}

		if (this.widget().releaseSelectionCursor()) {
			return true;
		} else if (this.widget().clearSelection()) {
			return true;
		}

		return false;
	}

	@Override
	protected boolean isDoubleTapSupported() {
		return false;
	}
	@Override
	protected boolean onFingerDoubleTap(Point pt) {
		return this.widget().hidePopup();
	}

	@Override
	protected boolean onFingerPress(Point pt) {
		this.slideMode = SlideMode.none;

		if (this.widget().captureSelectionCursorAt(pt)) {
			return true;
		}

		this.startPoint = pt;
		return true;
	}

	@Override
	protected boolean onFingerMove(Point pt) {
		if (this.widget().moveSelectionCursorTo(pt)) {
			return true;
		}

		synchronized (this.widget()) {
			switch (this.slideMode) {
				case none:
				{
					final float maxDist = ViewConfiguration.get(this.widget().getContext()).getScaledTouchSlop();
					final float xDiff = Math.abs(pt.x - this.startPoint.x);
					final float yDiff = Math.abs(pt.y - this.startPoint.y);
					if (yDiff >= maxDist && xDiff <= maxDist / 1.5f && pt.x < this.widget().getWidth() / 10 && this.isBrightnessAdjustmentEnabled()) {
						this.startBrightnessLevel = this.widget().getScreenBrightness();
						this.slideMode = SlideMode.brightnessAdjustment;
					} else if ((xDiff >= maxDist || yDiff >= maxDist) && this.isFlickScrollingEnabled()) {
						this.widget().startManualScrolling(pt);
						this.slideMode = SlideMode.pageTurning;
					}
					break;
				}
				case brightnessAdjustment:
				{
					final int delta = (this.startBrightnessLevel + 30) * (this.startPoint.y - pt.y) / this.widget().getHeight();
					this.widget().setScreenBrightness(this.startBrightnessLevel + delta, true);
					break;
				}
				case pageTurning:
					if (this.isFlickScrollingEnabled()) {
						this.widget().scrollManuallyTo(pt);
					}
					break;
			}
		}
		return true;
	}

	@Override
	protected boolean onFingerRelease(Point pt) {
		if (!this.widget().releaseSelectionCursor()) {
			switch (this.slideMode) {
				case none:
					break;
				case brightnessAdjustment:
					break;
				case pageTurning:
					if (this.isFlickScrollingEnabled()) {
						this.widget().startAnimatedScrolling(pt);
					}
			}
		}
		this.slideMode = SlideMode.none;
		return true;
	}

	@Override
	protected boolean onFingerLongPress(Point pt) {
		final Region region = this.widget().findRegion(pt);
		if (region != null) {
			if (region.entity instanceof WordEntity) {
				this.widget().performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
				this.widget().hidePopup();
				this.widget().initSelectionAt(pt);
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean onFingerMoveAfterLongPress(Point pt) {
		return this.widget().moveSelectionCursorTo(pt);
	}

	@Override
	protected boolean onFingerReleaseAfterLongPress(Point pt) {
		return this.widget().releaseSelectionCursor();
	}

	@Override
	protected boolean onFingerEventCancelled() {
		return this.widget().releaseSelectionCursor();
	}
	/* ------- TOUCH EVENTS ------- */
}
