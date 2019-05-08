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
            val map = HashMap<String, String>()
            map["name"] = v.etIdeaTitle.text.toString()
            map["description"] = v.etIdeaDesc.text.toString()
            map["author"] = FirebaseAuth.getInstance().currentUser?.uid!!
            firestore.collection("idea").add(map)
                .addOnSuccessListener {
                    dialog.dismiss()
                    Toasty.success(context!!, "Success").show()
                }
                .addOnFailureListener {
                    Toasty.error(context!!, it.message!!).show()
                }
        }
        dialog.setContentView(v)
    }
}