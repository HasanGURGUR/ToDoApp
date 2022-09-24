package hasan.gurgur.todoapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.SyncStateContract
import android.provider.SyncStateContract.Constants
import android.text.format.DateFormat
import hasan.gurgur.todoapp.util.AlarmService
import hasan.gurgur.todoapp.util.Constant
import io.karn.notify.Notify
import java.util.Calendar
import java.util.concurrent.TimeUnit


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val timeInMillis = intent.getLongExtra(Constant.EXTRA_EXACT_ALARM_TIME,0L)
        when(intent.action){
            Constant.ACTION_SET_EXACT_ALARM -> {
                buildNotification(context,"Set Exact Time", convertDate(timeInMillis))

            }

            Constant.ACTION_SET_REPETITIVE_ALARM -> {

                val cal = Calendar.getInstance().apply {
                    this.timeInMillis = timeInMillis + TimeUnit.DAYS.toMillis(7)

                }
                AlarmService(context).setRepetitiveAlarm(cal.timeInMillis)
                buildNotification(context,"Set Repetitive Time", convertDate(timeInMillis))
            }
        }
    }


    private fun buildNotification(context: Context, title: String, message: String) {

        Notify
            .with(context)
            .content {
                this.title = title
                this.text = "I got triggered at - $message"
            }
            .show()
    }
    private fun convertDate(timeInMillis: Long): String =
        DateFormat.format("dd/MM/yyyy hh:mm:ss", timeInMillis).toString()

}