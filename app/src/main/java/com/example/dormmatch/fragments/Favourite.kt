package com.example.dormmatch.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dormmatch.R
import com.example.dormmatch.ViewRoom
import com.example.dormmatch.adapters.favouritePropriedadeAdapter
import com.example.dormmatch.models.propriedade.Propriedade
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private lateinit var propriedadeRecyclerView: RecyclerView
private lateinit var PropListAdapter: favouritePropriedadeAdapter


private lateinit var propriedadeArrayList: ArrayList<Propriedade>

/**
 * A simple [Fragment] subclass.
 * Use the [Favourite.newInstance] factory method to
 * create an instance of this fragment.
 */
class Favourite : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onResume() {
        super.onResume()
        loadList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Favourite.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Favourite().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        propriedadeArrayList = arrayListOf<Propriedade>()

        PropListAdapter = favouritePropriedadeAdapter(ArrayList(), this)
        propriedadeRecyclerView = view.findViewById(R.id.propFav)
        propriedadeRecyclerView.layoutManager = LinearLayoutManager(this.context)
        propriedadeRecyclerView.adapter = PropListAdapter
        PropListAdapter.rmAll()
        PropListAdapter.notifyDataSetChanged()
    }

    private fun loadList() {
        val idProp = ArrayList<String>()

        val ref = FirebaseDatabase.getInstance().getReference("user")
        ref.child(firebaseAuth.uid!!).child("Favoritos")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    PropListAdapter.rmAll()
                    if (snapshot.exists()) {
                        for (anuncioSnap in snapshot.children) {
                            val idP = "${anuncioSnap.child("idPropriedade").value}"
                            idProp.add(idP)
                        }
                    }
                    for (i in idProp) {
                        val refProp = FirebaseDatabase.getInstance().getReference("propriedade")
                        refProp.child(i)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val propriedade = snapshot.getValue(Propriedade::class.java)
                                    if(propriedade!=null) {
                                        PropListAdapter.addTodo(propriedade)
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

    fun onPropClickFav(idPropriedade: String) {
        val intent = Intent(context, ViewRoom::class.java)
        intent.putExtra("idPropriedade", idPropriedade)
        startActivity(intent)
    }
}