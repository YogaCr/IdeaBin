package com.yogacahya.ideabin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.ly_comment.view.*

class CommentAdapter(private val c: Context, private val ls: MutableList<CommentModel>) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(c).inflate(R.layout.ly_comment, parent, false), c)

    override fun getItemCount(): Int = ls.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ls[position])
    }

    class ViewHolder(val v: View, val c: Context) : RecyclerView.ViewHolder(v) {
        fun bind(m: CommentModel) {
            v.tvNama.text = m.name
            v.tvComment.text = m.comment
            v.tvDate.text = "At : " + m.date.toString()
            Glide.with(c).load(m.pic).into(v.iv_profile)
        }
    }
}