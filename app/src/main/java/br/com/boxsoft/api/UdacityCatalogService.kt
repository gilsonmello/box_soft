package br.com.boxsoft.api

import br.com.boxsoft.model.UdacityCatalog
import retrofit2.Call
import retrofit2.http.GET

interface UdacityCatalogService {

    @GET("courses")
    fun listCatolog() : Call<UdacityCatalog>?
    
}