package com.example.myapplication.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.model.request.LoginRequest
import com.example.myapplication.preferences.PreferenceManager
import com.example.myapplication.retrofit.RetrofitClient
import com.example.myapplication.ui.base.ViewModelFactory
import com.example.myapplication.util.Status
import com.example.myapplication.util.Utils
import com.example.myapplication.util.Utils.Companion.hideSoftKeyboard
import com.example.myapplication.util.Utils.Companion.isNetworkConnected
import com.example.myapplication.viewmodel.AccountViewModel
import org.json.JSONObject

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAccountViewModel: AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initView()
    }

    /**
     * Method to initialize UI views and registers listeners
     */
    private fun initView() {
        mAccountViewModel = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitClient.apiService)
        )[AccountViewModel::class.java]
        binding.activityLoginBtnLogin.setOnClickListener(this)
        binding.activityLoginTvSignUpInstead.setOnClickListener(this)
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
        val emailId = binding.activityLoginEdtEmail.text.toString()
        val password = binding.activityLoginEdtPassword.text.toString()
        if (emailId.isNotEmpty() && emailId.matches(emailPattern.toRegex()) &&
            password.isNotEmpty()
        ) {
            if (isNetworkConnected(this)) {
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
        mAccountViewModel.getUserInfo(loginRequest).observe(this) { it ->
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.loginProgressBar.visibility = View.GONE
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
                        binding.loginProgressBar.visibility = View.GONE
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }

                    Status.LOADING -> {
                        binding.loginProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}