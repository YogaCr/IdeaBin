package com.yogacahya.ideabin


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_bookmark.*


class BookmarkFragment : Fragment() {
    private lateinit var user: FirebaseUser
    private lateinit var firestore: FirebaseFirestore
    private lateinit var col: CollectionReference
    private val ls: MutableList<IdeaModel> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        user = FirebaseAuth.getInstance().currentUser!!
        firestore = FirebaseFirestore.getInstance()
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvIdea.adapter = IdeaAdapter(context!!, ls)
        rvIdea.layoutManager = LinearLayoutManager(context!!)
        col = firestore.collection("idea")
        swipe_rv.setOnRefreshListener {
            getData()
        }
        getData()
    }

    private fun getData() {
        swipe_rv.isRefreshing = true
        firestore.collection("idea").whereEqualTo("author", user.uid).get().addOnSuccessListener {
            ls.clear()
            if (rvIdea != null) {
                rvIdea.adapter?.notifyDataSetChanged()
                for (d in it) {
                    val id = d.id
                    val judul = d.data["name"].toString()
                    firestore.collection("idea").document(id).collection("bookmark").get().addOnSuccessListener { res ->
                        ls.add(IdeaModel(id, judul, res.size()))
                        if (rvIdea != null) {
                            rvIdea.adapter?.notifyDataSetChanged()
                        }
                    }.addOnFailureListener {
                        Toasty.error(context!!, it.message!!).show()
                    }
                }
                swipe_rv.isRefreshing = false
            }
        }.addOnFailureListener {
            Toasty.error(context!!, it.message!!).show()
            swipe_rv.isRefreshing = false
        }
    }

    override fun onPause() {
        super.onPause()
        swipe_rv.isRefreshing = false
    }
}
