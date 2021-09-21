## 알람앱
- TimePicekrDialog를 통해서 시간을 선택하고 해당 시간에 알람을 맞춤

- 여기서 알람을 켜고 끄는 기능과 함께 시간 역시 TimePickerDialog로 재설정 가능함

- 여기서 시간 단위는 AM,PM으로 구분해서 설정함

- 알람을 맞추면 알람에 대해서 Notification을 활용해서 알람이 울리고 오게끔 설정함

## 메인화면
![one](/Intermediate/Alarm/img/one.png)

- 시간과 AM,PM을 나타내는 TextView와 알람을 켜고 끄는 버튼 시간을 재설정하는 버튼을 구성

- 그리고 원형의 View는 직접 Drawable에 그려서 추가함

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval">

    <solid android:color="@color/background_black" />
    <stroke android:width="1dp"
        android:color="@color/white"/>
    <size
        android:width="250dp"
        android:height="250dp" />


</shape>
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/background_black"
    tools:context=".MainActivity">

    <!--Drawable로 그리고 위에 덧입히기 위해서 아래와 같이 설정-->
    <View
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_marginStart="50dp"
        android:layout_marginEnd="50dp"
        android:background="@drawable/background_white_ring"
        app:layout_constraintBottom_toTopOf="@id/onOffButton"
        app:layout_constraintDimensionRatio="H, 1:1"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/timeTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="09:30"
        android:textColor="@color/white"
        android:textSize="50sp"
        app:layout_constraintBottom_toTopOf="@id/ampmTextView"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_chainStyle="packed" />

    <TextView
        android:id="@+id/ampmTextView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="AM"
        android:textColor="@color/white"
        android:textSize="25sp"
        app:layout_constraintBottom_toTopOf="@id/onOffButton"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/timeTextView" />

    <Button
        android:id="@+id/onOffButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="@string/on_alarm"
        app:layout_constraintBottom_toTopOf="@id/changeAlarmTimeButton"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />

    <Button
        android:id="@+id/changeAlarmTimeButton"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="@string/change_time"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### 구현코드
- 먼저 시간과 알람에 등록을 함을 알리기 위한 데이터 클래스를 만듬

- 데이터 클래스에서 값을 받은 뒤 넘겨받은 값에 따라 설정하게 처리함

- SharedPreferences를 활용하여 DB에 알람으로 등록한 시간과 상태를 저장함

### AlarmDisplayModel.kt
```kotlin
package techtown.org.alarm

/*
알람 데이터에 대한 것을 저장해두는 데이터 클래스
 */

data class AlarmDisplayModel(
    val hour: Int,
    val minute: Int,
    var onOff: Boolean // onOff 상황에 따라 다르므로 var로 만듬
) {
    // 받아온 데이터를 가공을 하고 UI에 업데이트 하게 할 수 있게 함, String 형태로 나갈 것임 09시 30분등의 형태로
    val timeText: String
    // val로 설정했으므로 getter로 설정 사용하기 위해서
        get() {
        // 9 -> 09, 23 -> 11로 들어오게 변환함 m도 3-> 03으로(String format을 통해)
            val h = "%02d".format(if(hour < 12) hour else hour - 12)
            val m = "%02d".format(minute)

            return "$h:$m"
        }

    val ampmText: String
    // ampm 설정을 위해서 getter 사용
        get() {
            return if(hour < 12) "AM" else "PM"
        }

    val onOffText: String
    // boolean 값에 따라 텍스트 바꿈
        get() {
            return if (onOff) "알람 끄기" else "알람 켜기"
        }

    // DB에 sharedPreferences에 저장을 위한 데이터를 만드는 함수(구분지어서 보내기 위해서)
    fun makeDataForDB(): String {
        return "$hour:$minute"
    }
}
```

- 그리고 알람을 받은 경우 해당 부분은 BroadcastReceiver를 통해서 상태를 체크하고 pendingIntent를 활용하여 알람을 설정하고 Intent 처리를 통해 Notification을 구현함, 이를 별도 클래스 파일에서 처리함

- 해당 클래스는 BroadcastReceiver를 상속받고 PendingIntent를 받아서 처리를 함

```kotlin
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
```

### MainActivity.kt
- 사전에 만든 데이터 클래스와 Notification을 생성하는 클래스를 메인에서 활용하면서 알람 켜기 끄기 버튼과 시간 재설정 버튼 등 해당 버튼에 맞게 이벤트 처리를 함

- 이렇게 설정한 값에 대해서는 sharedPreferences를 통해서 데이터를 저장하고 pendingIntent를 통해서 알람을 등록하며 View를 그리는 작업을 해 줌 

```kotlin
package techtown.org.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 뷰를 초기화
        initOnOffButton()
        initChangeAlarmTimeButton()

        // step1 데이터 가져오기
        val model = fetchDataFromSharedPreferences()

        // step2 View에 데이터 그려주기
        renderView(model)
    }

    private fun initOnOffButton() {
        // OnOff버튼을 가져와서 클릭 리스너 설정
        val onOffButton = findViewById<Button>(R.id.onOffButton)
        onOffButton.setOnClickListener{
            // 데이터를 확인을 함(sharedpreference로 저장한)
            // tag는 object로 저장되어 있으므로 as를 통해 AlarmDisplayModel로 형변환을 해줘야함
            val model = it.tag as? AlarmDisplayModel ?: return@setOnClickListener // null 처리
            // 데이터를 저장함
            val newModel = saveAlarmModel(model.hour, model.minute, model.onOff.not())
            renderView(newModel) // onOff 기능을 바꿔줌

            // onoff에 따라 작업을 처리함
            if(newModel.onOff) {
                // 켜진 경우 -> 알람을 등록함, 캘린더를 통해 등록
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, newModel.hour)
                    set(Calendar.MINUTE, newModel.minute)

                    if(before(Calendar.getInstance())) {
                        // 현재 시간을 가져와서 만약 이미 지났다면 다음날 설정하게 함
                        add(Calendar.DATE, 1)
                    }
                }
                // 알람 매니저를 가져와서 등록함
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(this, AlarmManager::class.java)
                // pedningIntent를 통해서 알람 설정
                val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)

                // 하루에 한 번씩 pendingIntent가 실행되고 알람이 됨
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )

            } else {
                // 꺼진 경우 -> 알람을 제거
                cancelAlarm()
            }


        }
    }

    private fun initChangeAlarmTimeButton() {
        // changeAlarmButton을 가져와서 클릭 리스너 설정
        val changeAlarmButton = findViewById<Button>(R.id.changeAlarmTimeButton)
        changeAlarmButton.setOnClickListener{

            // 현재시간을 일단 가져옴.(캘린더(자바)를 통해 가져옴, 시스템에 설정된 시간 가져옴)
            // calendar get 함수를 통해 가져옴
            val calendar = Calendar.getInstance()

            // 먼저 TimePickerDialog 가져와서 적용(람다로 적용), 12시간 형식으로 설정
            TimePickerDialog(this, {picker, hour, minute ->


                // 데이터를 만들고 이를 저장하는 함수 활용(데이터 저장)
                val model = saveAlarmModel(hour, minute, false)
                // 뷰를 업데이트 함(renderView를 통해 재설정된 데이터에 대해서 View를 다시 그림)
                renderView(model)
                // 기존에 있던 알람을 삭제한다.
                cancelAlarm()


            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false)
                    .show() // TimePickerDialog 실행


        }
    }

    // 앞서 TimePickerDialog에서 모델을 만들고 데이터를 저장하기 위한 함수
    private fun saveAlarmModel(
            hour: Int,
            minute: Int,
            onOff: Boolean
    ): AlarmDisplayModel {
        // 저장할 때 앞서 설정한 데이터 모델을 설정해야함(데이터를 만듬)
        val model = AlarmDisplayModel(
                hour = hour,
                minute = minute,
                onOff = onOff
        )

        // sharedPreferences를 통해서 실제 그 값을 저장해둠(time에 대해서 저장)
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        // with함수를 활용해 editor을 써서 사용함
        with(sharedPreferences.edit()) {
            putString(ALARM_KEY,model.makeDataForDB()) // 구분지어서 시간을 받음
            putBoolean(ONOFF_KEY,model.onOff)
            commit()
        }

        return model
    }

    // SharedPreferences에서 데이터를 가져와서 앞서 정의한 데이터 모델에 있는 데이터를 가져옴
    private fun fetchDataFromSharedPreferences(): AlarmDisplayModel {
        // sharedPreferences에 저장되어 있는 값을 가져옴
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        // 저장된 값을 꺼내옴(key 값으로 가져옴), 디폴트 값을 설정함
        // Java에서 쓰는 것이라 nullable임, 이 nullable 처리도 해야함
        val timeDBValue = sharedPreferences.getString(ALARM_KEY, "9:30") ?: "9:30"
        val onOffDBValue = sharedPreferences.getBoolean(ONOFF_KEY, false)
        val alarmData = timeDBValue.split(":") // : 기준으로 나눠 list형태로 저장함

        val alarmModel = AlarmDisplayModel(
            // 리스트 형태로 저장했기 때문에 처음은 hour, 그 다음 minute임, 그리고 boolean도 저장해서 model로 설정함
            hour = alarmData[0].toInt(),
            minute = alarmData[1].toInt(),
            onOff = onOffDBValue
        )

        // 보정(실제 앱은 알람 등록이 안됨, sharedPreferences를 off로 바꿔줘야 함), 실제 등록되었는데 sharedPrefences는 꺼져있음
        // 이런 예외처리를 해줘야함, BroadCastReceiver를 활용하면됨
        // 여기서 pendingIntent에서 알람일 때의 request code와 현재 context 기준으로 설정을 하는데 alarm 설정에 대해서는 해당 java 파일을 활용함
        // 그리고 flag로 알람이 없으면 없는대로 놔두고 없으면 만들고 있으면 업데이트 하는 flag로 쓸 것
        // NO_CREATE -> 없으면 놔두고(null) 있으면 가져오게 함
        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, Intent(this, AlarmReceiver::class.java),PendingIntent.FLAG_NO_CREATE)

        // 예외처리
        if ((pendingIntent == null) and alarmModel.onOff) {
            // 만약 알람이 꺼져있는데, 데이터가 켜져있는 경우는 데이터를 수정함
            alarmModel.onOff = false
        } else if ((pendingIntent != null) and alarmModel.onOff.not()) {
            // 알람은 켜져 있는데 데이터가 껴져있는 경우, 알람을 취소함
            pendingIntent.cancel()
        }
        // 위에서 처리한 예외가 아닌 경우 model을 그냥 반환해주면 됨
        return alarmModel
    }

    // 가져온 데이터에 대해서 해당 내용에 맞게 View를 그려줌
    private fun renderView(model: AlarmDisplayModel) {
        // View를 찾아오고 model에 있는 값을 연결하면 됨
        findViewById<TextView>(R.id.ampmTextView).apply{
            text = model.ampmText
        }
        
        findViewById<TextView>(R.id.timeTextView).apply {
            text = model.timeText
        }

        findViewById<Button>(R.id.onOffButton).apply {
            text = model.onOffText
            // model은 전역변수로 저장을 하지 않았기 때문에 실제 데이터를 가져오기 위해서 tag 사용함
            // tag는 object임 model을 저장함, 이러면 model을 tag에 저장하고 버튼을 누르면 tag에 있는 데이터를 가져와서 구성할 수 있음
            tag = model
        }
    }

    private fun cancelAlarm() {
        // 알람을 제거하는 함수
        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, Intent(this, AlarmReceiver::class.java),PendingIntent.FLAG_NO_CREATE)
        pendingIntent?.cancel() // null일수도 있기 때문에 null 처리를 함
    }

    // KEY 값을 바뀌거나 오타가 나면 확인할 수 없기 때문에 바뀌지 않는 상수로 정의해서 사용함
    companion object {
        private const val SHARED_PREFERENCES_NAME = "time"
        private const val ALARM_KEY = "alarm"
        private const val ONOFF_KEY = "onOff"
        private const val ALARM_REQUEST_CODE = 1000
    }
}
```