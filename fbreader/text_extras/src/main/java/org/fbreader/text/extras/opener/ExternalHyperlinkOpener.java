/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package org.fbreader.text.extras.opener;

import android.content.Intent;
import android.net.Uri;

import org.fbreader.text.HyperlinkType;
import org.fbreader.text.entity.Entity;
import org.fbreader.text.entity.HyperlinkEntity;
import org.fbreader.text.widget.EntityOpener;
import org.fbreader.text.widget.TextWidget;

public class ExternalHyperlinkOpener extends EntityOpener {
	public ExternalHyperlinkOpener(TextWidget widget) {
		super(widget, HyperlinkEntity.class);
	}

	@Override
	protected boolean accepts(Entity entity) {
		if (super.accepts(entity)) {
			return ((HyperlinkEntity)entity).hyperlink.type == HyperlinkType.EXTERNAL;
		} else {
			return false;
		}
	}

	@Override
	protected void open(Entity entity, Entity.Location location) {
		final String url = ((HyperlinkEntity)entity).hyperlink.id;
		try {
			this.widget.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
		} catch (Throwable t) {
		}
	}
}
