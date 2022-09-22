package hasan.gurgur.todoapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hasan.gurgur.todoapp.R
import hasan.gurgur.todoapp.databinding.ItemTaskBinding
import hasan.gurgur.todoapp.db.TaskEntity



class TaskAdapter : RecyclerView.Adapter<TaskAdapter.ViewHolder>(){
    private lateinit var binding:ItemTaskBinding
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
                tvDesc.text= item.taskDesc
              when(item.taskPriority){
                  1->{
                      itemCvBg.setBackgroundColor(ContextCompat.getColor(context,R.color.low))
                  }
                  2->{
                      itemCvBg.setBackgroundColor(ContextCompat.getColor(context,R.color.medium))
                  }
                  3->{
                      itemCvBg.setBackgroundColor(ContextCompat.getColor(context,R.color.high))
                  }
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