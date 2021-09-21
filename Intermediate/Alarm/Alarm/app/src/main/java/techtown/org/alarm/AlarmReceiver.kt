package techtown.org.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

// BroadcastReceiver를 상속받음, PendingIntent를 받아서 처리를 함
class AlarmReceiver: BroadcastReceiver() {
    companion object {
        // ID를 별도로 빼 둠
        const val NOTIFICATION_ID = 100
        const val NOTIFICATION_CHANNEL_ID = "1000"
    }

    // pendingintent를 받아서 처리하는 함수 -> Notification으로 처리할 것임
    override fun onReceive(context: Context, intent: Intent) {
        // 26버전 이상에서는 notification 처리를 위해 채널이 필요함
        createNotificationChannel(context)
        notifyNotification(context)
    }

    private fun createNotificationChannel(context: Context) {
        // MainActivity에서 this가 사용가능했음(Activity 자체를 context로 봐도 되니깐
        // 하지만 현재 클래스에서는 Activity가 아니므로 context를 따로 받아와야 함
        // context -> 앱의 글로벌 정보, API, 시스템에 대한 정보, SharedPreferences, 리소스 파일등 기능들을 저장을 해 둔 접근할 때 필요한 객체
        // activity 자체가 리소스 접근이 용이하여 context라고 말할 수 있음, 하지만 broadcastReceiver는 백그라운드에서 broadcast에서 pedning intent를 처리하므로 context를 받아와야함
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //26 이상이면
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "기상 알람",
                NotificationManager.IMPORTANCE_HIGH
            )
            // 채널 등록
            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
        }
    }

    private fun notifyNotification(context: Context) {
        with(NotificationManagerCompat.from(context)){
            // notification을 빌드해서 내보낼 것임
            val build = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("알람")
                .setContentText("일어날 시간입니다.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            notify(NOTIFICATION_ID, build.build()) // notify 설정을 해 줌
        }
    }

}