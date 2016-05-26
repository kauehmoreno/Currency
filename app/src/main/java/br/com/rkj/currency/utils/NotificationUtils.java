package br.com.rkj.currency.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;

import java.util.List;

import br.com.rkj.currency.Constante;
import br.com.rkj.currency.MainActivity;
import br.com.rkj.currency.R;

/**
 * Created by kauerodrigues on 5/17/16.
 * Notification itselfs!!
 * Basicamente define padroes da notificao como setTicker, setWhen, setContentTitle, setStyle,
 * setSound entre outros
 *
 */
public class NotificationUtils {
    public static void showNotificationMsg(Context context, String title, String message){
        if(TextUtils.isEmpty(message)){
            return;
        }

        if(isAppInBackGround(context)){
            int icon = R.mipmap.ic_launcher;
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent  = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            Notification notification = builder.setSmallIcon(icon).setTicker(title).setWhen(0).setAutoCancel(true).setContentTitle(title)
                                        .setStyle(inboxStyle)
                                        .setContentIntent(pendingIntent)
                                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                                        .setContentText(message)
                                        .build();

            // mostrar essa notificacao em si (getSystemService provides notifications)
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Constante.ID_NOTIFICACAO, notification);


        }else{
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    /**
    * metodo responsavel por verificar atividade do app em background
     * Verifica a versao do android, dado a versao acima de kitkat há uma execucao especifica para o mesmo
     * caso contrario adota-se uma outra abordagem.
    */
    public static boolean isAppInBackGround(Context context){
      boolean isInBackGround = true;
      ActivityManager activityManager= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
      if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH){
          List<ActivityManager.RunningAppProcessInfo> runningProcesses = activityManager.getRunningAppProcesses();

          for(ActivityManager.RunningAppProcessInfo processInfo : runningProcesses){
              if(processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                  for(String activeProcess : processInfo.pkgList){
                      if(activeProcess.equals(context.getPackageName())){
                          isInBackGround = false;
                      }
                  }
              }
          }
      } else {
          // deprecado o uso de getrunningtasks
          List<ActivityManager.RunningTaskInfo> taskInfos = activityManager.getRunningTasks(1);
          ComponentName componentName = taskInfos.get(0).topActivity;

          if(componentName.getPackageName().equals(context.getPackageName())){
              isInBackGround = false;
          }
      }
        return isInBackGround;
    }
}
