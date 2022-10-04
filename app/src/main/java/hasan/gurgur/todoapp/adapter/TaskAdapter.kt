package hasan.gurgur.todoapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hasan.gurgur.todoapp.db.TaskEntity


class TaskAdapter(val itemcallback : ((TaskEntity,Int) -> Unit)? = null) : BaseListAdapter<TaskEntity>(
    itemsSame = { old, new -> old.taskId == new.taskId },
    contentsSame = { old, new -> old == new }
) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TaskViewHolder -> {
                holder.bind(
                    task = getItem(position),
                    characterClickCallback = itemcallback
                )
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        inflater: LayoutInflater,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return TaskViewHolder(parent, inflater)
    }


}