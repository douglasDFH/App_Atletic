package com.example.tallerappmovil.notificaciones;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.tallerappmovil.R;
import com.example.tallerappmovil.api.ApiClient;
import com.example.tallerappmovil.dashboard.DashboardActivity;
import com.example.tallerappmovil.session.SessionManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PushNotificationService extends FirebaseMessagingService {

    private static final String CHANNEL_ID   = "atletismo_push";
    private static final String CHANNEL_NAME = "AthleteSCZ Notificaciones";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        registrarTokenEnServidor(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        String titulo  = "AthleteSCZ";
        String cuerpo  = "";

        if (message.getNotification() != null) {
            titulo = message.getNotification().getTitle() != null
                    ? message.getNotification().getTitle() : titulo;
            cuerpo = message.getNotification().getBody() != null
                    ? message.getNotification().getBody() : cuerpo;
        } else if (!message.getData().isEmpty()) {
            titulo = message.getData().containsKey("titulo")
                    ? message.getData().get("titulo") : titulo;
            cuerpo = message.getData().containsKey("mensaje")
                    ? message.getData().get("mensaje") : cuerpo;
        }

        mostrarNotificacion(titulo, cuerpo);
    }

    private void mostrarNotificacion(String titulo, String cuerpo) {
        crearCanal();

        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(titulo)
                .setContentText(cuerpo)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(cuerpo))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    private void crearCanal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(canal);
        }
    }

    private void registrarTokenEnServidor(String token) {
        SessionManager session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) return;

        Map<String, String> body = new HashMap<>();
        body.put("token", token);

        ApiClient.getUsuariosService().registrarFcmToken(body)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call,
                                           @NonNull Response<Void> response) {}
                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {}
                });
    }
}
