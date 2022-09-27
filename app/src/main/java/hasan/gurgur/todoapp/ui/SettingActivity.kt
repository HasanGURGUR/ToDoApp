package hasan.gurgur.todoapp.ui

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import hasan.gurgur.todoapp.R
import hasan.gurgur.todoapp.databinding.ActivitySettingBinding

private lateinit var binding: ActivitySettingBinding



class SettingActivity : AppCompatActivity() {
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}