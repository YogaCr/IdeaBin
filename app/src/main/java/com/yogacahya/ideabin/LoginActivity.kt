package com.yogacahya.ideabin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    lateinit var gso: GoogleSignInOptions
    lateinit var client: GoogleApiClient
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            checkUser(auth.currentUser!!)
        }
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        client = GoogleApiClient.Builder(this).addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
        btnGoogleSignIn.setOnClickListener {
            val intent = Auth.GoogleSignInApi.getSignInIntent(client)
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                val account = result.signInAccount
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        checkUser(account!!)
                    }

                }
            }
        }
    }

    fun checkUser(account: FirebaseUser) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users").document(account.uid).get().addOnSuccessListener {
            if (it.exists()) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val data = HashMap<String, String>()
                data.put("name", account.displayName!!)
                data.put("image", account.photoUrl!!.toString())
                firestore.collection("users").document(account.uid).set(data).addOnSuccessListener {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener {
                    Toasty.error(this, "Login Failure").show()
                }
            }
        }.addOnFailureListener {
            Toasty.error(this, "Login Failure").show()
        }
    }

    fun checkUser(account: GoogleSignInAccount) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users").document(account.id!!).get().addOnSuccessListener {
            if (it.exists()) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val data = HashMap<String, String>()
                data.put("name", account.displayName!!)
                data.put("image", account.photoUrl!!.toString())
                firestore.collection("users").document(account.id!!).set(data).addOnSuccessListener {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener {
                    Toasty.error(this, "Login Failure").show()
                }
            }
        }.addOnFailureListener {
            Toasty.error(this, "Login Failure").show()
        }
    }
}
