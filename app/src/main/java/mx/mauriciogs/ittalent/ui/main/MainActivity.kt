package mx.mauriciogs.ittalent.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ittalent.R
import com.example.ittalent.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mx.mauriciogs.ittalent.ui.connectivity.ConnectivityObserver
import mx.mauriciogs.ittalent.ui.connectivity.NetworkConnecivityObserver
import mx.mauriciogs.ittalent.ui.global.extensions.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var connectivityObserver: ConnectivityObserver

    private var isConnected = Boolean.no()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ITTalent)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        /*connectivityObserver = NetworkConnecivityObserver(applicationContext)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                connectivityObserver.observe().collectLatest {
                    when(it) {
                        ConnectivityObserver.Status.Available -> { isConnected = Boolean.yes() }
                        ConnectivityObserver.Status.Losing -> { snackbar("Se está perdiendo la conexión a internet...").showInfo() }
                        ConnectivityObserver.Status.Lost -> { isConnected = Boolean.no() }
                        ConnectivityObserver.Status.Unavailable -> { snackbar("Internet no disponible").showError() }
                    }
                }
            }
        }*/
    }
}