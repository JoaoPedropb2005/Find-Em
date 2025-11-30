package com.example.findem.model

data class Pet(
    val id: Int,
    val nome: String,
    val raca: String,
    val endereco: String,
    val classificacao: String,
    val imagemRes: Int,
    val especie: String,
    val categoria: String,
    val descricaoLocal: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

data class Notificacao(
    val id: Int,
    val mensagem: String,
    val distancia: String
)