/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package org.fbreader.extras.gesture;

import android.graphics.Point;
import android.view.*;

import org.fbreader.widget.BaseWidget;
import org.fbreader.widget.GestureListener;

public abstract class GestureListenerBase extends GestureListener {
	private final BaseWidget _widget;

	private volatile boolean touchDetected = false;
	private volatile boolean longPressConsumed = false;
	private volatile Point longPressPoint = null;
	private volatile boolean movedAfterLongPress = false;

	protected GestureListenerBase(BaseWidget widget) {
		this._widget = widget;
	}

	public BaseWidget widget() {
		return this._widget;
	}

	private void unsetTouchDetected() {
		this.touchDetected = false;
		this.widget().cancelTouchFeedback();
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		this.unsetTouchDetected();
		this.onFingerMove(point(e2));
		return true;
	}
	@Override
	public void onShowPress(MotionEvent event) {
		this.touchDetected = true;
		this.widget().showTouchFeedback(point(event));
	}
	@Override
	public boolean onDown(MotionEvent event) {
		this.onFingerPress(point(event));
		return true;
	}
	@Override
	public void onLongPress(MotionEvent event) {
		this.longPressPoint = point(event);
		this.longPressConsumed = this.onFingerLongPress(this.longPressPoint);
		this.movedAfterLongPress = false;
	}
	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		this.unsetTouchDetected();
		if (!this.isDoubleTapSupported()) {
			this.onFingerSingleTap(point(event));
		}
		return true;
	}
	@Override
	public boolean onSingleTapConfirmed(MotionEvent event) {
		if (this.isDoubleTapSupported()) {
			this.onFingerSingleTap(point(event));
		}
		return true;
	}
	@Override
	public boolean onDoubleTap(MotionEvent event) {
		if (this.isDoubleTapSupported()) {
			this.onFingerDoubleTap(point(event));
		}
		return true;
	}

	@Override
	protected void onMissingEvent(MotionEvent event) {
		final Point pt = point(event);
		switch (event.getAction()) {
			case MotionEvent.ACTION_CANCEL:
				this.longPressPoint = null;
				this.unsetTouchDetected();
				this.onFingerEventCancelled();
				break;
			case MotionEvent.ACTION_UP:
				if (this.longPressPoint != null) {
					this.longPressPoint = null;
					if (this.longPressConsumed) {
						this.onFingerReleaseAfterLongPress(pt);
					} else if (this.touchDetected) {
						this.onFingerSingleTap(pt);
					} else {
						this.onFingerRelease(pt);
					}
				} else {
					this.onFingerRelease(pt);
				}
				this.unsetTouchDetected();
				break;
			case MotionEvent.ACTION_MOVE:
			{
				final Point lpp = this.longPressPoint;
				if (lpp != null) {
					if (!this.movedAfterLongPress) {
						final int slop = ViewConfiguration.get(this.widget().getContext()).getScaledTouchSlop();
						if (Math.abs(pt.x - lpp.x) <= slop && Math.abs(pt.y - lpp.y) <= slop) {
							break;
						}
						this.movedAfterLongPress = true;
					}
					this.unsetTouchDetected();
					if (this.longPressConsumed) {
						this.onFingerMoveAfterLongPress(pt);
					} else {
						this.onFingerMove(pt);
					}
				}
				break;
			}
		}
	}

	/* +++++++ TOUCH EVENTS +++++++ */
	protected abstract boolean onFingerSingleTap(Point pt);
	protected abstract boolean isDoubleTapSupported();
	protected abstract boolean onFingerDoubleTap(Point pt);
	protected abstract boolean onFingerPress(Point pt);
	protected abstract boolean onFingerMove(Point pt);
	protected abstract boolean onFingerRelease(Point pt);
	protected abstract boolean onFingerLongPress(Point pt);
	protected abstract boolean onFingerMoveAfterLongPress(Point pt);
	protected abstract boolean onFingerReleaseAfterLongPress(Point pt);
	protected abstract boolean onFingerEventCancelled();
	/* ------- TOUCH EVENTS ------- */

	private static Point point(MotionEvent event) {
		return new Point(Math.round(event.getX()), Math.round(event.getY()));
	}
}
