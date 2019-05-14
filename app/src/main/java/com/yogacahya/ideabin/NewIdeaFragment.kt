package com.yogacahya.ideabin

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.layout_new_idea.view.*
import java.util.*
import kotlin.collections.HashMap

class NewIdeaFragment : BottomSheetDialogFragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val v = View.inflate(context!!, R.layout.layout_new_idea, null)
        v.btnSave.setOnClickListener {
            if (v.etIdeaTitle.text.isNullOrBlank()) {
                v.etIdeaTitle.error = "Please fill this field"
                return@setOnClickListener
            }
            if (v.etIdeaDesc.text.isNullOrBlank()) {
                v.etIdeaDesc.error = "Please fill this field"
                return@setOnClickListener
            }
            v.btnSave.visibility = View.GONE
            v.rotateloading.visibility = View.VISIBLE
            val map = HashMap<String, Any>()
            map["name"] = v.etIdeaTitle.text.toString()
            map["desc"] = v.etIdeaDesc.text.toString()
            map["author"] = FirebaseAuth.getInstance().currentUser!!.uid
            map["authorname"] = FirebaseAuth.getInstance().currentUser!!.displayName!!
            map["date"] = Calendar.getInstance().time
            firestore.collection("idea").add(map)
                .addOnSuccessListener {
                    Toasty.success(context!!, "Success").show()
                    dialog.dismiss()
                }
                .addOnFailureListener {
                    Toasty.error(context!!, it.message!!).show()
                    v.btnSave.visibility = View.VISIBLE
                    v.rotateloading.visibility = View.GONE
                }
        }
        dialog.setContentView(v)
    }
}