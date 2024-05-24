package com.example.dormmatch.repository

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dormmatch.models.propriedade.Propriedade
import com.example.dormmatch.models.propriedade.propriedadeViewModel
import com.google.firebase.database.*

class propriedadeRepository {

    private val databaseReference = FirebaseDatabase.getInstance().getReference("propriedade")

    @Volatile private var INSTANCE : propriedadeRepository ?= null

    fun getInstance() : propriedadeRepository{
        return INSTANCE ?: synchronized(this){

            val instance = propriedadeRepository()
            INSTANCE = instance
            instance
        }
    }

    fun loadPropriedade(propriedadeList: MutableLiveData<List<Propriedade>>) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val _propriedadeList: List<Propriedade> =
                        snapshot.children.map { dataSnapshot ->
                            dataSnapshot.getValue(Propriedade::class.java)!!
                        }
                    propriedadeList.postValue(_propriedadeList)
                } catch (e: Exception) {
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }





    /*

    private fun searchPropriedade() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val propriedadeList = mutableListOf<Propriedade>()

                if (snapshot.exists()) {
                    for (i in snapshot.children) {
                        val _propriedade = i.getValue(Propriedade::class.java)
                        _propriedade?.let { propriedadeList.add(it) }
                    }

                    // Update your RecyclerView adapter here
                    adapter.updatePropriedadeList(propriedadeList)
                } else {
                    Toast.makeText(applicationContext, "Data Does not Exist", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error here
            }
        })
    }*/
    // filtering in general
   /* private fun filterLoadCategory(){
        // Specifying path and filter category and adding a listener
        databaseReference.orderByChild("id_categoria").equalTo("1").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    propriedadeList.clear()
                    for (i in snapshot.children){
                        val  casa = i.getValue(Propriedade::class.java)
                        casa.add(casa!!)
                    }
                    propriedadeAdapter.submitList(propriedadeList)
                    binding.recyclerPropriedaade.adapter = propriedadeAdapter
                } else{
                    Toast.makeText(applicationContext, "Data is not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
*/
        // Other functions in your repository...

      /*fun filterLoadCategory(category: Int, propriedadeList: MutableLiveData<List<Propriedade>>) {
            // Specifying path and filter category and adding a listener

            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    try {
                        val filteredPropriedades: List<Propriedade> = snapshot.children.map { dataSnapshot ->
                            dataSnapshot.getValue(Propriedade::class.java)!!
                        }.filter { propriedade ->
                            propriedade.id_categoria == category.toString()
                        }
                        propriedadeList.postValue(filteredPropriedades)
                    } catch (e: Exception) {
                        // Handle exception
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle cancellation
                }
            })
        }*/


}


