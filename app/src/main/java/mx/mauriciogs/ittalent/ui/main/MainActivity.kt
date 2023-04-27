package mx.mauriciogs.ittalent.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorStateListDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
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
import mx.mauriciogs.ittalent.core.extensions.toast
import mx.mauriciogs.ittalent.databinding.ActivityMainBinding
import mx.mauriciogs.ittalent.ui.jobs.JobsViewModel

private const val APPBARKEY = "appbar"

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var bottomNav: BottomNavigationView
    private val navHost : NavHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.navHostFrag) as NavHostFragment }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("NOTI", "Podemos recibir")
        } else {
            toast("No aceptas recibir notificaciones")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_ITTalent)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        mainViewModel.checkToken()
        initObserver()
    }

    private fun initObserver() {
        mainViewModel.initUI.observe(this) {
            if (it) initUI()
            else toast("Ocurrió un problema guardando el token")
        }
        mainViewModel.error.observe(this) {
            if (it != null) it.localizedMessage?.let { it1 -> Log.e("NOTI", it1) }
        }
    }

    private fun initUI() {

        askNotificationPermission()

        navController = navHost.navController
        bottomNav = findViewById(R.id.bottomNav)

        binding.bottomNav.itemIconTintList = null
        binding.bottomNav.itemActiveIndicatorColor = null
        binding.bottomNav.itemTextColor = myColorList
        if(intent.getIntExtra("userType", 1) == Int.TALENT_UT()) {
            binding.bottomNav.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_SELECTED
            binding.bottomNav.menu.removeItem(R.id.talentFragment)
            binding.bottomNav.menu.removeItem(R.id.postulationsRecFragment)
        }else {
            binding.bottomNav.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_AUTO
            binding.bottomNav.menu.removeItem(R.id.postulationsTalFragment)
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

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                Log.d("NOTI", "Podemos recibir")
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
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