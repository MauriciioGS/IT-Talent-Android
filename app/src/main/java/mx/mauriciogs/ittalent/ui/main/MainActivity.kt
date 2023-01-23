package mx.mauriciogs.ittalent.ui.main

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorStateListDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint
import mx.mauriciogs.ittalent.R
import mx.mauriciogs.ittalent.core.extensions.TALENT_UT
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
        binding.bottomNav.itemTextColor = myColorList
        if(intent.getIntExtra("userType", 1) == Int.TALENT_UT()) {
            binding.bottomNav.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_SELECTED
            binding.bottomNav.menu.removeItem(R.id.talentFragment)
        }
        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _: NavController?, _: NavDestination?, arguments: Bundle? ->
            var showAppBar = false
            if (arguments != null) {
                showAppBar = arguments.getBoolean(APPBARKEY, false)
                Log.d("BottonNav", "$showAppBar")
            }

            bottomNav.isVisible = showAppBar
        }
    }

    companion object {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_enabled), // enabled
            intArrayOf(android.R.attr.state_enabled), // disabled
            intArrayOf(android.R.attr.state_checked), // unchecked
            intArrayOf(android.R.attr.state_pressed)  // pressed
        )

        val colors = intArrayOf(
            Color.WHITE,
            Color.WHITE,
            Color.GRAY,
            Color.WHITE
        )

        val myColorList = ColorStateList(states, colors)
    }
}