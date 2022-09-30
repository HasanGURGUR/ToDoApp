package hasan.gurgur.todoapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialog
import hasan.gurgur.todoapp.R
import hasan.gurgur.todoapp.databinding.ItemTaskBinding
import hasan.gurgur.todoapp.db.TaskEntity
import hasan.gurgur.todoapp.ui.UpdateTaskActivity
import hasan.gurgur.todoapp.util.Constant.BUNDLE_NOTE_ID


class TaskAdapter : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    private lateinit var binding: ItemTaskBinding
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemTaskBinding.inflate(inflater, parent, false)
        context = parent.context
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: TaskAdapter.ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n", "ResourceAsColor")
        fun bind(item: TaskEntity) {
            //InitView
            binding.apply {
                //Set text
                tvTitle.text = item.taskTitle
                tvDesc.text = item.taskDesc
                tvDate.text = item.taskDate
                tvTime.text = item.taskTime


                val decodedImage =
                    item.taskPhoto?.let {



                        BitmapFactory.decodeByteArray(
                            item.taskPhoto,
                            0,
                            it.size
                        )
                    }
                taskPhoto.setImageBitmap(decodedImage)



                when (item.taskPriority) {
                    1 -> {
                        itemCvBg.setBackgroundColor(ContextCompat.getColor(context, R.color.low))
                    }
                    2 -> {
                        itemCvBg.setBackgroundColor(ContextCompat.getColor(context, R.color.medium))
                    }
                    3 -> {
                        itemCvBg.setBackgroundColor(ContextCompat.getColor(context, R.color.high))
                    }
                }

                root.setOnClickListener {


                    val bottomSheetDialog = BottomSheetDialog(
                        context, R.style.BottomSheetDialogTheme
                    )
                    val bottomSheetView = LayoutInflater.from(context).inflate(
                        R.layout.layout_bottom_sheet,
                        binding.root.findViewById(R.id.bottomSheet) as LinearLayout?
                    )
                    bottomSheetView.findViewById<TextView>(R.id.bottomSheetDialogTitle).text =
                        item.taskTitle
                    bottomSheetView.findViewById<View>(R.id.btn_edit).setOnClickListener {

                        bottomSheetDialog.dismiss()
                        val intent = Intent(context, UpdateTaskActivity::class.java)
                        intent.putExtra(BUNDLE_NOTE_ID, item.taskId)
                        context.startActivity(intent)
                    }

                    bottomSheetView.findViewById<View>(R.id.btn_delete).setOnClickListener {
                        Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show()

                    }
                    bottomSheetDialog.setContentView(bottomSheetView)
                    bottomSheetDialog.show()


                }

                if (!tvDate.text.isNullOrEmpty()) {
                    tvDate.visibility = View.VISIBLE
                    tvTime.visibility = View.VISIBLE
                }

                if (item.taskPhoto?.isNotEmpty() == true){
                    cvTaskPhoto.visibility = View.VISIBLE
                }
            }
        }
  }

    private val differCallback = object : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem.taskId == newItem.taskId
        }

        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}