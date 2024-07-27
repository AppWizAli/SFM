package com.hiskytech.selfmademarket.Ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.databinding.ActivityMainBinding
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavHost()
        setUpBottomNavigation()
    }

    private fun setUpBottomNavigation() {
        val bottomNavigationItems = mutableListOf(
            CurvedBottomNavigation.Model(R.id.fragmentCommunity, "Community", R.drawable.community_icon),
            CurvedBottomNavigation.Model(R.id.fragmentCourses, "Courses", R.drawable.courses_icon),
            CurvedBottomNavigation.Model(R.id.fragmentPublishStory, "Publish story", R.drawable.plus_icon),
            CurvedBottomNavigation.Model(R.id.fragmentCrypto, "Crypto", R.drawable.bitcoin_icon),
            CurvedBottomNavigation.Model(R.id.fragmentForex, "Forex", R.drawable.forex_signal_icon)
        )
        binding.bottomNavigation.apply {
            bottomNavigationItems.forEach { add(it) }
            setOnClickMenuListener {
                navController.navigate(it.id)
            }
            setupNavController(navController)
        }
    }

    private fun initNavHost() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onBackPressed() {
        when (navController.currentDestination?.id) {
            R.id.fragmentCommunity -> super.onBackPressed()
            R.id.fragmentCourses -> navController.popBackStack(R.id.fragmentCommunity, false)
            R.id.fragmentPublishStory -> navController.popBackStack(R.id.fragmentCourses, false)
            R.id.fragmentCrypto -> navController.popBackStack(R.id.fragmentPublishStory, false)
            R.id.fragmentForex -> navController.popBackStack(R.id.fragmentCrypto, false)
            else -> navController.navigateUp()
        }
    }
}
