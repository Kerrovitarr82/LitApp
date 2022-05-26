/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package org.fbreader.extras.selection;

import android.graphics.*;

import org.fbreader.util.ColorUtil;
import org.fbreader.util.GlobalMetricsHelper;
import org.fbreader.widget.BaseWidget;

public abstract class SelectionCursorUtil {
	private static final Paint paint = new Paint();

	public static void drawCursor(BaseWidget widget, Canvas canvas, Point pt, boolean leftNotRight) {
		final long color = widget.colorProfile().selectionBackground.getValue();
		if (!ColorUtil.isValidArgb(color)) {
			return;
		}
		paint.setColor(Color.rgb(ColorUtil.red(color), ColorUtil.green(color), ColorUtil.blue(color)));
		final int dpi = GlobalMetricsHelper.instance(widget.getContext()).dpi();
		final int unit = dpi / 120;
		final int xCenter = leftNotRight ? pt.x - unit - 1 : pt.x + unit + 1;
		canvas.drawRect(xCenter - unit, pt.y - dpi / 8, xCenter + unit + 1, pt.y + dpi / 8 + 1, paint);
		if (leftNotRight) {
			canvas.drawCircle(xCenter, pt.y - dpi / 8, unit * 6, paint);
		} else {
			canvas.drawCircle(xCenter, pt.y + dpi / 8, unit * 6, paint);
		}
	}
}
