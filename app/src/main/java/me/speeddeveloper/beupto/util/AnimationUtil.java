package me.speeddeveloper.beupto.util;

import android.support.design.widget.FloatingActionButton;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import me.speeddeveloper.beupto.R;

/**
 * Created by phili on 8/19/2016.
 */
public class AnimationUtil {

    public static void startSubsribeAnimation(final FloatingActionButton fab){
        Animation rotateForward = AnimationUtils.loadAnimation(fab.getContext(), R.anim.rotate_forward);
        rotateForward.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setImageResource(R.drawable.ic_check_white_24dp);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fab.startAnimation(rotateForward);
        Toast.makeText(fab.getContext(), "Followed", Toast.LENGTH_SHORT).show();
    }
    public static void startUnsubsribeAnimation(final FloatingActionButton fab){
        Animation rotateForward = AnimationUtils.loadAnimation(fab.getContext(), R.anim.rotate_backward);
        rotateForward.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setImageResource(R.drawable.ic_add_white_24dp);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fab.startAnimation(rotateForward);
        Toast.makeText(fab.getContext(), "Unfollowed", Toast.LENGTH_SHORT).show();
    }
}
