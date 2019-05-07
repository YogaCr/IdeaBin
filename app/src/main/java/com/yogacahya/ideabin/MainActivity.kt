package com.yogacahya.ideabin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        auth = FirebaseAuth.getInstance()
        Glide.with(this).load(auth.currentUser?.photoUrl).into(iv_profile)
        fab_add.setOnClickListener {
            val intent = Intent(this, NewIdeaActivity::class.java)
            startActivity(intent)
        }
        bottom_nav.let {
            NavigationUI.setupWithNavController(it, (fragment as NavHostFragment).navController)
        }
    }
}
