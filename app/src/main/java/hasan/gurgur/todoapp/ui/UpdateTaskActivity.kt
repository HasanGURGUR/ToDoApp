package hasan.gurgur.todoapp.ui

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import hasan.gurgur.todoapp.R
import hasan.gurgur.todoapp.databinding.ActivityUpdateTaskBinding
import hasan.gurgur.todoapp.db.AppDatabse
import hasan.gurgur.todoapp.db.TaskEntity
import hasan.gurgur.todoapp.extension.cameraCheckPermission
import hasan.gurgur.todoapp.extension.galleryCheckPermission
import hasan.gurgur.todoapp.extension.showDialog
import hasan.gurgur.todoapp.util.Constant
import hasan.gurgur.todoapp.util.Constant.BUNDLE_NOTE_ID

class UpdateTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateTaskBinding
    var date: String? = null
    var times: String? = null
    var photo: ByteArray? = null
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
            photo = taskDB.taskDao().getTask(taskOfId).taskPhoto

            edtTitle.setText(defaultTitle)
            edtDesc.setText(defaultDesc)


            val decodedImage =
                photo?.let {
                    BitmapFactory.decodeByteArray(
                        photo,
                        0,
                        it.size
                    )
                }
            updateTaskPhoto.setImageBitmap(decodedImage)

            if (photo == null) {
                updateCvTaskPhoto.visibility = View.GONE
            }


            if (photo == null) {
                updateAddPhoto.visibility = View.VISIBLE
            } else {
                updateAddPhoto.visibility = View.GONE
            }

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

            removePhotoBtn.setOnClickListener {
                photo = null
                updateCvTaskPhoto.visibility = View.GONE


                if (photo == null) {
                    updateAddPhoto.visibility = View.VISIBLE
                } else {
                    updateAddPhoto.visibility = View.GONE
                }

            }
            binding.updateAddPhoto.setOnClickListener {
                showDialog(camera = {
                    cameraCheckPermission()
                }, gallery = {
                    galleryCheckPermission()
                })
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
                    times ?: "",
                    photo
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
                        TaskEntity(taskOfId, title, desc, priority, date ?: "", times ?: "", photo)
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