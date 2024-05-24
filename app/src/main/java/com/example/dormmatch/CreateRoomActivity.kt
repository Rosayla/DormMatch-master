package com.example.dormmatch

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.dormmatch.databinding.CriarEditarAnuncioBinding
import com.example.dormmatch.fragments.Home
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList

class CreateRoomActivity : AppCompatActivity() {
    private lateinit var binding: CriarEditarAnuncioBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var nameCategoria: ArrayList<String>
    private lateinit var catID : String

    private lateinit var imageForCapa : ArrayList<String>
    private var imagegeUri:Uri ?= null

    private var imagem = 0

    private val idPropriedade = UUID.randomUUID().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        listaCategoria()
        catID = ""
        nameCategoria = arrayListOf()
        imageForCapa = arrayListOf()
        super.onCreate(savedInstanceState)
        binding = CriarEditarAnuncioBinding.inflate(layoutInflater)

        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        FirebaseApp.initializeApp(this)

        binding.btnFicheiros.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imageActivity.launch(intent)
        }

        //val categorias = nameCategoria

        val customVal = arrayListOf("Apartamento", "Casa", "Outros", "Quarto")
        Log.d("**** TAG", nameCategoria.toString())
        val spinner = findViewById<Spinner>(R.id.spinner)
        if (spinner != null) {
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, customVal)
            //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            // Set a listener to retrieve the selected value
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                   catID = parent.selectedItemId.toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing
                }
            }

        }
    }

    fun btnCriar(view: View) {
        validarDados()
    }

    fun btnVoltar(view: View) {
        onBackPressed()
    }


    val input = readLine()
    private var titulo = ""
    private var descricao = ""
    private var telemovel = ""
    private var arCondicionado = false
    private var maquinaLavar = false
    private var mobilia = false
    private var wifi = false
    private var localizacao = ""
    private var tipo = ""
    private var preco = ""

    //valida dos dados antes de publicar
    private fun validarDados() {
        titulo = binding.etTitulo.text.toString().trim()
        descricao = binding.etDescricao.text.toString().trim()
        telemovel = binding.etContacto.text.toString().trim()
        arCondicionado = binding.arCondicionadoCheck.isChecked
        maquinaLavar = binding.maquilaLavarCheck.isChecked
        mobilia = binding.mobiliaCheck.isChecked
        wifi = binding.wifiCheck.isChecked
        localizacao = binding.etLocalizacao.text.toString().trim()
        preco = binding.etPreco.text.toString().trim()

        if (binding.venda.isChecked){
            tipo = "Venda"
        }else if (binding.arrendamento.isChecked){
            tipo = "Arrendamento"
        }

        //validar
        if (TextUtils.isEmpty(telemovel.toString())){
            //sem telemovel
            binding.etContacto.error = R.string.insertPhoneNumber.toString()
            return
        }else if(imageForCapa.isEmpty()){
            Toast.makeText(this, "Tem que adicionar uma imagem", Toast.LENGTH_LONG).show()
            return
        }
        else if (TextUtils.isEmpty(titulo)){
            //sem titulo
            binding.etTitulo.error = R.string.insertTitle.toString()
            return
        }else if (TextUtils.isEmpty(descricao)){
            //sem descricao
            binding.etDescricao.error = R.string.insertDescription.toString()
            return
        }else if (TextUtils.isEmpty(localizacao)){
            //sem localizacao
            binding.etLocalizacao.error = R.string.insertAddress.toString()
            return
        }
        else if(binding.venda.isChecked==false && binding.arrendamento.isChecked==false){
            binding.arrendamento.error = R.string.selectOp.toString()
            return
        }else{
            //submter os dados para a bd
            guardaInfo()
        }
    }

    private fun guardaInfo() {
        //guarda os dados no real time database
        val uId = auth.uid

        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["idPropriedade"]=idPropriedade
        hashMap["id_user"]=uId
        hashMap["id_categoria"]= catID
        hashMap["imagemCapa"]=imageForCapa[0]
        hashMap["titulo"]=titulo
        hashMap["descricao"]=descricao
        hashMap["telemovel"]=telemovel.toInt()
        hashMap["arCondicionado"]=arCondicionado
        hashMap["wifi"]=wifi
        hashMap["mobilia"]=mobilia
        hashMap["maquinaLavar"]=maquinaLavar
        hashMap["preco"]=preco.toInt()
        hashMap["localizacao"]=localizacao
        hashMap["tipo"]=tipo

        //guardar td
        val ref = FirebaseDatabase.getInstance().getReference("propriedade")
        ref.child(idPropriedade!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                //caso de sucesso
                Toast.makeText(this, R.string.propriedadeRegistada, Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, AnnounceListActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                //caso de fail
                Toast.makeText(this, R.string.registerFail, Toast.LENGTH_SHORT).show()
            }
    }

    fun listaCategoria() {
        val refCat = FirebaseDatabase.getInstance().getReference("categorias")
        refCat.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (anuncioSnap in snapshot.children) {
                        val name = "${anuncioSnap.child("descricao").value}"
                        nameCategoria.add(name)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
    })
    }

    //Utilizar imagens da galeria
    private val imageActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{ result ->
            //obter o uri da imagem
            if (result.resultCode == RESULT_OK){
                var idFoto = UUID.randomUUID().toString()

                val data = result.data
                imagegeUri = data!!.data

                // Pasta + idFoto | imagem
                val filePath = "foto/" + idFoto

                //referencia de armazenamento
                val reference = FirebaseStorage.getInstance().getReference(filePath)
                reference.putFile(imagegeUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        //Sucesso obtem a url da imagem
                        val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                        while (!uriTask.isSuccessful);

                        val uploadedImageUrl = "${uriTask.result}"
                        imagem=1

                        imageForCapa.add(uploadedImageUrl)
                        gravaImagemRealTime(idFoto, uploadedImageUrl)
                    }
                    .addOnFailureListener{

                        //Envia um toast de erro
                        Toast.makeText(this, "Error to send the image", Toast.LENGTH_SHORT).show()
                    }

            }else{
                //Cancela
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    )

    private fun gravaImagemRealTime(idFoto: String, imageUrl: String) {
        //envia informação para a BD
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["id_foto"] = idFoto
        hashMap["idPropriedade"] = idPropriedade
        hashMap["imageData"] = imageUrl


        //update
        val reference = FirebaseDatabase.getInstance().getReference("foto")
        reference.child(idFoto!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {

                //Envia um toast
                Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {

                //Envia um toast de erro
                Toast.makeText(this, "Error to save the image", Toast.LENGTH_SHORT).show()
            }
    }


}