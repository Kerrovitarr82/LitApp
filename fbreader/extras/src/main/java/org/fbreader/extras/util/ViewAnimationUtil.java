/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package org.fbreader.extras.util;

import android.animation.*;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.UiThread;

import org.fbreader.extras.R;

public abstract class ViewAnimationUtil {
	@UiThread
	public static void showWithAlpha(View view) {
		final Animator existing = (Animator)view.getTag(R.id.tag_view_animation);
		if (existing != null) {
			existing.end();
			view.setTag(R.id.tag_view_animation, null);
		}
		if (view.getVisibility() == View.VISIBLE) {
			return;
		}

		view.setVisibility(View.VISIBLE);
		view.setAlpha(0);
		final AnimatorSet animator = new AnimatorSet();
		animator.play(ObjectAnimator.ofFloat(view, "alpha", 1));
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animator) {
				view.setTag(R.id.tag_view_animation, null);
				view.requestLayout();
			}
		});
		view.setTag(R.id.tag_view_animation, animator);
		animator.start();
	}

	@UiThread
	public static void hideWithAlpha(View view) {
		final Animator existing = (Animator)view.getTag(R.id.tag_view_animation);
		if (existing != null) {
			existing.end();
			view.setTag(R.id.tag_view_animation, null);
		}
		if (view.getVisibility() == View.GONE) {
			return;
		}

		view.setAlpha(1);
		final AnimatorSet animator = new AnimatorSet();
		animator.play(ObjectAnimator.ofFloat(view, "alpha", 0));
		animator.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animator) {
				view.setTag(R.id.tag_view_animation, null);
				view.setVisibility(View.GONE);
			}
		});
		view.setTag(R.id.tag_view_animation, animator);
		animator.start();
	}
}
