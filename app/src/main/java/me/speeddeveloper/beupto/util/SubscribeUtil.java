package me.speeddeveloper.beupto.util;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import me.speeddeveloper.beupto.firebase.FireDB;
import me.speeddeveloper.beupto.model.Info;
import me.speeddeveloper.beupto.model.InfoModel;

/**
 * Created by phili on 8/17/2016.
 */
public class SubscribeUtil {
    private static final String TAG = SubscribeUtil.class.getSimpleName();
    private static DatabaseReference subscribablesReference;
    private static DatabaseReference userSubscriptionUrisReference;
    private static List<String> subscribedUris = new ArrayList<>();
    private static FirebaseUser user;
    private static DatabaseReference reference = FirebaseDatabase.getInstance().getReference();



    public static List<String> getSubscribedUris(){
      return subscribedUris;
    };

    public static void init(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        userSubscriptionUrisReference = reference.child(FireDB.NODE_USER).child(user.getUid()).child(FireDB.NODE_SUBSCRIPTION);
        subscribablesReference = reference.child(FireDB.NODE_SUBSCRIBABLE);
        userSubscriptionUrisReference.addChildEventListener(new ChildEventListener() {
                @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                String subscribedUri = dataSnapshot.getKey();
                subscribedUris.add(subscribedUri);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String subscribedUri = dataSnapshot.getKey();
                subscribedUris.remove(subscribedUri);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    public static boolean hasSubscribed(final String uri){
        String uriString = MyUtil.encode(Uri.parse(uri).toString());
        Log.d(TAG, "uri: " + uriString);
        return subscribedUris.contains(uriString);
    }

    public static void subscribe(final String uriString, final IFunctional<Boolean> e){
        Log.d(TAG, "uriString1: " + uriString);

        final String uri = MyUtil.encode(Uri.parse(uriString).toString());


        subscribablesReference.child(uri).child(FireDB.NODE_FOLLOWER).child(user.getUid()).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    userSubscriptionUrisReference.child(uri).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            e.makeIt(task.isSuccessful());
                        }
                    });
                else{
                    e.makeIt(false);
                }
            }
        });
    }


    public static void unsubscribe(final String uri, final IFunctional<Boolean> e){
        subscribablesReference.child(MyUtil.encode(uri)).child(FireDB.NODE_FOLLOWER).child(user.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    userSubscriptionUrisReference.child(MyUtil.encode(uri)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            e.makeIt(task.isSuccessful());
                        }
                    });
                }else{
                    e.makeIt(false);
                }
            }
        });

    }








}
