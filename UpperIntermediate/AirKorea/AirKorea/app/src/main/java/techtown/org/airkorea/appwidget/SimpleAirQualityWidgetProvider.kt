package techtown.org.airkorea.appwidget

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import techtown.org.airkorea.R
import techtown.org.airkorea.data.Repository
import techtown.org.airkorea.data.models.airquality.Grade

class SimpleAirQualityWidgetProvider : AppWidgetProvider() {

    // 앱 위젯에 내용을 갱신하기 위해서 오버라이딩한 함수
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        // 서비스를 시작해줌
        ContextCompat.startForegroundService(
            context!!,
            Intent(context, UpdateWidgetService::class.java)
        )
    }

    class UpdateWidgetService : LifecycleService() {

        override fun onCreate() {
            super.onCreate()

            createChannelIfNeeded() // API 26 이상에서 채널을 만듬
            startForeground(
                NOTIFICATION_ID,
                createNotification(),
            )
        }

        // foreground 시작을 할 때 불리는 Command
        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

            // 위치를 먼저 가져옴
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // 권한이 없는 경우, 위젯을 업데이트 해 줌 그에 맞게
                val updateViews = RemoteViews(packageName, R.layout.widget_simple).apply {
                    setTextViewText(
                        R.id.resultTextView,
                        "권한 없음"
                    )
                    setViewVisibility(R.id.labelTextView, View.GONE)
                    setViewVisibility(R.id.gradeLabelTextView, View.GONE)
                }
                updateWidget(updateViews)
                stopSelf() // 권한 없으면 종료함

                return super.onStartCommand(intent, flags, startId)
            }

            LocationServices.getFusedLocationProviderClient(this).lastLocation
                .addOnSuccessListener { location ->
                    lifecycleScope.launch {
                        // try-catch로 위젯 갱신 실패시 상황도 처리함
                        try {
                            // 데이터 통신을 위해서 코루틴 시작, 정보를 가져와서 갱신을 함
                            val nearbyMonitoringStation = Repository.getNearbyMonitoringStation(
                                location.latitude,
                                location.longitude
                            )
                            val measuredValue =
                                Repository.getLatestAirQualityData(nearbyMonitoringStation!!.stationName!!)
                            // 정보를 바탕으로 위뎃을 업데이트 함
                            val updateViews =
                                RemoteViews(packageName, R.layout.widget_simple).apply {
                                    setViewVisibility(R.id.labelTextView, View.VISIBLE)
                                    setViewVisibility(R.id.gradeLabelTextView, View.VISIBLE)

                                    val currentGrade = (measuredValue?.khaiGrade ?: Grade.UNKNOWN)
                                    setTextViewText(R.id.resultTextView, currentGrade.emoji)
                                    setTextViewText(R.id.gradeLabelTextView, currentGrade.label)
                                }

                            // 업데이트 하고 끝냄
                            updateWidget(updateViews)
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        } finally {
                            stopSelf()
                        }

                    }
                }

            return super.onStartCommand(intent, flags, startId)
        }

        override fun onDestroy() {
            super.onDestroy()
            stopForeground(true)
        }

        // 채널을 만듬 서비스를 위해서
        private fun createChannelIfNeeded() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // 해당 버전 이상인 경우 채널을 만들어야함
                (getSystemService(NOTIFICATION_SERVICE) as? NotificationManager)
                    ?.createNotificationChannel(
                        NotificationChannel(
                            WIDGET_REFRESH_CHANNEL_ID,
                            "위젯 갱신 채널",
                            NotificationManager.IMPORTANCE_LOW
                        )
                    )
            }
        }

        // Notification 생성함
        private fun createNotification(): Notification =
            NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_baseline_refresh_24)
                .setChannelId(WIDGET_REFRESH_CHANNEL_ID)
                .build()

        private fun updateWidget(updateViews: RemoteViews) {
            val widgetProvider = ComponentName(this, SimpleAirQualityWidgetProvider::class.java)
            AppWidgetManager.getInstance(this).updateAppWidget(widgetProvider, updateViews)
        }
    }

    companion object {
        private const val WIDGET_REFRESH_CHANNEL_ID = "WIDGET_REFRESH_CHANNEL_ID"
        private const val NOTIFICATION_ID = 101
    }
}