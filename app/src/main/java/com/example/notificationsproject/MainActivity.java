package com.example.notificationsproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.os.BuildCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button notifBtn;
    private Button updateBtn;
    private Button cancelBtn;

    // ID untuk mengirim notificationnya
    private static final String CHANNEL_ID = BuildConfig.APPLICATION_ID + "notification";

    private NotificationManager notificationManager;

    private static final int NOTIF_ID = 0;

    private static final String UPDATE_EVENT = "UPDATE_EVENT";

    private NotificationReceiver notificationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notificationReceiver = new NotificationReceiver();
        registerReceiver(notificationReceiver,new IntentFilter(UPDATE_EVENT));

        // Inisialisasi notification manager
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Step yang tidak ada di modul
        // Pada dasarnya SDK_INT selalu lebih dari Oreo ( API 26 ), jadi tidak perlu dicek
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Membuat channel notifikasi
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"Notification Project", NotificationManager.IMPORTANCE_DEFAULT);

            // Daftarkan channel ke Notification Manager
            notificationManager.createNotificationChannel(channel);
        }

        notifBtn = findViewById(R.id.notif_btn);
        updateBtn = findViewById(R.id.update_btn);
        cancelBtn = findViewById(R.id.cancel_btn);

        notifBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNotification();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelNotification();
            }
        });

        notifBtn.setEnabled(true);
        updateBtn.setEnabled(false);
        cancelBtn.setEnabled(false);
    }

    private void sendNotification() {
        // Buat intent untuk membuat notifikasinya mengunjungi aplikasi kita ketika notifikasi ditekan
        Intent contentIntent = new Intent(getApplicationContext(), MainActivity.class);

        // Bungkus intent ke dalam PendingIntent untuk memberikan target action yang akan dilakukan
        PendingIntent pendingContentIntent = PendingIntent.getActivity(getApplicationContext(), NOTIF_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Link yang dikunjungi ketika action di tekan
        String GUIDE_URL = "https://developer.android.com/guide/topics/ui/notifiers/notifications";

        // Buat intent untuk membuat notifikasinya mengunjungi situs dari link yang disediakan
        Intent learnMoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GUIDE_URL));

        // Bungkus intent ke dalam PendingIntent untuk melakukan target action
        PendingIntent pendingLearnMoreIntent = PendingIntent.getActivity(getApplicationContext(),NOTIF_ID,learnMoreIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        // Buat intent untuk membuat notifikasinya mengupdate
        Intent updateIntent = new Intent(UPDATE_EVENT);

        // Bungkus intent ke dalam PendingIntent untuk memberikan melakukan broadcast
        PendingIntent pendingUpdateIntent = PendingIntent.getBroadcast(getApplicationContext(),NOTIF_ID,updateIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        // Inisialisasi notifikasinya
        NotificationCompat.Builder built = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);

        // Mengisi judul, konten, dan icon untuk notifikasi
        built.setContentTitle("You've been notified");
        built.setContentText("This is notification text");
        built.setSmallIcon(R.drawable.ic_baseline_notifications_active_24);

        // Menetapkan konten/halaman yg dikunjungi oleh notifikasi ketika di tekan
        built.setContentIntent(pendingContentIntent);
        built.setPriority(NotificationCompat.PRIORITY_HIGH);

        // Memberikan notifikasi tombol Learn More dan Update
        built.addAction(R.drawable.ic_baseline_notifications_active_24,"Learn More",pendingLearnMoreIntent);
        built.addAction(R.drawable.ic_baseline_notifications_active_24,"Update",pendingUpdateIntent);


        // Membangun notifikasinya
        Notification notif = built.build();
        notificationManager.notify(NOTIF_ID,notif);

        notifBtn.setEnabled(false);
        updateBtn.setEnabled(true);
        cancelBtn.setEnabled(true);
    }

    private void cancelNotification() {
        // Menghapus notifikasi seluruhnya
        notificationManager.cancel(NOTIF_ID);

        notifBtn.setEnabled(true);
        updateBtn.setEnabled(false);
        cancelBtn.setEnabled(false);
    }

    private void updateNotification() {
        Intent contentIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingContentIntent = PendingIntent.getActivity(getApplicationContext(), NOTIF_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Inisialisasi notifikasinya
        NotificationCompat.Builder built = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);

        // Mengisi judul, konten, dan icon untuk notifikasi
        built.setContentTitle("You've been notified");
        built.setContentText("This is notification text");
        built.setSmallIcon(R.drawable.ic_baseline_notifications_active_24);
        built.setContentIntent(pendingContentIntent);
        built.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Memasukkan gambar ke notification ketika update dengan expanded notification
        Bitmap mascotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_name);
        built.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(mascotBitmap).setBigContentTitle("The notification has been updated"));

        // Membangun notifikasinya
        Notification notif = built.build();

        // Mengirim notifikasi mengggunakan notification manager
        notificationManager.notify(NOTIF_ID,notif);

        notifBtn.setEnabled(false);
        updateBtn.setEnabled(true);
        cancelBtn.setEnabled(true);
    }

    public class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == UPDATE_EVENT ) {
                updateNotification();
            }
        }
    }
}