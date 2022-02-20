package com.clinicalaluz.clinicaapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import com.clinicalaluz.clinicaapp.databinding.ActivityServiciosBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


class ServiciosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServiciosBinding


    var drawerLayout: DrawerLayout? = null
    var navigationView: NavigationView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_servicios)

        binding = ActivityServiciosBinding.inflate(layoutInflater)
        setContentView(binding.root)




        //drawerLayout =  binding.drawerLayout
       //     navigationView = binding.navigationView
       //    drawerLayout!!.openDrawer(GravityCompat.END)
       // binding.drawerLayout.openDrawer(GravityCompat.END)

        binding.cardconsulta.setOnClickListener {
            val intent = Intent(this, SedesActivity::class.java)
            startActivity(intent)
        }

        binding.imageViewplaces5.setOnClickListener {
            salir()
        }


//        binding.cardpruepresion.setOnClickListener {
//            val intent = Intent(this, AutochequoActivity::class.java)
//            startActivity(intent)
//        }
    }

    private fun salir() {
        finish()
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        val inflater: MenuInflater = menuInflater
//        inflater.inflate(R.menu.menu_notificacion, menu)
//        return true
//    }
//
    fun showPopup(v: View) {
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_notificacion, popup.menu)
        popup.show()
    }







//    fun showMenu(v: View) {
//        PopupMenu(this, v).apply {
//            // MainActivity implements OnMenuItemClickListener
//            setOnMenuItemClickListener(this@MainActivity)
//            inflate(R.menu.actions)
//            show()
//        }
//    }
//
//    override fun onContextItemSelected(item: MenuItem): Boolean {
//        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
//        return when (item.itemId) {
//            R.id.navigation_home -> {
//
//                true
//            }
//            R.id.navigation_dashboard -> {
//               // deleteNote(info.id)
//                true
//            }
//            else -> super.onContextItemSelected(item)
//
//        }
//
//
//    }

}