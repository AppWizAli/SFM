package com.hiskytech.selfmademarket.Ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hiskytech.selfmademarket.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }
}
