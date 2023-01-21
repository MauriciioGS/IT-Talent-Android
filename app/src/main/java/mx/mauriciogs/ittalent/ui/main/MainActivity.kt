package mx.mauriciogs.ittalent.ui.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.databinding.ActivityMainBinding

private const val APPBARKEY = "appbar"

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var bottomNav: BottomNavigationView
    private val navHost : NavHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.navHostFrag) as NavHostFragment }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ITTalent)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        initUI()
    }

    private fun initUI() {
        navController = navHost.navController
        bottomNav = findViewById(R.id.bottomNav)

        binding.bottomNav.itemIconTintList = null
        binding.bottomNav.itemActiveIndicatorColor = null
        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _: NavController?, _: NavDestination?, arguments: Bundle? ->
            var showAppBar = false
            if (arguments != null) {
                showAppBar = arguments.getBoolean(APPBARKEY, false)
                Log.d("BottonNav", "$showAppBar")
            }

            bottomNav.isVisible = showAppBar
        }

        binding.ivBtnToolbar.setOnClickListener {

        }
    }
}