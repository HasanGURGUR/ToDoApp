package hasan.gurgur.todoapp.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hasan.gurgur.todoapp.util.Constant.NOTE_TABLE



@Entity(tableName = NOTE_TABLE)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val taskId: Int,
    @ColumnInfo(name = "task_title") val taskTitle: String,
    @ColumnInfo(name = "task_desc") val taskDesc: String,
    @ColumnInfo(name = "task_priority") val taskPriority: Int,
    @ColumnInfo(name = "task_date") val taskDate: String,
    @ColumnInfo(name = "task_time") val taskTime: String
)
