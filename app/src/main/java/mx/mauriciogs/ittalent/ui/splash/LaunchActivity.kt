package mx.mauriciogs.ittalent.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import mx.mauriciogs.ittalent.ui.init.InitActivity
import mx.mauriciogs.ittalent.ui.main.MainActivity

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            delay(1200)
            startActivity(Intent(this@LaunchActivity, MainActivity::class.java)).apply {
                this@LaunchActivity.finish()
            }
        }
    }

}
