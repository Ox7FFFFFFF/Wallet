package dee.wallet;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by dee on 12/5/17.
 * Alarm Receiver
 */

public class AlarmReceiver extends BroadcastReceiver {
    private long[] vibrate_effect = {1000, 500, 1000, 400, 1000, 300, 1000, 200, 1000, 100};
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if(bundle.get("msg").equals("alarm")){
            Log.e("duration",bundle.getString("duration"));

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

            Intent notifyIntent = new Intent(context, MainActivity.class);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent appIntent = PendingIntent.getActivity(context, 0, notifyIntent, 0);

            Notification notification = new Notification.Builder(context)
                    .setContentIntent(appIntent)
                    .setSmallIcon(R.drawable.icon_wallet)
                    .setTicker("notification on status bar.") // 設置狀態列的顯示的資訊
                    .setWhen(System.currentTimeMillis())// 設置時間發生時間
                    .setAutoCancel(true) // 設置通知被使用者點擊後是否清除  //notification.flags = Notification.FLAG_AUTO_CANCEL;
                    .setContentTitle("Wallet") // 設置下拉清單裡的標題
                    .setContentText("Accounting Reminder")
                    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS) //使用默認手機震動提示
                    .setVibrate(vibrate_effect) //自訂震動長度
                    .setLights(0xff00ff00, 300, 1000) //自訂燈光閃爍 (ledARGB, vibrateledOnMS, ledOffMS)
                    .build();
            //TODO
            //把指定ID的通知持久的發送到狀態條上
            mNotificationManager.notify(0, notification);
        }
    }
}
