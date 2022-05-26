/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package org.fbreader.extras.info;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

public class InfoView extends AppCompatTextView {
	private final Timer timer = new Timer();
	private volatile TimerTask cancelTask;

	public InfoView(Context context) {
		super(context);
	}

	public InfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InfoView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	final void showInfo(String text, Integer colorLevel) {
		synchronized (this.timer) {
			if (this.cancelTask != null) {
				this.cancelTask.cancel();
			}
			this.cancelTask = new TimerTask() {
				public void run() {
					synchronized (InfoView.this.timer) {
						post(() -> InfoView.this.setVisibility(GONE));
					}
				}
			};
			this.timer.schedule(this.cancelTask, 1000);
		}
		final int color = colorLevel == null ? 0x80 : 0x80 * colorLevel / 0xFF;
		post(() -> {
			this.setVisibility(VISIBLE);
			this.setTextColor(Color.argb(0xCC, color, color, color));
			this.setText(text);
		});
	}
}
