package com.clinicalaluz.clinicaapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import com.clinicalaluz.clinicaapp.databinding.ActivitySubMenuBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class SubMenuActivity : AppCompatActivity() {
    private var binding: ActivitySubMenuBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubMenuBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
        )
            .build()
        val navController =
            Navigation.findNavController(this, R.id.nav_host_fragment_activity_sub_menu)
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);

    }
}