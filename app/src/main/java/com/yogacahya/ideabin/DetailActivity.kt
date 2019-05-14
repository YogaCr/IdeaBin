package com.yogacahya.ideabin

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_detail.*
import java.util.*
import kotlin.collections.HashMap

class DetailActivity : AppCompatActivity() {
    private lateinit var user: FirebaseUser
    private lateinit var firestore: FirebaseFirestore
    private var hijau: Int = 0
    private var like = false
    private var bookmark = false
    private var contentloaded = false
    private var likeloaded = false
    private var bookmarkloaded = false
    private val ls: MutableList<CommentModel> = mutableListOf()
    private lateinit var listenerRegistration: ListenerRegistration
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
        rv_comment.adapter = CommentAdapter(this, ls)
        rv_comment.layoutManager = LinearLayoutManager(this)
        doc.get().addOnSuccessListener {
            countLike()
            setBookmark()
            tv_judul.text = it.getString("name")
            tv_desc.text = it.getString("desc")
            tv_date.text = "At : " + it.getDate("date").toString()
            tv_name.text = "By : " + it.getString("authorname")
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
        Glide.with(this).load(user.photoUrl).into(iv_profile)
        btnSend.setOnClickListener {
            if (etNewComment.text.isNullOrBlank()) {
                etNewComment.error = "This field must be filled"
                return@setOnClickListener
            }
            btnSend.visibility = View.INVISIBLE
            rotateloading.visibility = View.VISIBLE
            val data = HashMap<String, Any>()
            data["name"] = user.displayName!!
            data["photo"] = user.photoUrl.toString()
            data["comment"] = etNewComment.text.toString()
            data["date"] = Calendar.getInstance().time
            doc.collection("comment").add(data).addOnCompleteListener {
                if (!it.isSuccessful) {
                    Toasty.error(this, "Error while sending your comment").show()
                }
                btnSend.visibility = View.VISIBLE
                rotateloading.visibility = View.INVISIBLE
            }
        }
        listenerRegistration = doc.collection("comment").addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toasty.error(this, exception.message!!).show()
                return@addSnapshotListener
            }

            for (d in snapshot!!.documentChanges) {
                if (d.type == DocumentChange.Type.ADDED) {
                    ls.add(
                        CommentModel(
                            d.document.getString("name"),
                            d.document.getString("photo"),
                            d.document.getString("comment"),
                            d.document.getDate("date")
                        )
                    )
                    tv_totalcomment.text = snapshot.size().toString() + " comments"
                    (rv_comment.adapter as CommentAdapter).notifyDataSetChanged()


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
                btn_bookmark.text = "Bookmarked"
                true
            } else {
                btn_bookmark.background.setTint(Color.parseColor("#FB8C00"))
                btn_bookmark.text = "Bookmark"
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

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }
}
