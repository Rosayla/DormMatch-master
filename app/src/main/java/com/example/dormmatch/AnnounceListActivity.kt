package com.example.dormmatch

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dormmatch.adapters.announceListUserAdapter
import com.example.dormmatch.adapters.favouritePropriedadeAdapter
import com.example.dormmatch.databinding.AnnounceListActivityBinding
import com.example.dormmatch.models.propriedade.Propriedade
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AnnounceListActivity : AppCompatActivity() {
    private lateinit var propriedadeRecyclerView: RecyclerView
    private lateinit var PropListAdapter: announceListUserAdapter


    private lateinit var propriedadeArrayList: ArrayList<Propriedade>

    private lateinit var binding: AnnounceListActivityBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AnnounceListActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener{
            onBackPressed()
        }
        auth = FirebaseAuth.getInstance()

        loadList(auth.uid.toString())

        propriedadeArrayList = arrayListOf<Propriedade>()

        PropListAdapter = announceListUserAdapter(ArrayList(), this)
        propriedadeRecyclerView = findViewById(R.id.propFav)
        propriedadeRecyclerView.layoutManager = LinearLayoutManager(this)
        propriedadeRecyclerView.adapter = PropListAdapter
        PropListAdapter.rmAll()
        PropListAdapter.notifyDataSetChanged()

        binding.btAddAnnounce.setOnClickListener{
            val intent = Intent(this, CreateRoomActivity::class.java)
            startActivity(intent)
        }

    }

    private fun loadList(uid: String) {
        val idProp = ArrayList<String>()

        val ref = FirebaseDatabase.getInstance().getReference("propriedade")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    PropListAdapter.rmAll()
                    if (snapshot.exists()) {
                        for (anuncioSnap in snapshot.children) {
                            val idP = "${anuncioSnap.child("idPropriedade").value}"
                            idProp.add(idP)
                        }
                    }
                    for (id in idProp) {
                        val refProp = FirebaseDatabase.getInstance().getReference("propriedade")
                        refProp.child(id)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists()){
                                        val idUser = "${snapshot.child("id_user").value}"
                                        if(idUser.equals(uid)){
                                            val propriedade = snapshot.getValue(Propriedade::class.java)
                                            if(propriedade!=null) {
                                                PropListAdapter.addTodo(propriedade)
                                            }
                                        }

                                    }

                                }
                                override fun onCancelled(error: DatabaseError) {
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    fun onPropClickAnnounce(idPropriedade: Int) {
        val intent = Intent(this, ViewRoom::class.java)
        intent.putExtra("idPropriedade", idPropriedade)
        startActivity(intent)
    }
}