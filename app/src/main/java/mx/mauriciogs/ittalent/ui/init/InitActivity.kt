package mx.mauriciogs.ittalent.ui.init

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.databinding.ActivityInitBinding
import mx.mauriciogs.ittalent.databinding.ActivityMainBinding

@AndroidEntryPoint
class InitActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ITTalent)
        binding = ActivityInitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
    }
}