package me.speeddeveloper.beupto.introduction;

import android.view.View;

/**
 * Created by phili on 7/17/2016.
 */
public abstract class IntroductionScreen {

    int layoutId;

    public IntroductionScreen(int layoutId){
        this.layoutId = layoutId;
    }

    // setup gets called on the current root view when it's inflated
    public abstract void setup(View view, int position);


    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }
}
