package com.example.dormmatch.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dormmatch.*
import com.example.dormmatch.adapters.propriedadeAdapter
import com.example.dormmatch.models.propriedade.Propriedade
import com.example.dormmatch.models.propriedade.propriedadeViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
private lateinit var viewModel: propriedadeViewModel
private lateinit var propriedadeRecyclerView: RecyclerView
private lateinit var adapter: propriedadeAdapter

private lateinit var propriedadeArrayList: ArrayList<Propriedade>


class Home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var idCategoria: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idCategoria = arrayListOf()

        firebaseAuth = FirebaseAuth.getInstance()

        propriedadeArrayList = arrayListOf<Propriedade>()

        propriedadeRecyclerView = view.findViewById(R.id.recView)
        propriedadeRecyclerView.layoutManager = LinearLayoutManager(this.context)
        propriedadeRecyclerView.setHasFixedSize(true)
        adapter = propriedadeAdapter(propriedadeArrayList, this)
        propriedadeRecyclerView.adapter = adapter

        viewModel = ViewModelProvider(this).get(propriedadeViewModel::class.java)
        viewModel.allPropriedade.observe(viewLifecycleOwner, Observer {
            adapter.updatePropriedadeList(it)
        })

        val btnAddAn = view.findViewById<FloatingActionButton>(R.id.addAn)
        btnAddAn.setOnClickListener{
            val intent = Intent(requireContext(), CreateRoomActivity::class.java)
            startActivity(intent)
        }

        val helloText = getText(R.string.hello)
        val textWelcome = view.findViewById<TextView>(R.id.welcome)
        textWelcome.text = "$helloText, " + firebaseAuth.currentUser?.displayName.toString() + "!"

        val logoutButton = view.findViewById<Button>(R.id.logout)
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }


        val btnHouse = view.findViewById<Button>(R.id.btnHouse)
        btnHouse.setOnClickListener {
            val category = 1 // Categoria desejada

            loadListFilter(category)
        }

        val btnRoom = view.findViewById<Button>(R.id.btnRoom)
        btnRoom.setOnClickListener {
            val category = 3 // Categoria desejada

            loadListFilter(category)
        }
        val btnApart = view.findViewById<Button>(R.id.btnApart)
        btnApart.setOnClickListener {
            val category = 0 // Categoria desejada

            loadListFilter(category)
        }

        val btnOthers = view.findViewById<Button>(R.id.btnOthers)
        btnOthers.setOnClickListener {
            val category = 2 // Categoria desejada

            loadListFilter(category)
        }

        val btnSearch = view.findViewById<Button>(R.id.btnSearch)
        btnSearch.setOnClickListener {
            val searchtxt = view.findViewById<EditText>(R.id.search)
            val titulo = searchtxt.text // Categoria desejada

            loadListSearch(titulo.toString())
        }
    }
      fun onPropClickItem(position: Int) {
        val idProp = propriedadeArrayList[position].idPropriedade

        val intent = Intent(context, ViewRoom::class.java)
        intent.putExtra("idPropriedade", idProp)
        startActivity(intent)
    }

    fun loadListFilter(categoryID: Int){
        val idProp = ArrayList<String>()

        val ref = FirebaseDatabase.getInstance().getReference("propriedade")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    adapter.rmAll()
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
                                            val idCat = "${snapshot.child("id_categoria").value}"

                                                if(idCat.equals(categoryID.toString())){

                                                    val propriedade = snapshot.getValue(Propriedade::class.java)
                                                    if(propriedade!=null) {
                                                        adapter.addTodo(propriedade)
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


    fun loadListSearch(titulo: String){
        val idProp = ArrayList<String>()

        val ref = FirebaseDatabase.getInstance().getReference("propriedade")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    adapter.rmAll()
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
                                        val title= "${snapshot.child("titulo").value}"

                                        if(title.equals(titulo)){

                                            val propriedade = snapshot.getValue(Propriedade::class.java)
                                            if(propriedade!=null) {
                                                adapter.addTodo(propriedade)
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








}


