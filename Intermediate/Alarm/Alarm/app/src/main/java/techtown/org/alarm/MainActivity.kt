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
        renderView(model)

        // step2 View에 데이터 그려주기
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