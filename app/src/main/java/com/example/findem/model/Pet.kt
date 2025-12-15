package com.example.findem.model

// --- CLIENTE RETROFIT (CÓDIGO DE INFRAESTRUTURA) ---
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
// --- INTERFACE RETROFIT PARA A API DO IBGE ---
import retrofit2.http.GET
import retrofit2.http.Path

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

// ... (Seu código Pet e Notificacao existentes)

// --- NOVOS MODELOS PARA O IBGE ---

data class Estado(
    val id: Int,
    val sigla: String, // Ex: "PE"
    val nome: String   // Ex: "Pernambuco"
)

data class Municipio(
    val id: Int,
    val nome: String // Ex: "Recife"
)

interface IBGEService {
    // Busca todos os estados, ordenados por nome
    @GET("localidades/estados?orderBy=nome")
    suspend fun getEstados(): List<Estado>

    // Busca todos os municípios de um estado, usando a sigla (UF)
    @GET("localidades/estados/{UF}/municipios?orderBy=nome")
    suspend fun getMunicipiosPorEstado(@Path("UF") uf: String): List<Municipio>
}
object RetrofitClient {
    private const val BASE_URL = "https://servicodados.ibge.gov.br/api/v1/"

    val ibgeService: IBGEService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(IBGEService::class.java)
    }
}