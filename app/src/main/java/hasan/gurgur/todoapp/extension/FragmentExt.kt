package hasan.gurgur.todoapp.extension

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener

fun Activity.showDialog(camera: () -> Unit, gallery: () -> Unit) {
    val pictureDialog = AlertDialog.Builder(this)
    pictureDialog.setTitle("SelectAction")
    val pictureDialogItem =
        arrayOf("Select photo from Gallery", "Capture photo from Camera")
    pictureDialog.setItems(pictureDialogItem) { dialog, which ->

        when (which) {
            0 -> gallery.invoke()
            1 -> camera.invoke()
        }
    }
    pictureDialog.show()
}

fun Activity.cameraCheckPermission() {
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

fun Activity.camera() {


    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    startActivityForResult(intent, CAMERA_REQUEST_CODE)

}

fun Activity.gallery() {
    val intent = Intent(Intent.ACTION_PICK)
    intent.type = "image/*"
    startActivityForResult(intent, GALLERY_REQUEST_CODE)
}
fun Activity.galleryCheckPermission() {
    Dexter.withContext(this).withPermission(
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    ).withListener(object : PermissionListener {
        override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
            gallery()
        }

        override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
            Toast.makeText(
                this@galleryCheckPermission, "You have denied the storage permission to selecet image",
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

fun Activity.showRorationalDialogForPermission() {

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

