package com.example.dormmatch

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import com.example.dormmatch.databinding.ViewRoomBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ViewRoom : AppCompatActivity() {
    private lateinit var binding: ViewRoomBinding

    private lateinit var viewFlipperCapa: ViewFlipper

    private lateinit var imageVC: ImageView

    private lateinit var firebaseAuth: FirebaseAuth

    // guarda o valor do favorito
    private var isInMyFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ViewRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val idPropriedade = intent.getStringExtra("idPropriedade")

        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            checkFav(idPropriedade!!)
        }

        viewFlipperCapa = binding.imagemA

        carregaImgCapa(idPropriedade!!)
        loadAnuncio(idPropriedade)

        //Favoritos
        binding.favBtn.setOnClickListener {
            if (isInMyFavorite) {
                //remove
                removeFromFav(idPropriedade)
            } else {
                //adiciona
                addFavorite(idPropriedade)
            }
        }

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.btnCall.setOnClickListener {
            startActivity(
                Intent(Intent.ACTION_DIAL)
                    .setData(Uri.parse("tel:" + binding.contacto.text))
            )
        }

    }

    private fun loadAnuncio(idPropriedade: String) {
        val ref = FirebaseDatabase.getInstance().getReference("propriedade")
        ref.child(idPropriedade)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    //carrega os dados
                    val titulo = "${snapshot.child("titulo").value}"
                    val localizacao = "${snapshot.child("localizacao").value}"
                    val preco = "${snapshot.child("preco").value}"
                    val descricao = "${snapshot.child("descricao").value}"
                    val telemovel = "${snapshot.child("telemovel").value}"
                    val tipo = "${snapshot.child("tipo").value}"
                    val arCondicionado = "${snapshot.child("arCondicionado").value}"
                    val wifi = "${snapshot.child("wifi").value}"
                    val mobilia = "${snapshot.child("mobilia").value}"
                    val maquilaLavar = "${snapshot.child("maquinaLavar").value}"

                    //coloca os dados
                    binding.roomTitle.text = titulo
                    binding.roomTitle2.text = titulo
                    binding.location.text = localizacao
                    binding.price.text = preco + " € / mês"
                    binding.type.text = "Tipo: " + tipo
                    binding.description.text = descricao
                    binding.contacto.text = telemovel

                    if (arCondicionado.equals("true")) {
                        binding.LlArCondicionado.visibility = View.VISIBLE
                    } else {
                        binding.LlArCondicionado.visibility = View.GONE
                    }
                    if (wifi.equals("true")) {
                        binding.LlWifi.visibility = View.VISIBLE
                    } else {
                        binding.LlWifi.visibility = View.GONE
                    }
                    if (mobilia.equals("true")) {
                        binding.LlMobilia.visibility = View.VISIBLE
                    } else {
                        binding.LlMobilia.visibility = View.GONE
                    }
                    if (maquilaLavar.equals("true")) {
                        binding.LlMaquinaLavar.visibility = View.VISIBLE
                    } else {
                        binding.LlMaquinaLavar.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun carregaImgCapa(idPropriedade: String) {
        val ref = FirebaseDatabase.getInstance().getReference("foto")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var conta = 0
                    for (anuncioSnap in snapshot.children) {
                        val idP = "${anuncioSnap.child("idPropriedade").value}"
                        if (idP.equals(idPropriedade)) {
                            val image = "${anuncioSnap.child("imageData").value}"
                            flipperImageCapa(image)
                            conta += 1
                        }
                    }
                    if (conta == 0) {
                        val image = "https://wallpapers.com/images/featured-full/blank-white-7sn5o1woonmklx1h.jpg"
                        flipperImageCapa(image)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun flipperImageCapa(imagem: String) {
        imageVC = ImageView(this)
        Picasso.get().load(imagem).into(imageVC)

        viewFlipperCapa.addView(imageVC)
        viewFlipperCapa.setFlipInterval(3000)
        viewFlipperCapa.setAutoStart(true)

        viewFlipperCapa.setInAnimation(this, android.R.anim.slide_in_left)
        viewFlipperCapa.setOutAnimation(this, android.R.anim.slide_out_right)
    }

    private fun checkFav(idPropriedade: String) {
        val rel = FirebaseDatabase.getInstance().getReference("user")
        rel.child(firebaseAuth.uid!!).child("Favoritos").child(idPropriedade)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    isInMyFavorite = snapshot.exists()
                    if (isInMyFavorite) {
                        //já existia
                        binding.favBtn.setImageResource(R.drawable.baseline_favorite_24)
                    } else {
                        binding.favBtn.setImageResource(R.drawable.baseline_favorite_border_24)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun addFavorite(idPropriedade: String) {
        val hashMap = HashMap<String, Any>()
        hashMap["idPropriedade"] = idPropriedade

        //guardar na BD
        val rel = FirebaseDatabase.getInstance().getReference("user")
        rel.child(firebaseAuth.uid!!).child("Favoritos").child(idPropriedade)
            .setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Anuncio adicionado aos favoritos", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show()
            }
    }

    private fun removeFromFav(idPropriedade: String) {
        val rel = FirebaseDatabase.getInstance().getReference("user")
        rel.child(firebaseAuth.uid!!).child("Favoritos").child(idPropriedade)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Anuncio removido dos favoritos", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show()
            }

    }
}