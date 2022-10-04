package hasan.gurgur.todoapp.adapter

import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import hasan.gurgur.todoapp.R

object BindingAdapter {

    @JvmStatic
    @BindingAdapter("loadImage")
    fun loadImageFromUrl(imageview: ImageView, item : ByteArray?) {
        val decodedImage = item?.let {
            BitmapFactory.decodeByteArray(
                item,
                0,
                it.size
            )
        }
        imageview.setImageBitmap(decodedImage)
    }

    @JvmStatic
    @BindingAdapter("setPriorityColor")
    fun setPriorityColor(view: ConstraintLayout, priority: Int ) {
        when (priority) {
            1 -> {
                view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.low))
            }
            2 -> {
                view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.medium))
            }
            3 -> {
                view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.high))
            }
            else -> {
                view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.default_priority_color))
            }
        }
    }







}