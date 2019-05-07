package com.yogacahya.ideabin

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rv_home.view.*

class IdeaAdapter(private val c: Context, private val ls: MutableList<IdeaModel>) :
    RecyclerView.Adapter<IdeaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(c).inflate(R.layout.rv_home, parent, false))

    override fun getItemCount(): Int = ls.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ls[position], position)
    }

    class ViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {

        private val warna: MutableList<Int> = mutableListOf()

        init {
            warna.add(Color.parseColor("#E53935"))
            warna.add(Color.parseColor("#039BE5"))
            warna.add(Color.parseColor("#43A047"))
        }

        fun bind(m: IdeaModel, x: Int) {

            v.tv_judul.text = m.judul
            v.tv_like.text = m.like.toString()
            if (x % 3 == 0) {
                v.cv_idea.setCardBackgroundColor(warna[0])
            } else if (x % 3 == 1) {
                v.cv_idea.setCardBackgroundColor(warna[1])
            } else {
                v.cv_idea.setCardBackgroundColor(warna[2])
            }
        }
    }
}