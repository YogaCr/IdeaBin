package com.yogacahya.ideabin

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_new_idea.*

class NewIdeaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_idea)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        editorToolbar.editor = editor
        editor.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                editorToolbar.visibility = View.VISIBLE
            } else {
                editorToolbar.visibility = View.INVISIBLE
            }
        }
        editorToolbar.visibility = View.VISIBLE
    }
}
