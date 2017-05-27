package me.speeddeveloper.beupto.util;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Map;

import me.speeddeveloper.beupto.firebase.FireDB;
import me.speeddeveloper.beupto.model.InfoModel;

/**
 * Created by phili on 8/17/2016.
 */
public class SubscribableUtil {

    private static DatabaseReference subscribablesReference;
    private static final String TAG = SubscribeUtil.class.getSimpleName();



    public static void init(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        subscribablesReference = reference.child(FireDB.NODE_SUBSCRIBABLE);
    }


    public static void setSubscribable(final InfoModel model, final IFunctional<Boolean> e) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void[] voids) {
                String uriString = model.getUri();

                final String uri = MyUtil.encode(Uri.parse(uriString).toString());

                Map<String, Object> map = model.toMap();

                subscribablesReference.child(uri).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        e.makeIt(task.isSuccessful());
                        subscribablesReference.child(uri).child(FireDB.NODE_CREATION_DATE).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.d(TAG, "called");
                                if (dataSnapshot == null || dataSnapshot.exists() == false) {
                                    subscribablesReference.child(uri).child(FireDB.NODE_CREATION_DATE).setValue(System.currentTimeMillis());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });
                return null;
            }


        }.execute();
    }

}

