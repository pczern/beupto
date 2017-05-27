package me.speeddeveloper.beupto.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import me.speeddeveloper.beupto.R;
import me.speeddeveloper.beupto.activity.style.BaseActivityNoActionBar;
import me.speeddeveloper.beupto.activity.style.BaseActivityTranslucentStatus;
import me.speeddeveloper.beupto.adapter.PopularRecyclerAdapter;
import me.speeddeveloper.beupto.firebase.FireDB;
import me.speeddeveloper.beupto.model.InfoModel;

/**
 * Created by phili on 8/19/2016.
 */
public class PopularActivity extends BaseActivityTranslucentStatus{

    private static final String TAG = PopularActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_popular);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        RecyclerView newChannelsRecyclerView = (RecyclerView) findViewById(R.id.newchannels_recyclerview);
        RecyclerView newVideosRecyclerView = (RecyclerView) findViewById(R.id.newvideos_recyclerview);

        newChannelsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newVideosRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Query query = FirebaseDatabase.getInstance().getReference().child(FireDB.NODE_SUBSCRIBABLE).limitToLast(5).orderByChild(FireDB.NODE_CREATION_DATE);

        final PopularRecyclerAdapter recyclerAdapter = new PopularRecyclerAdapter();
        newChannelsRecyclerView.setAdapter(recyclerAdapter);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot != null && dataSnapshot.exists()) {
                    String key = dataSnapshot.getKey();
                    Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                    while (iter.hasNext()){
                        DataSnapshot snapshot = iter.next();
                        InfoModel infoModel = snapshot.getValue(InfoModel.class);
                        recyclerAdapter.addInfo(infoModel.asInfo());
                    }
                    Log.d(TAG, "key : " + key);
                    /*InfoModel infoModel = dataSnapshot.getValue();
                    recyclerAdapter.addInfo(infoModel.asInfo());*/


                }else{
                    Log.e(TAG, "datasnapshot does not exist");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });












    }
}
