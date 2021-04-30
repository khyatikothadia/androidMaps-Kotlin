package com.example.myapplication.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.myapplication.R
import com.example.myapplication.model.request.LoginRequest
import com.example.myapplication.preferences.PreferenceManager
import com.example.myapplication.retrofit.RetrofitClient
import com.example.myapplication.ui.base.ViewModelFactory
import com.example.myapplication.util.Status
import com.example.myapplication.util.Utils
import com.example.myapplication.util.Utils.Companion.hideSoftKeyboard
import com.example.myapplication.util.Utils.Companion.isNetworkConnected
import com.example.myapplication.viewmodel.AccountViewModel
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mAccountViewModel: AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
    }

    /**
     * Method to initialize UI views and registers listeners
     */
    private fun initView() {
        mAccountViewModel = ViewModelProviders.of(this, ViewModelFactory(RetrofitClient.apiService))
            .get(AccountViewModel::class.java)
        activityLoginBtnLogin.setOnClickListener(this)
        activityLoginTvSignUpInstead.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        val id = view?.id
        if (id == R.id.activityLoginBtnLogin) {
            performValidation()
            hideSoftKeyboard(this)
        } else if (id == R.id.activityLoginTvSignUpInstead) {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Method to perform basic email and null field validations
     */
    private fun performValidation() {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val emailId = activityLoginEdtEmail.text.toString()
        val password = activityLoginEdtPassword.text.toString()
        if (emailId.isNotEmpty() && emailId.matches(emailPattern.toRegex()) &&
            password.isNotEmpty()
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isNetworkConnected(this)) {
                performUserLogin(emailId, password)
            } else {
                Utils.displayDialog(getString(R.string.alert_internet_connection), this, false)
            }
        } else {
            Utils.displayDialog(getString(R.string.alert_enter_valid_details), this, false)
        }
    }

    /**
     * Method to perform login and observe login response
     */
    private fun performUserLogin(emailId: String, password: String) {
        val loginRequest = LoginRequest(emailId, password)
        mAccountViewModel.getUserInfo(loginRequest).observe(this, { it ->
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        loginProgressBar.visibility = View.GONE
                        if (it.data!!.isSuccessful) {
                            it.data.body()?.authenticationToken?.let { authToken ->
                                PreferenceManager.getInstance(this).setAuthToken(authToken)
                                val intent = Intent(this, MapsActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            val error = it.data.errorBody()?.string()
                            error?.let {
                                Utils.displayDialog(
                                    JSONObject(it).getString("message"), this, false
                                )
                            }
                        }
                    }
                    Status.ERROR -> {
                        loginProgressBar.visibility = View.GONE
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        loginProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }
}