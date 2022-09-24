package hasan.gurgur.todoapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import hasan.gurgur.todoapp.R
import hasan.gurgur.todoapp.adapter.TaskAdapter
import hasan.gurgur.todoapp.databinding.ActivityMainBinding
import hasan.gurgur.todoapp.db.AppDatabse
import hasan.gurgur.todoapp.util.Constant.NOTE_DATABASE


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var myToolbar: Toolbar

    private val taskDB: AppDatabse by lazy {
        Room.databaseBuilder(this, AppDatabse::class.java, NOTE_DATABASE)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
    private val taskAdapter by lazy { TaskAdapter() }

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myToolbar = findViewById(R.id.MyToolbar)
        setSupportActionBar(myToolbar)



        binding.btnAddTask.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id ==R.id.settings){
            Toast.makeText(this, "Selected: " +item.title, Toast.LENGTH_SHORT).show()
        }else if (id == R.id.search){
            Toast.makeText(this, "Selected: " +item.title, Toast.LENGTH_SHORT).show()
        }else if (id == R.id.edit){
            Toast.makeText(this, "Selected: " +item.title, Toast.LENGTH_SHORT).show()
        }
        return true
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
            adapter = taskAdapter
        }
    }
}