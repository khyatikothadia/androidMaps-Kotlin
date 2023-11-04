package com.example.myapplication.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySignupBinding
import com.example.myapplication.model.request.SignUpRequest
import com.example.myapplication.preferences.PreferenceManager
import com.example.myapplication.retrofit.RetrofitClient
import com.example.myapplication.ui.base.ViewModelFactory
import com.example.myapplication.util.Status
import com.example.myapplication.util.Utils
import com.example.myapplication.util.Utils.Companion.hideSoftKeyboard
import com.example.myapplication.util.Utils.Companion.isNetworkConnected
import com.example.myapplication.viewmodel.AccountViewModel
import org.json.JSONException
import org.json.JSONObject

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var mAccountViewModel: AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
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
        binding.activitySignUpBtnSignUp.setOnClickListener {
            performValidation()
            hideSoftKeyboard(this)
        }
    }

    /**
     * Method to perform basic email and null field validations
     */
    private fun performValidation() {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val emailId = binding.activitySignUpEdtEmail.text.toString()
        val fullName = binding.activitySignUpEdtFullName.text.toString()
        val password = binding.activitySignUpEdtPassword.text.toString()
        val confirmPassword = binding.activitySignUpEdtConfirmPassword.text.toString()
        if (emailId.isNotEmpty() && emailId.matches(emailPattern.toRegex()) && fullName.isNotEmpty() &&
            password.isNotEmpty() && confirmPassword.isNotEmpty()
        ) {
            if (password.length < 8) {
                Utils.displayDialog(getString(R.string.alert_password_length), this, false)
            } else if (password != confirmPassword) {
                Utils.displayDialog(getString(R.string.alert_confirm_password), this, false)
            } else {
                if (isNetworkConnected(this)) {
                    performUserSignUp(fullName, emailId, password)
                } else {
                    Utils.displayDialog(getString(R.string.alert_internet_connection), this, false)
                }
            }
        } else {
            Utils.displayDialog(getString(R.string.alert_enter_valid_details), this, false)
        }
    }

    /**
     * Method to perform sign up and observe user response
     */
    private fun performUserSignUp(fullName: String, emailId: String, password: String) {
        val signUpRequest = SignUpRequest(fullName, emailId, password)
        mAccountViewModel.getNewUserInfo(signUpRequest).observe(this) { it ->
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.signUpProgressBar.visibility = View.GONE
                        if (it.data!!.isSuccessful) {
                            it.data.body()?.authenticationToken?.let { authToken ->
                                PreferenceManager.getInstance(this).setAuthToken(authToken)
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            val error = it.data.errorBody()?.string()
                            error?.let {
                                try {
                                    Utils.displayDialog(
                                        JSONObject(it).getString("message"), this, false
                                    )
                                } catch (_: JSONException) {
                                }
                            }
                        }
                    }

                    Status.ERROR -> {
                        binding.signUpProgressBar.visibility = View.GONE
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }

                    Status.LOADING -> {
                        binding.signUpProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}