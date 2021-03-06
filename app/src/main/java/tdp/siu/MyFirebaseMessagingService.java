package tdp.siu;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //SharedPref para almacenar datos de sesión
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        String from = remoteMessage.getFrom();
        Log.d("FIREBASEE", "Mensaje recibido de: " + from);

        if(remoteMessage.getData().size() > 0){
            Log.d("FIREBASEE", "Data: " + remoteMessage.getData());
            mostrarNotificacion(remoteMessage.getData().get("title"), remoteMessage.getData().get("text"), remoteMessage.getData().get("timestamp"));
        }

    }

    private void mostrarNotificacion(String title, String body, String timestamp) {
        String padron = sharedPref.getString("padron", null);
        String strNotificaciones = sharedPref.getString("strNotificaciones"+padron, "");
        strNotificaciones = strNotificaciones + timestamp + " - " + title + ": " + body + ";";
        editorShared.putString("strNotificaciones"+padron, strNotificaciones);
        editorShared.apply();

        Intent intent = new Intent(this, MainActivityAlumno.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_logo_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
