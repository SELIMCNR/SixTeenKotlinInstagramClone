package com.selimcinar.kotlininstagramclone.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Adapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.selimcinar.kotlininstagramclone.R
import com.selimcinar.kotlininstagramclone.adapter.feedRecyclerAdapter
import com.selimcinar.kotlininstagramclone.databinding.ActivityFeedBinding
import com.selimcinar.kotlininstagramclone.model.Post

class Activity_Feed : AppCompatActivity(){

    private  lateinit var binding: ActivityFeedBinding

    private lateinit var toolbar: MaterialToolbar
    private  lateinit var auth: FirebaseAuth
    private  lateinit var db : FirebaseFirestore
    val postArrayList : ArrayList<Post> = ArrayList()
    var adapter : feedRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbars
        setSupportActionBar(toolbar)

        auth = Firebase.auth
        db = Firebase.firestore
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        getData()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter=feedRecyclerAdapter(postArrayList)
        binding.recyclerView.adapter = adapter

    }
    private fun getData() {
        db.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(applicationContext, "Error fetching data: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
            } else {
                if (value != null) {
                    if (!value.isEmpty) {
                        postArrayList.clear()

                        val documents = value.documents

                        for (document in documents) {
                            val comment = document.get("comment") as String
                            val userEmail = document.get("userEmail") as String
                            val downloadUrl = document.get("downloadUrl") as String

                            val post = Post(userEmail, comment, downloadUrl)
                            postArrayList.add(post)
                        }

                        adapter?.notifyDataSetChanged() // Verileri güncelle
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.insta_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_post){
            val  intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
        else if (item.itemId == R.id.signut){
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

}