package com.yogacahya.ideabin

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    private lateinit var user: FirebaseUser
    private lateinit var firestore: FirebaseFirestore
    private var hijau: Int = 0
    private var like = false
    private var bookmark = false
    private var contentloaded = false
    private var likeloaded = false
    private var bookmarkloaded = false
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        hijau = Color.parseColor("#00ACC1")
        user = FirebaseAuth.getInstance().currentUser!!
        firestore = FirebaseFirestore.getInstance()
        val i = intent
        val doc = firestore.collection("idea").document(i.getStringExtra("id"))
        doc.get().addOnSuccessListener {
            countLike()
            setBookmark()
            tv_judul.text = it.getString("name")
            tv_desc.text = it.getString("desc")
            contentloaded = true
            loadedCheck()
        }

        btn_like.setOnClickListener {
            btn_like.isEnabled = false
            if (like) {
                doc.collection("like").document(user.uid).delete().addOnSuccessListener {
                    like = false
                    countLike()
                }
            } else {
                val data = HashMap<String, String>()
                doc.collection("like").document(user.uid).set(data).addOnSuccessListener {
                    like = true
                    countLike()
                }
            }
        }
        btn_bookmark.setOnClickListener {
            btn_bookmark.isEnabled = false
            if (bookmark) {
                doc.collection("bookmark").document(user.uid).delete().addOnSuccessListener {
                    bookmark = false
                    setBookmark()
                }
            } else {
                val data = HashMap<String, String>()
                doc.collection("bookmark").document(user.uid).set(data).addOnSuccessListener {
                    bookmark = true
                    setBookmark()
                }
            }
        }
    }

    @SuppressLint("NewApi")
    fun countLike() {
        val doc = firestore.collection("idea").document(intent.getStringExtra("id"))
        doc.collection("like").get()
            .addOnSuccessListener { q ->
                if (!q.isEmpty) {
                    doc.collection("like").document(user.uid).get().addOnSuccessListener {
                        like = if (it.exists()) {
                            btn_like.icon.setTint(hijau)
                            true
                        } else {
                            false
                        }
                    }
                }
                btn_like.isEnabled = true
                btn_like.text = q.size().toString()
                likeloaded = true

                loadedCheck()
            }
    }

    @SuppressLint("NewApi")
    fun setBookmark() {
        val doc = firestore.collection("idea").document(intent.getStringExtra("id"))
        doc.collection("bookmark").document(user.uid).get().addOnSuccessListener {
            btn_bookmark.isEnabled = true
            bookmark = if (it.exists()) {
                btn_bookmark.background.setTint(hijau)
                true
            } else {
                btn_bookmark.background.setTint(Color.parseColor("#FB8C00"))
                false
            }

            bookmarkloaded = true

            loadedCheck()

        }
    }

    private fun loadedCheck() {
        if (contentloaded && likeloaded && bookmarkloaded) {
            loading.visibility = View.GONE
            ly_content.visibility = View.VISIBLE
        }
    }
}
