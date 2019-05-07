package com.yogacahya.ideabin


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.*

class YourideaFragment : Fragment() {
    private lateinit var firestore: FirebaseFirestore
    private val ls: MutableList<IdeaModel> = mutableListOf()
    private lateinit var adapter: IdeaAdapter
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_youridea, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = IdeaAdapter(context!!, ls)
        rvIdea.adapter = adapter
        rvIdea.layoutManager = LinearLayoutManager(context!!)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        swipe_rv.setOnRefreshListener {
            getData()
        }
        getData()
    }

    private fun getData() {
        swipe_rv.isRefreshing = true
        firestore.collection("idea").whereEqualTo("author", auth.currentUser?.uid).get().addOnSuccessListener {
            ls.clear()
            if (rvIdea != null) {
                rvIdea.adapter?.notifyDataSetChanged()
                for (d in it) {
                    val id = d.id
                    val judul = d.data["name"].toString()
                    firestore.collection("idea").document(id).collection("like").get().addOnSuccessListener { res ->
                        ls.add(IdeaModel(id, judul, res.size()))
                        if (rvIdea != null) {
                            rvIdea.adapter?.notifyDataSetChanged()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context!!, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
                swipe_rv.isRefreshing = false
            }
        }.addOnFailureListener {
            Toast.makeText(context!!, it.message, Toast.LENGTH_SHORT).show()
            swipe_rv.isRefreshing = false
        }
    }

    override fun onPause() {
        super.onPause()
        swipe_rv.isRefreshing = false
    }
}
