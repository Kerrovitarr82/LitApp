/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package org.fbreader.text.extras.opener;

import android.content.Intent;

import org.fbreader.text.HyperlinkType;
import org.fbreader.text.entity.Entity;
import org.fbreader.text.entity.HyperlinkEntity;
import org.fbreader.text.widget.EntityOpener;
import org.fbreader.text.widget.TextBookController;
import org.fbreader.text.widget.TextWidget;

public class InternalHyperlinkOpener extends EntityOpener {
	public InternalHyperlinkOpener(TextWidget widget) {
		super(widget, HyperlinkEntity.class);
	}

	@Override
	protected boolean accepts(Entity entity) {
		if (super.accepts(entity)) {
			switch (((HyperlinkEntity)entity).hyperlink.type) {
				case HyperlinkType.INTERNAL:
				case HyperlinkType.FOOTNOTE:
					return true;
				default:
					return false;
			}
		} else {
			return false;
		}
	}

	@Override
	protected void open(Entity entity, Entity.Location location) {
		final String id = ((HyperlinkEntity)entity).hyperlink.id;
		final TextBookController controller = widget.controller();
		if (controller != null) {
			controller.markHyperlinkAsVisited(id);
		}
		this.widget.jumpToFootnote(id);
	}
}
