package me.speeddeveloper.beupto.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import me.speeddeveloper.beupto.R;
import me.speeddeveloper.beupto.model.Info;

/**
 * Created by phili on 8/3/2016.
 */
public class ImageSlider extends PagerAdapter {


    private ImageView[] dots;
    private LinearLayout dotLinearLayout;
    private List<Info> infoList;
    private int position = 0;
    private Context context;
    private View.OnClickListener itemClickListener;
    public ImageSlider(List<Info>  infoList, View.OnClickListener clickListener, LinearLayout dotLinearLayout){
        this.infoList = infoList;
        this.dotLinearLayout = dotLinearLayout;
        this.context = dotLinearLayout.getContext();
        this.itemClickListener = clickListener;
        setupSelectionPoints();

    }

    int unselectedSize = 15;
    int selectedSize = 25;

    public void next(){
        position += 1;
        setSelectionPoint(position);
    }
    public void previous(){
        position -= 1;
        setSelectionPoint(position);
    }

    public void jump(int position){
        setSelectionPoint(position);
    }


    private void setupSelectionPoints() {
        int dotsCount = infoList.size();
        dots = new ImageView[dotsCount];
        dots[0] = new ImageView(context);
        dots[0].setImageResource(R.drawable.vendor_circle_selected);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(selectedSize, selectedSize);
        params.setMargins(20, 0, 20, 0);
        params.gravity = Gravity.CENTER;
        dotLinearLayout.addView(dots[0], params);
        for (int i = 1; i < dots.length; i++) {

            dots[i] = new ImageView(context);
            dots[i].setImageResource(R.drawable.vendor_circle_nonselected);
            params = new LinearLayout.LayoutParams(unselectedSize, unselectedSize);
            params.setMargins(20, 0, 20, 0);
            params.gravity = Gravity.CENTER;
            dotLinearLayout.addView(dots[i], params);
        }



    }

    private void setSelectionPoint(int position) {




        for (int i = 0; i < dots.length; i++) {
            dots[i].setImageResource(R.drawable.vendor_circle_nonselected);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(unselectedSize, unselectedSize);
            params.setMargins(20, 0, 20, 0);
            params.gravity = Gravity.CENTER;
            dots[i].setLayoutParams(params);
        }
        dots[position].setImageResource(R.drawable.vendor_circle_selected);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(selectedSize, selectedSize);
        params.setMargins(20, 0, 20, 0);
        params.gravity = Gravity.CENTER;
        dots[position].setLayoutParams(params);
    }


    @Override
    public int getCount() {
        return infoList.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = LayoutInflater.from(container.getContext()).inflate(R.layout.adapter_vendor, null);


        Info info = infoList.get(position);

        ((TextView) v.findViewById(R.id.headline_textview)).setText(info.getTitle());
        //((TextView) v.findViewById(R.id.description_textview)).setText(info.getDescription().substring(0, 50));


        Picasso.with(container.getContext()).load(info.getThumbnailUri()).into(((ImageView) v.findViewById(R.id.entry_imageview)));

        container.addView(v);
        v.setOnClickListener(itemClickListener);
        return v;
    }
}
