package com.example.final_gram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.final_gram.databinding.ActivityHolderBinding

class HolderActivity : AppCompatActivity() {
    lateinit var binding:ActivityHolderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHolderBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}