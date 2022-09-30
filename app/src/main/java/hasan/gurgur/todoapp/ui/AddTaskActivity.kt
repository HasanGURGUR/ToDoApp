package hasan.gurgur.todoapp.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import coil.load
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import hasan.gurgur.todoapp.R
import hasan.gurgur.todoapp.databinding.ActivityAddTaskBinding
import hasan.gurgur.todoapp.db.AppDatabse
import hasan.gurgur.todoapp.db.TaskEntity
import hasan.gurgur.todoapp.extension.showDialog
import hasan.gurgur.todoapp.util.AlarmService
import hasan.gurgur.todoapp.util.Constant.NOTE_DATABASE
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    var formatDate = SimpleDateFormat("dd MMMM YYYY", Locale.US)
    var date: String? = null
    var times: String? = null
    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2
    lateinit var alarmService: AlarmService
    var photo: ByteArray? = null

    private val taskDB: AppDatabse by lazy {
        Room.databaseBuilder(this, AppDatabse::class.java, NOTE_DATABASE)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
    private lateinit var taskEntity: TaskEntity

    @SuppressLint("ResourceAsColor", "SuspiciousIndentation")
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


                if (title.isNotEmpty() && desc.isNotEmpty()) {

                    taskEntity =
                        TaskEntity(0, title, desc, priority, date ?: "", times ?: "", photo)
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

            }


            binding.selectedTaskPhoto.setOnClickListener {
              showDialog(camera = {
                  cameraCheckPermission()
              }, gallery = {
                  galleryCheckPermission()
              })
            }
            binding.btnAddPhoto.setOnClickListener {
                showDialog(camera = {
                    cameraCheckPermission()
                }, gallery = {
                    galleryCheckPermission()
                })
            }
        }
    }

   fun galleryCheckPermission() {
        Dexter.withContext(this).withPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                gallery()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(
                    this@AddTaskActivity, "You have denied the storage permission to selecet image",
                    Toast.LENGTH_SHORT
                ).show()
                showRorationalDialogForPermission()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?,
                p1: PermissionToken?
            ) {
                showRorationalDialogForPermission()
            }
        }).onSameThread().check()
    }

     fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

     fun cameraCheckPermission() {

        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            ).withListener(

                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if (report.areAllPermissionsGranted()) {
                                camera()
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        showRorationalDialogForPermission()
                    }

                }
            ).onSameThread().check()


    }

     fun camera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {


                CAMERA_REQUEST_CODE -> {
                    var bitmap = data?.extras?.get("data") as Bitmap
                    binding.selectedTaskPhoto.load(bitmap)

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
                    binding.selectedTaskPhoto.load(data?.data)


                }
            }
        }
    }

    fun Context.getBitmap(uri: Uri): Bitmap =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.contentResolver, uri))
        else MediaStore.Images.Media.getBitmap(this.contentResolver, uri)

    fun showRorationalDialogForPermission() {

        AlertDialog.Builder(this).setMessage(
            "It looks like you have turned off permissions"
                    + "required for this feature. It can be enable under App Settings!!"
        )
            .setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)


                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }.setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }.show()
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
                    date = "$day/$month/$year"


                    TimePickerDialog(
                        this@AddTaskActivity, 0, { _, hour, min ->
                            this.set(Calendar.HOUR_OF_DAY, hour)
                            this.set(Calendar.MINUTE, min)
                            callback(this.timeInMillis)

                            times = "$hour:$min"

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