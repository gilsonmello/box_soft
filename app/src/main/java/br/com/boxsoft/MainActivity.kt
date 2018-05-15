package br.com.boxsoft

import android.nfc.Tag
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import br.com.boxsoft.api.UdacityCatalogService
import br.com.boxsoft.model.UdacityCatalog
import butterknife.BindView
import butterknife.OnClick
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    @BindView(R.id.edtEmail)
    var email: EditText? = null

    @BindView(R.id.edtPassword)
    var password: EditText? = null

    @BindView(R.id.btnLogin)
    var btnLogin: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
                .baseUrl("https://www.udacity.com/public-api/v0/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val service = retrofit.create(UdacityCatalogService::class.java)
        val requestCatalog = service.listCatolog()

        requestCatalog?.enqueue(object: Callback<UdacityCatalog> {
            override fun onResponse(call: Call<UdacityCatalog>, response: Response<UdacityCatalog>?) {
                if(response!!.isSuccessful) {

                }
            }

            override fun onFailure(call: Call<UdacityCatalog>, t: Throwable?) {
                Log.e("Teste", "Erro"+ t!!.message)
            }
        })
    }

    @OnClick(R.id.btnLogin)
    fun login (view: View?) {

    }
}
