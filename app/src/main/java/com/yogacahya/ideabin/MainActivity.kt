package com.yogacahya.ideabin

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.ly_profile.view.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var listener: ListenerRegistration? = null
    private lateinit var firestore: FirebaseFirestore
    private var alreadyInit = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initComponent()
    }

    private fun initComponent() {
        auth = FirebaseAuth.getInstance()

        Glide.with(this).load(auth.currentUser?.photoUrl).into(iv_profile)

        btnNotif.setOnClickListener {
            btnNotif.visibility = View.GONE
            bottom_nav.selectedItemId = R.id.homeFragment
        }
        fab_add.setOnClickListener {
            val bottom = NewIdeaFragment()
            bottom.show(supportFragmentManager, bottom.tag)
        }
        bottom_nav.let {
            NavigationUI.setupWithNavController(it, (fragment as NavHostFragment).navController)
        }
        iv_profile.setOnClickListener {
            val v = View.inflate(this, R.layout.ly_profile, null)
            Glide.with(this).load(auth.currentUser?.photoUrl).into(v.iv_profile)
            v.tv_nama.text = auth.currentUser?.displayName
            v.btnLogout.setOnClickListener {
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            val alert = Dialog(this)
            alert.setContentView(v)
            alert.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alert.show()
        }
        firestore = FirebaseFirestore.getInstance()
    }

    fun generateListener() {
        if (listener == null) {
            listener = firestore.collection("idea").addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                for (dc in snapshot!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        if (alreadyInit) {
                            btnNotif.visibility = View.VISIBLE
                        }
                    }
                }
                alreadyInit = true
            }

        }

    }
}
