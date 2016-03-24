//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lmy.lycommon.widget;

import android.os.Build.VERSION;
import com.lmy.lycommon.widget.ValueAnimatorCompat.Creator;
import com.lmy.lycommon.widget.ValueAnimatorCompat.Impl;
import android.view.View;

class ViewUtils {
    static final Creator DEFAULT_ANIMATOR_CREATOR = new Creator() {
        public ValueAnimatorCompat createAnimator() {
            return new ValueAnimatorCompat((Impl)(VERSION.SDK_INT >= 12?new ValueAnimatorCompatImplHoneycombMr1():new ValueAnimatorCompatImplEclairMr1()));
        }
    };
    private static final ViewUtilsImpl IMPL;

    ViewUtils() {
    }

    static void setBoundsViewOutlineProvider(View view) {
        IMPL.setBoundsViewOutlineProvider(view);
    }

    static ValueAnimatorCompat createAnimator() {
        return DEFAULT_ANIMATOR_CREATOR.createAnimator();
    }

    static {
        int version = VERSION.SDK_INT;
        if(version >= 21) {
            //NOTE EDIT
            IMPL = new ViewUtilsImplLollipop();
        } else {
            //NOTE EDIT
            IMPL = new ViewUtilsImplBase();
        }

    }

    private static class ViewUtilsImplLollipop implements ViewUtilsImpl {
        private ViewUtilsImplLollipop() {
        }

        public void setBoundsViewOutlineProvider(View view) {
            ViewUtilsLollipop.setBoundsViewOutlineProvider(view);
        }
    }

    private static class ViewUtilsImplBase implements ViewUtilsImpl {
        private ViewUtilsImplBase() {
        }

        public void setBoundsViewOutlineProvider(View view) {
        }
    }

    private interface ViewUtilsImpl {
        void setBoundsViewOutlineProvider(View var1);
    }
}
