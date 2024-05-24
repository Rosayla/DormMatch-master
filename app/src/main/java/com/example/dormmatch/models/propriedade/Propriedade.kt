package com.example.dormmatch.models.propriedade

data class Propriedade(
    /*
    var categoria: String ?= null,
    var user: String ?= null,
     */
    var idPropriedade: String ?= null,
    var id_categoria: String ?= null,
    var imagem: String ?= null,
    var localizacao: String ?= null,
    var preco: Int ?= null,
    var titulo: String ?= null
)
