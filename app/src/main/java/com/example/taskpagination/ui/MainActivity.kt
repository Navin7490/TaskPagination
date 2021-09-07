package com.example.taskpagination.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.taskpagination.R
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    lateinit var toolbar: MaterialToolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar= findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
      val navHostFragment=  supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)
        navController.addOnDestinationChangedListener(navDestination)
    }

    private val navDestination= NavController.OnDestinationChangedListener { controller, destination, arguments ->

        when(destination.id){
            R.id.homeFragment->{
              statusBar()
            }
        }

    }

    private fun statusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.show(WindowInsets.Type.statusBars())

        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        }
    }
}