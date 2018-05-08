package jaffa.com.jaffareviews;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;

import java.io.IOException;

public class MyIDListenerService extends InstanceIDListenerService {
    @Override
     public void onTokenRefresh() {
     InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
    try {
            String token = instanceID.getToken(getApplicationContext().getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

     }
}