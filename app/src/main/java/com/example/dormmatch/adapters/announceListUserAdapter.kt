package com.example.dormmatch.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dormmatch.AnnounceListActivity
import com.example.dormmatch.R
import com.example.dormmatch.fragments.Home
import com.example.dormmatch.models.propriedade.Propriedade
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class announceListUserAdapter( private val propriedadeList: ArrayList<Propriedade>, private val onAnnouceClickListenner: AnnounceListActivity):  RecyclerView.Adapter<announceListUserAdapter.AnnounceListUserViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnounceListUserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.announce_line,
            parent,false
        )
        return AnnounceListUserViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return this.propriedadeList.size
    }

    fun updatePropriedadeList(proplist: List<Propriedade>){
        this.propriedadeList.clear()
        this.propriedadeList.addAll(proplist)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AnnounceListUserViewHolder, position: Int) {
        val currentList = this.propriedadeList[position]

        holder.title.text = currentList.titulo
        holder.address.text = currentList.localizacao
        holder.rent.text = currentList.preco.toString() + " € / mês"

        // atraves do codigo do anuncio vou buscar uma imagem a BD
        val ref = FirebaseDatabase.getInstance().getReference("foto")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var conta = 0
                    for (anuncioSnap in snapshot.children) {
                        val idP = "${anuncioSnap.child("idPropriedade").value}"
                        if (idP.equals(currentList.idPropriedade)) {
                            val image = "${anuncioSnap.child("imageData").value}"
                            Picasso.get().load(image).into(holder.foto)
                            conta += 1
                        }
                    }
                    if (conta == 0) {
                        val image = "https://wallpapers.com/images/featured-full/blank-white-7sn5o1woonmklx1h.jpg"
                        Picasso.get().load(image).into(holder.foto)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        // atraves do codigo da categoria vou buscar o nome da categoria a BD
        val refCat = FirebaseDatabase.getInstance().getReference("categorias")
        refCat.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var conta = 0
                    for (anuncioSnap in snapshot.children) {
                        val idC = "${anuncioSnap.child("id_categoria").value}"
                        if (idC.equals(currentList.id_categoria)) {
                            val name = "${anuncioSnap.child("descricao").value}"
                            holder.cat.text = name
                            conta += 1
                        }
                    }
                    if (conta == 0) {
                        holder.cat.text = ""
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        holder.itemView.setOnClickListener {
            onAnnouceClickListenner.onPropClickAnnounce(position)
        }

    }

    fun addTodo(sl: Propriedade) {
        propriedadeList.add(sl)
        notifyDataSetChanged()
    }

    fun rmAll() {
        propriedadeList.clear()
        notifyDataSetChanged()
    }

    class AnnounceListUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.title)
        val address: TextView = itemView.findViewById(R.id.address)
        val rent: TextView = itemView.findViewById(R.id.price)
        val cat: TextView = itemView.findViewById(R.id.categoria)
        val foto: ImageView = itemView.findViewById(R.id.imagemA)

    }
}