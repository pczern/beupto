package me.speeddeveloper.beupto.util;

import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;

import me.speeddeveloper.beupto.R;

/**
 * Created by phili on 7/30/2016.
 */
public class SocialSyncUtil {
    private boolean isTwitterSynced;
    private boolean isGooglePlusSynced;
    private boolean isFacebookSynced;


    public void init(FirebaseUser user){
        for(String provider : user.getProviders()) {
            if (GoogleAuthProvider.PROVIDER_ID.equalsIgnoreCase(provider)) {
               isGooglePlusSynced = true;
            }
            if (TwitterAuthProvider.PROVIDER_ID.equalsIgnoreCase(provider)) {
                isTwitterSynced = true;
            }
            if (FacebookAuthProvider.PROVIDER_ID.equalsIgnoreCase(provider)) {
                isFacebookSynced = true;
            }
        }

    }

    public boolean isTwitterSynced() {
        return isTwitterSynced;
    }

    public void setTwitterSynced(boolean twitterSynced) {
        isTwitterSynced = twitterSynced;
    }

    public boolean isGooglePlusSynced() {
        return isGooglePlusSynced;
    }

    public void setGooglePlusSynced(boolean googlePlusSynced) {
        isGooglePlusSynced = googlePlusSynced;
    }

    public boolean isFacebookSynced() {
        return isFacebookSynced;
    }

    public void setFacebookSynced(boolean facebookSynced) {
        isFacebookSynced = facebookSynced;
    }
}
