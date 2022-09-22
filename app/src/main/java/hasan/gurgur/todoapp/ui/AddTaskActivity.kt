package hasan.gurgur.todoapp.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import hasan.gurgur.todoapp.R

import hasan.gurgur.todoapp.databinding.ActivityAddTaskBinding
import hasan.gurgur.todoapp.db.AppDatabse
import hasan.gurgur.todoapp.db.TaskEntity
import hasan.gurgur.todoapp.util.Constant.NOTE_DATABASE


class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding



    private val taskDB: AppDatabse by lazy {
        Room.databaseBuilder(this, AppDatabse::class.java, NOTE_DATABASE)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
    private lateinit var taskEntity: TaskEntity

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnSave.setOnClickListener {
                val title = binding.etTitle.text.toString()
                val desc = binding.etDesc.text.toString()
                var priority = 0

                if (rbLow.isChecked) priority =1
                if (rbMedium.isChecked) priority =2
                if (rbHigh.isChecked) priority =3


                if (title.isNotEmpty() || desc.isNotEmpty()) {

                    taskEntity = TaskEntity(0, title, desc, priority, "a", "a")
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
        }

    }
}