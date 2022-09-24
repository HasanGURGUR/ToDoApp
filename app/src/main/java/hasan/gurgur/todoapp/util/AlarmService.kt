package hasan.gurgur.todoapp.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import hasan.gurgur.todoapp.receiver.AlarmReceiver

class AlarmService(private val context: Context) {

    private val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?


    fun setExactAlarm(timeInMillis: Long) {
        setAlarm(
            timeInMillis,
            getPendingIntent(
                getIntent().apply {

                    action = Constant.ACTION_SET_EXACT_ALARM
                    putExtra(Constant.EXTRA_EXACT_ALARM_TIME, timeInMillis)


                }
            )
        )

    }

    //Every week
    fun setRepetitiveAlarm(timeInMillis: Long) {

        setAlarm(
            timeInMillis,
            getPendingIntent(
                getIntent().apply {

                    action = Constant.ACTION_SET_REPETITIVE_ALARM
                    putExtra(Constant.EXTRA_EXACT_ALARM_TIME, timeInMillis)


                }
            )
        )

    }

    private fun setAlarm(timeInMillis: Long, pendingIntent: PendingIntent) {
        alarmManager?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent

                )
            } else {

                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            }
        }
    }

    private fun getIntent() = Intent(context, AlarmReceiver::class.java)

    private fun getPendingIntent(intent: Intent) =
        PendingIntent.getBroadcast(
            context,
            RandomIntUtil.getRandomInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
}