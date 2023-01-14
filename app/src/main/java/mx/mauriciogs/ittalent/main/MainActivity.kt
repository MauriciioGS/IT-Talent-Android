package mx.mauriciogs.ittalent.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ittalent.R
import com.example.ittalent.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ITTalent)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}