package hasan.gurgur.todoapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.android.material.bottomsheet.BottomSheetDialog
import hasan.gurgur.todoapp.R
import hasan.gurgur.todoapp.adapter.TaskAdapter
import hasan.gurgur.todoapp.databinding.ActivityMainBinding
import hasan.gurgur.todoapp.db.AppDatabse
import hasan.gurgur.todoapp.db.TaskEntity
import hasan.gurgur.todoapp.util.Constant
import hasan.gurgur.todoapp.util.Constant.NOTE_DATABASE


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var myToolbar: Toolbar
    lateinit var appSettingPrefs: SharedPreferences
    lateinit var sharedPrefsEdit: SharedPreferences.Editor
    var isNightModeOn: Boolean = false
    private var list = arrayListOf<TaskEntity>()

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
        appSettingPrefs = getSharedPreferences("AppSettingPrefs", 0)
        sharedPrefsEdit = appSettingPrefs.edit()
        isNightModeOn = appSettingPrefs.getBoolean("NightMode", false)
        if (appSettingPrefs.getBoolean("NightMode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.darkMode) {
            darkModeOnOff()
        } else if (id == R.id.search) {
            Toast.makeText(this, "Selected: " + item.title, Toast.LENGTH_SHORT).show()
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
                list = taskDB.taskDao().getAllTasks() as ArrayList<TaskEntity>
                taskAdapter.differ.submitList(list)
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

    private fun darkModeOnOff() {


        if (isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            sharedPrefsEdit.putBoolean("NightMode", false)
            sharedPrefsEdit.apply()
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            sharedPrefsEdit.putBoolean("NightMode", true)
            sharedPrefsEdit.apply()
        }

        if (appSettingPrefs.getBoolean("NightMode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }


}