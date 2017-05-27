package me.speeddeveloper.beupto.introduction;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.speeddeveloper.beupto.R;

/**
 * Created by phili on 7/17/2016.
 */
public class IntroductionPagerAdapter extends PagerAdapter {


    List<IntroductionScreen> introductionScreenList;

    public IntroductionPagerAdapter(List<IntroductionScreen> introductionScreenList) {
        this.introductionScreenList = introductionScreenList;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {


        LayoutInflater layoutInflater = LayoutInflater.from(container.getContext());
        View view = null;

        IntroductionScreen introductionScreen = introductionScreenList.get(position);
        view = layoutInflater.inflate(introductionScreen.getLayoutId(), container, false);


        container.addView(view);


        introductionScreen.setup(view, position);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return introductionScreenList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
