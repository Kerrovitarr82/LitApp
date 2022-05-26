/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package org.fbreader.extras.util;

import org.fbreader.util.ProgressTask;
import org.fbreader.widget.BaseWidget;

public abstract class WidgetProgressTask<W extends BaseWidget,Params,Progress,Result> extends ProgressTask<Params,Progress,Result> {
	protected final W widget;

	public WidgetProgressTask(W widget) {
		this.widget = widget;
	}

	@Override
	protected final void showProgressIndicator() {
		if (this.widget != null) {
			this.widget.showProgressIndicator();
		}
	}
	@Override
	protected final void hideProgressIndicator() {
		if (this.widget != null) {
			this.widget.hideProgressIndicator();
		}
	}
}
