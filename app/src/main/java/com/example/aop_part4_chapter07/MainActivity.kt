package com.example.aop_part4_chapter07

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.aop_part4_chapter07.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}