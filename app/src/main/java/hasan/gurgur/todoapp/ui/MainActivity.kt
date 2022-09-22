package hasan.gurgur.todoapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import hasan.gurgur.todoapp.adapter.TaskAdapter
import hasan.gurgur.todoapp.databinding.ActivityMainBinding
import hasan.gurgur.todoapp.db.AppDatabse
import hasan.gurgur.todoapp.util.Constant.NOTE_DATABASE


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val taskDB: AppDatabse by lazy {
        Room.databaseBuilder(this, AppDatabse::class.java,NOTE_DATABASE)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
    private val taskAdapter by lazy { TaskAdapter() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddTask.setOnClickListener {
            startActivity(Intent(this,AddTaskActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        checkItem()
    }
    private fun checkItem() {
        binding.apply {

            if (taskDB.taskDao().getAllTasks().isNotEmpty()) {
                rvTaskList.visibility = View.VISIBLE
                tvEmptyText.visibility = View.GONE
                taskAdapter.differ.submitList(taskDB.taskDao().getAllTasks())
                setupRecyclerView()
            } else {
                rvTaskList.visibility = View.GONE
                tvEmptyText.visibility = View.VISIBLE


            }
        }
    }
    private fun setupRecyclerView() {

        binding.rvTaskList.apply {

            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter =taskAdapter
        }
    }
}