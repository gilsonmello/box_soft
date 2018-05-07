package br.com.boxsoft

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick



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
    }

    @OnClick(R.id.btnLogin)
    fun login (view: View?) {
        
    }
}
