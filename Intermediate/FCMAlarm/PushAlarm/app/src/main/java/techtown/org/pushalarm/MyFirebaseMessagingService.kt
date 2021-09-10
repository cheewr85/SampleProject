package techtown.org.pushalarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService(){

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // 채널 완성
        createNotificationChannel()

        // 알림 창 type을 정함 enum과 동일하게 전달해줘서 타입을 전달해줌
        val type = remoteMessage.data["type"]
            ?.let { NotificationType.valueOf(it) }
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]

        type ?: return // type이 null일 경우 return을 함



        // notify를 해줌, title과 메시지를 맞춰서 알림을 보내줌
        // type 별 id로 메시지를 보내주게 함
        NotificationManagerCompat.from(this)
            .notify(type.id,createNotification(type, title, message))
    }


    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Oreo 버전(26) 이상일 경우에는 채널을 만들어줘야함
            // 채널 생성시 미리 정의한 ID, NAME과 중요도를 정해줌
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            // Description도 설정함
            channel.description = CHANNEL_DESCRIPTION

            // 채널을 추가해서 만들어주면 됨
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    // type에 따라 다르게 만들어주므로 type도 받아줘야함
    private fun createNotification(type: NotificationType,
                                   title: String?,
                                   message: String?
    ): Notification {
        // 알림창을 클릭했을 때 상호작용하기 위해서 인텐트 처리함
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("notificationType", "${type.title} 타입")
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) // 하나만 있게 함 즉, 실행시 같은 액티비티는 하나만 있음, 중복되서 처리 안 함
        }
        // 클릭시 상호작용하려면 pendingIntent로 감싸야 함
        // PendingIntent는 누군가에게 Intent를 다룰 수 있는 권한을 줌, NotificationManager에 전달해서 그곳에서 판단해서 처리함
        // id 미리 설정한 타입에 한해서는 pendingIntent가 달라짐, 설정을 해둬서
        val pendingIntent = PendingIntent.getActivity(this, type.id, intent, FLAG_UPDATE_CURRENT)

        // 알림 컨텐츠 만들기
        val notificationBuilder =  NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_circle_notifications_24) // 알림 떴을 때 좌측 상단 아이콘표시
            .setContentTitle(title) // CloudMessaging으로 넘겨받은 Title과 message를 설정함
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // 중요도 설정
            .setContentIntent(pendingIntent) // pendingIntent 전달
            .setAutoCancel(true) // 메시지 클릭시 자동으로 Notification이 사라짐


        when(type) {
            // type에 따라 다르게 설정하기 위해서 설정함
            NotificationType.NORMAL -> Unit
            NotificationType.EXPANDABLE -> {
                // 확장형 알림 만들기(큰 스타일로 만들 것임)
                    notificationBuilder.setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(
                                "\uD83D\uDE00 \uD83D\uDE03 \uD83D\uDE04 \uD83D\uDE01 \uD83D\uDE06 \uD83D\uDE05 \uD83D\uDE02 \uD83E\uDD23 \uD83E\uDD72 ☺" +
                                        "\uD83D\uDE0A \uD83D\uDE07 \uD83D\uDE42 \uD83D\uDE43 \uD83D\uDE09 \uD83D\uDE0C \uD83D\uDE0D \uD83E\uDD70 \uD83D\uDE18"+
                                        "\uD83D\uDE17 \uD83D\uDE19 \uD83D\uDE1A \uD83D\uDE0B \uD83D\uDE1B \uD83D\uDE1D \uD83D\uDE1C \uD83E\uDD2A \uD83E\uDD28 "+
                                        "\uD83E\uDDD0 \uD83E\uDD13 \uD83D\uDE0E \uD83E\uDD78 \uD83E\uDD29 \uD83E\uDD73 \uD83D\uDE0F \uD83D\uDE12 \uD83D\uDE1E \uD83D\uDE14" +
                                        "\uD83D\uDE1F \uD83D\uDE15 \uD83D\uDE41 ☹️ \uD83D\uDE23 \uD83D\uDE16 \uD83D\uDE2B \uD83D\uDE29 \uD83E\uDD7A \uD83D\uDE22 \uD83D\uDE2D" +
                                        "\uD83D\uDE24 \uD83D\uDE20 \uD83D\uDE21 \uD83E\uDD2C \uD83E\uDD2F \uD83D\uDE33 \uD83E\uDD75 \uD83E\uDD76 \uD83D\uDE31 \uD83D\uDE28"+
                                        "\uD83D\uDE30 \uD83D\uDE25 \uD83D\uDE13 \uD83E\uDD17 \uD83E\uDD14 \uD83E\uDD2D \uD83E\uDD2B \uD83E\uDD25 \uD83D\uDE36 \uD83D\uDE10 \uD83D\uDE11"+
                                        "\uD83D\uDE2C \uD83D\uDE44 \uD83D\uDE2F \uD83D\uDE26 \uD83D\uDE27 \uD83D\uDE2E \uD83D\uDE32 \uD83E\uDD71 \uD83D\uDE34 \uD83E\uDD24 \uD83D\uDE2A"+
                                        "\uD83D\uDE35 \uD83E\uDD10 \uD83E\uDD74")
                    )
            }
            NotificationType.CUSTOM -> {
                // Custom 알림 만들기(자체적인 레이아웃 만들어서 사용, 많이는 안 씀
                // 미리 xml로 만든 레이아웃에 대해서 아래와 같이 설정함
                notificationBuilder
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(
                        RemoteViews(
                            packageName,
                            R.layout.view_custom_notification
                        ).apply {
                            // RemoteViews에 있는 메소드 커스텀 레이아웃에 있는 view에 접근해서 수정하기 위해서 아래와 같이 씀
                            setTextViewText(R.id.title, title)
                            setTextViewText(R.id.message, message)
                        }
                    )

            }
        }

        return notificationBuilder.build()
    }

    companion object {
        // 채널 생성을 위한 상수 만들어줌
        private const val CHANNEL_NAME = "Emoji Party"
        private const val CHANNEL_DESCRIPTION = "Emoji Party를 위한 채널"
        private const val CHANNEL_ID = "Channel Id"
    }
}