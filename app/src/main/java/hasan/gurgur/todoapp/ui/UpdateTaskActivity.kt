package hasan.gurgur.todoapp.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.room.Room
import coil.load
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import hasan.gurgur.todoapp.R
import hasan.gurgur.todoapp.databinding.ActivityUpdateTaskBinding
import hasan.gurgur.todoapp.db.AppDatabse
import hasan.gurgur.todoapp.db.TaskEntity
import hasan.gurgur.todoapp.extension.*
import hasan.gurgur.todoapp.util.Constant
import hasan.gurgur.todoapp.util.Constant.BUNDLE_NOTE_ID
import java.io.ByteArrayOutputStream

class UpdateTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateTaskBinding
    var date: String? = null
    var times: String? = null
    var photo: ByteArray? = null
    private lateinit var myToolbar: androidx.appcompat.widget.Toolbar
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

        myToolbar = findViewById(R.id.MyToolbar)
        myToolbar.title = "Edit"
        setSupportActionBar(myToolbar)

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
                showDialog(gallery = {
                    galleryCheckPermission()
                },camera = {
                    cameraCheckPermission()
                })
            }



            btnDelete.setOnClickListener {
                taskDB.taskDao().deleteTask(taskDB.taskDao().getTask(taskOfId))
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navigationIcon = myToolbar.navigationIcon
        navigationIcon.apply {
            finish()
        }
        return true
    }

    fun Context.getBitmap(uri: Uri): Bitmap =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(
                this.contentResolver,
                uri
            )
        )
        else MediaStore.Images.Media.getBitmap(this.contentResolver, uri)


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {


                CAMERA_REQUEST_CODE -> {
                    var bitmap = data?.extras?.get("data") as Bitmap
                    binding.updateAddPhoto.load(bitmap)

                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
                    val image = stream.toByteArray()
                    photo = image
                }

                GALLERY_REQUEST_CODE -> {
                    val bitmap = this.getBitmap(data?.data!!)

                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
                    val image = stream.toByteArray()
                    photo = image
                    binding.updateAddPhoto.load(data?.data)


                }
            }
        }
    }
}