package hasan.gurgur.todoapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import hasan.gurgur.todoapp.databinding.ItemTaskBinding
import hasan.gurgur.todoapp.db.TaskEntity

class TaskViewHolder (
    parent: ViewGroup,
    inflater: LayoutInflater
) : BaseViewHolder<ItemTaskBinding>(
    binding = ItemTaskBinding.inflate(inflater, parent, false)
) {

    fun bind(
        task : TaskEntity,
        characterClickCallback : ((TaskEntity,Int) -> Unit)? = null
    ) {
        with(binding) {
            binding.task = task
            binding.mainLay.setOnClickListener{
                characterClickCallback?.invoke(task,adapterPosition)
            }
            executePendingBindings()
        }
    }


}
