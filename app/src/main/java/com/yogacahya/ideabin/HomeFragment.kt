package com.yogacahya.ideabin


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private lateinit var firestore: FirebaseFirestore
    private val ls: MutableList<IdeaModel> = mutableListOf()
    private lateinit var adapter: IdeaAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = IdeaAdapter(context!!, ls)
        rvIdea.adapter = adapter
        rvIdea.layoutManager = LinearLayoutManager(context!!)
        firestore = FirebaseFirestore.getInstance()
        swipe_rv.setOnRefreshListener {
            if ((activity as MainActivity).et_search.text.isNullOrBlank()) {
                getData()
            } else {
                getDataSearch((activity as MainActivity).et_search.text.toString())
            }
        }
        getData()

        (activity as MainActivity).et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (swipe_rv != null) {
                    if (s.isNullOrBlank()) {
                        getData()
                    } else {
                        getDataSearch(s.toString())
                    }
                }
            }
        })
    }

    fun getData() {
        swipe_rv.isRefreshing = true
        firestore.collection("idea").orderBy("date", Query.Direction.DESCENDING).get().addOnSuccessListener {
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
                        Toasty.error(context!!, it.message!!).show()
                    }
                }
                swipe_rv.isRefreshing = false
            }

        }.addOnFailureListener {
            Toasty.error(context!!, it.message!!).show()
            swipe_rv.isRefreshing = false
        }
        (activity as MainActivity).generateListener()
    }

    fun getDataSearch(q: String) {
        swipe_rv.isRefreshing = true
        firestore.collection("idea").orderBy("date", Query.Direction.DESCENDING).get().addOnSuccessListener {
            ls.clear()
            if (rvIdea != null) {
                rvIdea.adapter?.notifyDataSetChanged()
                for (d in it) {
                    val id = d.id
                    val judul = d.data["name"].toString()
                    if (judul.contains(q)) {
                        firestore.collection("idea").document(id).collection("like").get().addOnSuccessListener { res ->
                            ls.add(IdeaModel(id, judul, res.size()))
                            if (rvIdea != null) {
                                rvIdea.adapter?.notifyDataSetChanged()
                            }
                        }.addOnFailureListener {
                            Toasty.error(context!!, it.message!!).show()
                        }
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
