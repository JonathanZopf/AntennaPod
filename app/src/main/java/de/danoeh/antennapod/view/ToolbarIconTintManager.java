package de.danoeh.antennapod.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.ContextThemeWrapper;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import de.danoeh.antennapod.R;

public abstract class ToolbarIconTintManager implements AppBarLayout.OnOffsetChangedListener {
    private final Activity activity;
    private final CollapsingToolbarLayout collapsingToolbar;
    private final MaterialToolbar toolbar;
    private boolean isTinted = false;
    private boolean isWhiteIconsStatusBar = false;

    public ToolbarIconTintManager(Activity activity, MaterialToolbar toolbar, CollapsingToolbarLayout collapsingToolbar) {
        this.activity = activity;
        this.collapsingToolbar = collapsingToolbar;
        this.toolbar = toolbar;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        double ratio = (double) (collapsingToolbar.getHeight() + offset) / collapsingToolbar.getMinimumHeight();
        System.out.println("X: " + ratio +  " Offset: " + offset);

        boolean whiteIconsStatusBar = ratio > 2.0 || ratio < 1.5;
        boolean tint = ratio > 1.5;

        if (isWhiteIconsStatusBar != whiteIconsStatusBar) {
            isWhiteIconsStatusBar = whiteIconsStatusBar;
            WindowInsetsControllerCompat windowInsetController = WindowCompat.getInsetsController(activity.getWindow(), activity.getWindow().getDecorView());
            windowInsetController.setAppearanceLightStatusBars(whiteIconsStatusBar);
        }

        if (isTinted != tint) {
            isTinted = tint;
            updateTint();
        }
    }

    public void updateTint() {
        if (isTinted) {
            doTint(new ContextThemeWrapper(activity, R.style.Theme_AntennaPod_Dark));
            safeSetColorFilter(toolbar.getNavigationIcon(), new PorterDuffColorFilter(0xffffffff, Mode.SRC_ATOP));
            safeSetColorFilter(toolbar.getOverflowIcon(), new PorterDuffColorFilter(0xffffffff, Mode.SRC_ATOP));
            safeSetColorFilter(toolbar.getCollapseIcon(), new PorterDuffColorFilter(0xffffffff, Mode.SRC_ATOP));
        } else {
            doTint(activity);
            safeSetColorFilter(toolbar.getNavigationIcon(), null);
            safeSetColorFilter(toolbar.getOverflowIcon(), null);
            safeSetColorFilter(toolbar.getCollapseIcon(), null);
        }
    }

    private void safeSetColorFilter(Drawable icon, PorterDuffColorFilter filter) {
        if (icon != null) {
            icon.setColorFilter(filter);
        }
    }

    /**
     * View expansion was changed. Icons need to be tinted
     * @param themedContext ContextThemeWrapper with dark theme while expanded
     */
    protected abstract void doTint(Context themedContext);
}
