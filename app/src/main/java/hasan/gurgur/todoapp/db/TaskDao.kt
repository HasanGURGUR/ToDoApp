package hasan.gurgur.todoapp.db

import androidx.room.*
import hasan.gurgur.todoapp.util.Constant.NOTE_TABLE

@Dao
interface TaskDao {

    @Query("SELECT * FROM $NOTE_TABLE ORDER BY taskId DESC")
    fun getAllTasks(): MutableList<TaskEntity>

    @Query("SELECT * FROM $NOTE_TABLE WHERE taskId LIKE :id")
    fun getTask(id: Int): TaskEntity

    @Query("SELECT * FROM $NOTE_TABLE WHERE task_title LIKE '%' || :title || '%' OR task_desc LIKE '%' || :title || '%' LIKE :title")
    fun getAllTasksFromTitle(title : String): MutableList<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(taskEntity: TaskEntity)

    @Update
    fun updateTask(taskEntity: TaskEntity)

    @Delete
    fun deleteTask(taskEntity: TaskEntity)
}