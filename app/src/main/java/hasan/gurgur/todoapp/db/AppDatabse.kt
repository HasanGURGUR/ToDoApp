package hasan.gurgur.todoapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TaskEntity::class], version =1)
abstract class AppDatabse : RoomDatabase() {
    abstract fun taskDao() : TaskDao

}