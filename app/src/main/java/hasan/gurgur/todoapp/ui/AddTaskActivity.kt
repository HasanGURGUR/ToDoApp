package hasan.gurgur.todoapp.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import hasan.gurgur.todoapp.R

import hasan.gurgur.todoapp.databinding.ActivityAddTaskBinding
import hasan.gurgur.todoapp.db.AppDatabse
import hasan.gurgur.todoapp.db.TaskEntity
import hasan.gurgur.todoapp.util.AlarmService
import hasan.gurgur.todoapp.util.Constant.NOTE_DATABASE

import java.text.SimpleDateFormat
import java.util.*


class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    var formatDate = SimpleDateFormat("dd MMMM YYYY", Locale.US)
    var date: String? = null
    var time: String? = null
    lateinit var alarmService: AlarmService

    private val taskDB: AppDatabse by lazy {
        Room.databaseBuilder(this, AppDatabse::class.java, NOTE_DATABASE)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
    private lateinit var taskEntity: TaskEntity

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        alarmService = AlarmService(this)
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnSave.setOnClickListener {
                val title = binding.etTitle.text.toString()
                val desc = binding.etDesc.text.toString()
                var priority = 0

                if (rbLow.isChecked) priority = 1
                if (rbMedium.isChecked) priority = 2
                if (rbHigh.isChecked) priority = 3


                if (title.isNotEmpty() || desc.isNotEmpty()) {

                    taskEntity = TaskEntity(0, title, desc, priority, date ?: "", time ?: "")
                    taskDB.taskDao().insertTask(taskEntity)
                    finish()
                } else {
                    Snackbar.make(
                        it,
                        "Title and Describition cannot be Empty",
                        Snackbar.LENGTH_LONG
                    ).show()
                }


            }

            datePickerBtn.setOnClickListener {
                setAlarm { timeInMillis -> alarmService.setExactAlarm(timeInMillis) }
                /* val getDate = Calendar.getInstance()
                 val datepicker = DatePickerDialog(
                     this@AddTaskActivity, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                     DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->

                         val selectDate = Calendar.getInstance()
                         selectDate.set(Calendar.YEAR, i)
                         selectDate.set(Calendar.MONTH, i2)
                         selectDate.set(Calendar.DAY_OF_MONTH, i3)
                         date = formatDate.format(selectDate.time)


                     },
                     getDate.get(Calendar.YEAR),
                     getDate.get(Calendar.MONTH),
                     getDate.get(Calendar.DAY_OF_MONTH)
                 )
                 datepicker.show()*/

            }

            timePickerBtn.setOnClickListener {
                setAlarm {
                    alarmService.setRepetitiveAlarm(it)
                }
            }


        }


    }

    private fun setAlarm(callback: (Long) -> Unit) {

        Calendar.getInstance().apply {
            this.set(Calendar.SECOND, 0)
            this.set(Calendar.MILLISECOND, 0)
            DatePickerDialog(
                this@AddTaskActivity, 0,
                { _, year, month, day ->

                    this.set(Calendar.YEAR, year)
                    this.set(Calendar.MONTH, month)
                    this.set(Calendar.DAY_OF_MONTH, day)

                    TimePickerDialog(
                        this@AddTaskActivity, 0, { _, hour, min ->
                            this.set(Calendar.HOUR_OF_DAY, hour)
                            this.set(Calendar.MINUTE, min)
                            callback(this.timeInMillis)

                        },
                        this.get(Calendar.HOUR_OF_DAY),
                        this.get(Calendar.MINUTE),
                        false
                    ).show()

                },
                this.get(Calendar.YEAR),
                this.get(Calendar.MONTH),
                this.get(Calendar.DAY_OF_MONTH),
            ).show()
        }

    }
}