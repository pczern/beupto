package me.speeddeveloper.beupto.adapter;

/**
 * Created by phili on 8/4/2016.
 */

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import me.speeddeveloper.beupto.R;
import me.speeddeveloper.beupto.model.Info;
import me.speeddeveloper.beupto.model.Website;
import me.speeddeveloper.beupto.util.CircleTransform;


/**
 * Created by speedDeveloper on 21.03.2016.
 */

/**
 * Created by speedDeveloper on 10.02.2016.
 */
public class VendorYoutubeRecyclerAdapter extends RecyclerView.Adapter<VendorYoutubeRecyclerAdapter.ViewHolder> {

    private final static String TAG = VendorYoutubeRecyclerAdapter.class.getSimpleName();
    private List<Info> infoList = new ArrayList<>();
    private Set<Info> infoHashSet = new LinkedHashSet<>();

    public List<Info> getInfoList() {
        return infoList;
    }


    public VendorYoutubeRecyclerAdapter(List<Info> infos) {
        infoHashSet.addAll(infos);
        infoList.addAll(infoHashSet);
    }


    @Override
    public int getItemCount() {
        return infoList.size();
    }

    @Override
    public int getItemViewType(int position) {
        /*if(infoList.get(position) instanceof Website){
            return 1;
        }else{
            return 0;
        }*/
        return 0;


    }

    public Info getInfo(int position){
        return infoList.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search, parent, false);


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Info info = infoList.get(position);

        String title = info.getTitle();
        String description = info.getDescription();


        if (holder.title != null && title != null && title.trim().isEmpty() == false) {
            holder.title.setText(title);
        }

        if (holder.description != null && description != null && description.trim().isEmpty() == false) {
            holder.description.setText(description);
        }


        if (holder.imageView != null && info.getThumbnailUri() != null) {
            RequestCreator requestCreator = Picasso.with(holder.itemView.getContext()).load(info.getThumbnailUri()).placeholder(R.drawable.progress_animation).transform(new CircleTransform());

            if (info instanceof Website) {
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

        } else if (holder.imageView != null && info.getThumbnailUri() == null) {
            if (info instanceof Website) {
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


        if (holder.borderline != null && (infoList.size() - 1) == position) {
            holder.borderline.setVisibility(View.GONE);
        } else if (holder.borderline != null) {
            holder.borderline.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public void addInfo(Info info) {

        infoHashSet.add(info);
        infoList.clear();
        infoList.addAll(infoHashSet);
        int itemCount = getItemCount();
        Log.d(TAG, "count2: " +  itemCount);

        if(itemCount >= 2)
            notifyItemChanged(getItemCount() - 2);
        if(itemCount == 0)
            notifyItemInserted(0);
        else
            notifyItemInserted(getItemCount() - 1);

    }

    public void addInfos(List<Info> infoList) {
        int itemCount = getItemCount();
        int start = getItemCount();
        infoHashSet.addAll(infoList);
        this.infoList.clear();
        this.infoList.addAll(infoHashSet);
        Log.d(TAG, "count1: " +  itemCount);
        if(itemCount > 0) {
            notifyItemRangeInserted(start, infoList.size());
        }else{
            notifyItemRangeInserted(0, infoList.size());
        }
    }

    public void clearInfos() {
        if (infoList != null) {
            infoList.clear();
            notifyDataSetChanged();
        }
    }

 /*   public void addInfos(List<Info> infos){
        int count = getItemCount();
        this.infoList.addAll(infos);
        for(int i = 0; i < infos.size(); i++){
            notifyItemInserted(infoList.size());
        }
    }*/


    public static class ViewHolder extends RecyclerView.ViewHolder {
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


