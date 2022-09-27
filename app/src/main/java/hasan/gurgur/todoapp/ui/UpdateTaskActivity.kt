package hasan.gurgur.todoapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import hasan.gurgur.todoapp.R
import hasan.gurgur.todoapp.databinding.ActivityUpdateTaskBinding
import hasan.gurgur.todoapp.db.AppDatabse
import hasan.gurgur.todoapp.db.TaskEntity
import hasan.gurgur.todoapp.util.Constant
import hasan.gurgur.todoapp.util.Constant.BUNDLE_NOTE_ID

class UpdateTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateTaskBinding
    var date: String? = null
    var times: String? = null
    private val taskDB: AppDatabse by lazy {
        Room.databaseBuilder(this, AppDatabse::class.java, Constant.NOTE_DATABASE)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
    private lateinit var taskEntity: TaskEntity

    private var taskOfId = 0
    private var defaultTitle = ""
    private var defaultDesc = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.extras?.let {
            taskOfId = it.getInt(BUNDLE_NOTE_ID)
        }


        binding.apply {
            defaultTitle = taskDB.taskDao().getTask(taskOfId).taskTitle
            defaultDesc = taskDB.taskDao().getTask(taskOfId).taskDesc

            edtTitle.setText(defaultTitle)
            edtDesc.setText(defaultDesc)


            when (taskDB.taskDao().getTask(taskOfId).taskPriority) {
                1 -> {
                    rbLow.isChecked = true
                }
                2 -> {
                    rbMedium.isChecked = true
                }
                3 -> {
                    rbHigh.isChecked = true
                }
            }



            btnDelete.setOnClickListener {
                var priority = 0

                if (rbLow.isChecked) priority = 1
                if (rbMedium.isChecked) priority = 2
                if (rbHigh.isChecked) priority = 3
                taskEntity = TaskEntity(
                    taskOfId,
                    defaultTitle,
                    defaultDesc,
                    priority,
                    date ?: "",
                    times ?: ""
                )
                taskDB.taskDao().deleteTask(taskEntity)
                finish()

            }

            btnSave.setOnClickListener {
                val title = edtTitle.text.toString()
                val desc = edtDesc.text.toString()
                var priority = 0

                if (rbLow.isChecked) priority = 1
                if (rbMedium.isChecked) priority = 2
                if (rbHigh.isChecked) priority = 3


                if (title.isNotEmpty() || desc.isNotEmpty()) {

                    taskEntity =
                        TaskEntity(taskOfId, title, desc, priority, date ?: "", times ?: "")
                    taskDB.taskDao().updateTask(taskEntity)
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