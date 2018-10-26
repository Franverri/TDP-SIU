package tdp.siu;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String from = remoteMessage.getFrom();
        Log.d("FIREBASEE", "Mensaje recibido de: " + from);

        if(remoteMessage.getNotification() != null){
            Log.d("FIREBASEE", "Notificacion: " + remoteMessage.getNotification().getBody());
        }

    }
}
