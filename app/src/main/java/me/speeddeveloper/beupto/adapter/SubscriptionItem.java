package me.speeddeveloper.beupto.adapter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import me.speeddeveloper.beupto.R;
import me.speeddeveloper.beupto.model.InfoModel;
import me.speeddeveloper.beupto.model.InfoType;
import me.speeddeveloper.beupto.util.CircleTransform;

/**
 * Created by phili on 8/21/2016.
 */
public class SubscriptionItem extends AbstractItem<SubscriptionItem, SubscriptionItem.ViewHolder> {

    private InfoModel infoModel;
    private static final String TAG = SubscriptionItem.class.getSimpleName();

    public SubscriptionItem(InfoModel info){
        this.infoModel = info;
    }

    @Override
    public int getType() {
        return R.id.item_subscription;
    }



    public InfoModel getInfoModel(){
        return infoModel;
    }


    @Override
    public void bindView(ViewHolder holder) {
        //call super so the selection is already handled for you
        super.bindView(holder);

        String title = infoModel.getTitle();

        Log.d(TAG, " a title : "  +  title);
        String description = infoModel.getDescription();


        if (holder.title != null && title != null && title.trim().isEmpty() == false) {
            holder.title.setText(title);
        }

        if (holder.description != null && description != null && description.trim().isEmpty() == false) {
            holder.description.setText(description);
        }


        if (holder.imageView != null && infoModel.getThumbnailUri() != null) {
            RequestCreator requestCreator = Picasso.with(holder.itemView.getContext()).load(infoModel.getThumbnailUri()).placeholder(R.drawable.progress_animation).transform(new CircleTransform());

            if (infoModel.getInfoType() == InfoType.FEED.toString() || infoModel.getInfoType() == InfoType.WEBSITE.toString()) {
                Drawable errorDrawable = MaterialDrawableBuilder.with(holder.itemView.getContext()) // provide a context
                        .setIcon(MaterialDrawableBuilder.IconValue.WEB) // provide an icon
                        .setColor(holder.itemView.getContext().getResources().getColor(R.color.error_icon)) // set the icon color
                        .setToActionbarSize()
                        // set the icon size
                        .build(); // Finally call build
                requestCreator = requestCreator.error(errorDrawable);
            } else {
                Log.d(TAG, "no plan");
            }

            requestCreator.into(holder.imageView);

        } else if (holder.imageView != null && infoModel.getThumbnailUri() == null) {
            if (infoModel.getInfoType() == InfoType.FEED.toString() || infoModel.getInfoType() == InfoType.WEBSITE.toString()) {
                Drawable errorDrawable = MaterialDrawableBuilder.with(holder.itemView.getContext()) // provide a context
                        .setIcon(MaterialDrawableBuilder.IconValue.WEB) // provide an icon
                        .setColor(holder.itemView.getContext().getResources().getColor(R.color.error_icon)) // set the icon color
                        .setToActionbarSize()
                        // set the icon size
                        .build(); // Finally call build
                holder.imageView.setImageDrawable(errorDrawable);
            } else {
                Log.d(TAG, "no plan");
            }
        }


        /*if (holder.borderline != null && (infoList.size() - 1) == position) {
            holder.borderline.setVisibility(View.GONE);
        } else if (holder.borderline != null) {
            holder.borderline.setVisibility(View.VISIBLE);
        }
*/










    }


    @Override
    public int getLayoutRes() {
        return R.layout.adapter_subscription;
    }

    protected static  class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView description;
        ImageView imageView;
        View borderline;

        ViewHolder(View itemView) {
            super(itemView);

            itemView.setTag(this);
            title = (TextView) itemView.findViewById(R.id.title_textview);
            description = (TextView) itemView.findViewById(R.id.description_textview);
            borderline = itemView.findViewById(R.id.borderline);
            imageView = (ImageView) itemView.findViewById(R.id.imageview);
        }
    }
}
