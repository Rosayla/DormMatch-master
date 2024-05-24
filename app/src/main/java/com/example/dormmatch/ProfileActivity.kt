package com.example.dormmatch

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dormmatch.databinding.PerfilActivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: PerfilActivityBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PerfilActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener{
            onBackPressed()
        }
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("user")

        showData()
        binding.nome.setText(auth.currentUser?.displayName)
        binding.email.setText(auth.currentUser?.email)


        binding.saveChanges.setOnClickListener{
            editarNomeEmailTele("username", binding.nome.text.toString())
            editarNomeEmailTele("email", binding.email.text.toString())
            editarNomeEmailTele("telefone", binding.telefone.text.toString())
        }
    }

    fun showData() {
        val currentUser = auth.currentUser
        //binding.email.text = currentUser.toString().email

        //binding.nome.text.setText()


        database.child(currentUser!!.uid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userData = dataSnapshot.value as? Map<String, String>
                //val displayName = userData?.get("username")
                //val email = userData?.get("email")
                val phoneNumber = userData?.get("telefone")


                binding.telefone.setText(phoneNumber)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FIREBASE", "Erro ao recolher dados")
            }
        })
    }

    fun editarNomeEmailTele(child: String, novo: String){
        database.child(auth.currentUser!!.uid).child(child).setValue(novo)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    showData()
                }else{
                    Toast.makeText(this, "Erro a guardar dados!", Toast.LENGTH_SHORT).show()
                }
            }
    }

}