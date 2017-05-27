package me.speeddeveloper.beupto.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.api.services.youtube.model.Subscription;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter_extensions.ActionModeHelper;
import com.mikepenz.fastadapter_extensions.UndoHelper;
import com.mikepenz.materialize.util.UIUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import me.speeddeveloper.beupto.R;
import me.speeddeveloper.beupto.activity.style.BaseActivityActionBar;
import me.speeddeveloper.beupto.adapter.SubscriptionItem;
import me.speeddeveloper.beupto.adapter.SubscriptionRecyclerAdapter;
import me.speeddeveloper.beupto.firebase.FireDB;
import me.speeddeveloper.beupto.model.Info;
import me.speeddeveloper.beupto.model.InfoModel;
import me.speeddeveloper.beupto.util.IFunctional;
import me.speeddeveloper.beupto.util.SubscribeUtil;

/**
 * Created by phili on 8/19/2016.
 */
public class SubscriptionActivity extends BaseActivityActionBar{



    private List<SubscriptionItem> subscriptionsList = new ArrayList<>();
    private static final String TAG = SubscriptionActivity.class.getSimpleName();
    private UndoHelper mUndoHelper;
    private ActionModeHelper mActionModeHelper;
    private FastItemAdapter<SubscriptionItem> fastAdapter;

    private Set<InfoModel> infoHashSet = new LinkedHashSet<>();
    private Set<SubscriptionItem> itemsForDeletion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_subscription);
        fastAdapter = new FastItemAdapter<>();
        fastAdapter.withSavedInstanceState(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new SlideInDownAnimator());


        mUndoHelper = new UndoHelper(fastAdapter, new UndoHelper.UndoListener<SubscriptionItem>() {
            @Override
            public void commitRemove(Set<Integer> positions, ArrayList<FastAdapter.RelativeInfo<SubscriptionItem>> removed) {
                Log.e("UndoHelper", "Positions: " + positions.toString() + " Removed: " + removed.size());
            }
        });

        mActionModeHelper = new ActionModeHelper(fastAdapter, R.menu.activity_subscription, new ActionBarCallBack());





        fastAdapter.setHasStableIds(true);
        fastAdapter.withSelectable(true);
        fastAdapter.withMultiSelect(true);
        fastAdapter.withSelectOnLongClick(true);




        fastAdapter.withOnPreClickListener(new FastAdapter.OnClickListener<SubscriptionItem>() {


            @Override
            public boolean onClick(View v, IAdapter<SubscriptionItem> adapter, SubscriptionItem item, int position) {
                //we handle the default onClick behavior for the actionMode. This will return null if it didn't do anything and you can handle a normal onClick
                Boolean res = mActionModeHelper.onClick(item);
                return res != null ? res : false;
            }
        });
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<SubscriptionItem>() {
            @Override
            public boolean onClick(View v, IAdapter<SubscriptionItem> adapter, SubscriptionItem item, int position) {


                Toast.makeText(v.getContext(), "SelectedCount: " + fastAdapter.getSelections().size() + " ItemsCount: " + fastAdapter.getSelectedItems().size(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        fastAdapter.withOnLongClickListener(new FastAdapter.OnLongClickListener<SubscriptionItem>() {
            @Override
            public boolean onLongClick(View v, IAdapter<SubscriptionItem> adapter, SubscriptionItem item, int position) {
                ActionMode actionMode = mActionModeHelper.onLongClick(SubscriptionActivity.this, position);

                if (actionMode != null) {

                    //v.setBackgroundResource(R.color.md_red_A100);
                    //we want color our CAB
                   // findViewById(R.id.action_mode_bar).setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(Su.this, R.attr.colorPrimary, R.color.material_drawer_primary));
                }

                //if we have no actionMode we do not consume the event
                return actionMode != null;
            }
        });




//set our adapters to the RecyclerView
//we wrap our FastAdapter inside the ItemAdapter -> This allows us to chain adapters for more complex useCases

//set the items to your ItemAdapter


        DatabaseReference subscribableReference = FirebaseDatabase.getInstance().getReference().child(FireDB.NODE_SUBSCRIBABLE);


        for(String subscribedUri : SubscribeUtil.getSubscribedUris()){
            subscribableReference.child(subscribedUri).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null && dataSnapshot.exists()) {
                        Log.d(TAG, "here's a key " + dataSnapshot.getKey());
                        InfoModel infoModel = dataSnapshot.getValue(InfoModel.class);
                        Log.d(TAG, "title : " + infoModel.getTitle());
                        if(!infoHashSet.contains(infoModel)) {
                            infoHashSet.add(infoModel);
                            fastAdapter.add(new SubscriptionItem(infoModel));
                        }

                        Log.d(TAG, "added something");
                    }else{
                        Log.e(TAG, "datasnapshot is null or doesn't exist?");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, databaseError.toString());
                }
            });

        }






        recyclerView.setAdapter(fastAdapter);



     /*   List<SubscriptionItem> items = new ArrayList<>();
        for (InfoModel model : subscriptionsList) {
            items.add(new SubscriptionItem(model));
        }
        itemAdapter.add(items);*/










    }




    @Override
    protected void onStop() {
        super.onStop();
        if(itemsForDeletion != null)
        for(SubscriptionItem subscriptionItem : itemsForDeletion){

            SubscribeUtil.unsubscribe(subscriptionItem.getInfoModel().getUri(), new IFunctional<Boolean>() {
                @Override
                public void makeIt(Boolean aBoolean) {
                    Log.d(TAG, "subscription deleted succesfully : " + aBoolean);
                }
            });

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundel
        outState = fastAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    class ActionBarCallBack implements ActionMode.Callback {

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {




            itemsForDeletion = fastAdapter.getSelectedItems();


            if(fastAdapter.getSelections() != null){
                Log.d(TAG, "first selection : " + fastAdapter.getSelections().toArray()[0]);
            }

            if(fastAdapter.getSelections().size() > 1)
                mUndoHelper.remove(findViewById(android.R.id.content), "Items removed", "Undo", Snackbar.LENGTH_LONG, fastAdapter.getSelections());
            else if(fastAdapter.getSelections().size() == 1)
                mUndoHelper.remove(findViewById(android.R.id.content), "Item removed", "Undo", Snackbar.LENGTH_LONG, fastAdapter.getSelections());




            //as we no longer have a selection so the actionMode can be finished
            mode.finish();
            //we consume the event
            return true;

        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
    }
}
