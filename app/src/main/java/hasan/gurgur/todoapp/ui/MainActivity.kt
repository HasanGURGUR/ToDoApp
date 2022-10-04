package hasan.gurgur.todoapp.ui

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BlendModeColorFilter
import android.graphics.ColorFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.android.material.bottomsheet.BottomSheetDialog
import hasan.gurgur.todoapp.R
import hasan.gurgur.todoapp.adapter.TaskAdapter
import hasan.gurgur.todoapp.databinding.ActivityMainBinding
import hasan.gurgur.todoapp.db.AppDatabse
import hasan.gurgur.todoapp.db.TaskEntity
import hasan.gurgur.todoapp.util.Constant.BUNDLE_NOTE_ID
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
   lateinit var taskAdapter : TaskAdapter

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myToolbar = findViewById(R.id.MyToolbar)
        myToolbar.title = "Home"
        myToolbar.navigationIcon = null
        setSupportActionBar(myToolbar)

        binding.btnAddTask.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }
        appSettingPrefs = getSharedPreferences("AppSettingPrefs", 0)
        sharedPrefsEdit = appSettingPrefs.edit()

        if (appSettingPrefs.getBoolean("NightMode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        val searchItem: MenuItem? = menu?.findItem(R.id.search)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView: SearchView? = searchItem?.actionView as SearchView


        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val searchS = "$newText"
                checkItem(true,searchS)
                return true
            }

        })


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.darkMode) {
            darkModeOnOff()
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        checkItem(false)
    }

    private fun checkItem(isSearching : Boolean,searchString: String? = null) {
        binding.apply {

            if (taskDB.taskDao().getAllTasks().isNotEmpty()) {
                rvTaskList.visibility = View.VISIBLE
                tvEmptyText.visibility = View.GONE
               list =  if (isSearching){
                    if (!searchString.isNullOrEmpty()){
                        taskDB.taskDao().getAllTasksFromTitle(searchString.toString()) as ArrayList<TaskEntity>
                    }else{
                        taskDB.taskDao().getAllTasks() as ArrayList<TaskEntity>
                    }
                }else{
                    taskDB.taskDao().getAllTasks() as ArrayList<TaskEntity>
                }
                setupRecyclerView()
            } else {
                rvTaskList.visibility = View.GONE
                tvEmptyText.visibility = View.VISIBLE
            }
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(itemcallback = { task,position->
            openBottomSheet(task,position)
        })
        binding.rvTaskList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = taskAdapter
        }
        taskAdapter.submitList(list)
    }

    private fun darkModeOnOff() {
        if (appSettingPrefs.getBoolean("NightMode", false)) {
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

    private fun openBottomSheet(task : TaskEntity,position : Int){
        val bottomSheetDialog = BottomSheetDialog(
            this, R.style.BottomSheetDialogTheme
        )
        val bottomSheetView = LayoutInflater.from(this).inflate(
            R.layout.layout_bottom_sheet,
            binding.root.findViewById(R.id.bottomSheet) as LinearLayout?
        )
        bottomSheetView.findViewById<TextView>(R.id.bottomSheetDialogTitle).text = task.taskTitle
        bottomSheetView.findViewById<LinearLayout>(R.id.edit_layout).setOnClickListener {
            bottomSheetDialog.dismiss()
            val intent = Intent(this, UpdateTaskActivity::class.java)
            intent.putExtra(BUNDLE_NOTE_ID, task.taskId)
            this.startActivity(intent)
        }

        bottomSheetView.findViewById<LinearLayout>(R.id.delete_layout).setOnClickListener {
            taskDB.taskDao().deleteTask(task)
            checkItem(false)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }
}