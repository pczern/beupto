package me.speeddeveloper.beupto.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by phili on 8/14/2016.
 */
public class NoInterceptNestedScollView extends NestedScrollView {
    public NoInterceptNestedScollView(Context context) {
        super(context);
    }

    public NoInterceptNestedScollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoInterceptNestedScollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);


        if (action == MotionEvent.ACTION_CANCEL) {
            // Release the scroll.
            return false; // Do not intercept touch event, let the child handle it
        }

        if(action == MotionEvent.ACTION_SCROLL || action == MotionEvent.ACTION_UP){
            return true;
        }

        return false;
    }
}
