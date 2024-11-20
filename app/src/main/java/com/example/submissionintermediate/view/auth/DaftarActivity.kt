package com.example.submissionintermediate.view.auth

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.submissionintermediate.R
import com.example.submissionintermediate.databinding.ActivityDaftarBinding
import com.example.submissionintermediate.databinding.ActivityLoginBinding
import com.example.submissionintermediate.viewmodel.MainViewModel
import com.example.submissionintermediate.viewmodel.ViewModelFactory

class DaftarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDaftarBinding

    private val registerViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getAuthInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDaftarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        setupView()
        setupAction()
        observeViewModel()

    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
    private fun setupAction() {
        binding.daftarButton.setOnClickListener {
            val name = binding.fullNameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                registerViewModel.register(name, email, password)
            } else {
                showToast("Please fill in all fields")
            }
        }
    }

    private fun observeViewModel() {
        registerViewModel.isRegisterSuccessful.observe(this) { isSuccessful ->
            if (isSuccessful) {
                showToast("Registration successful!")
                navigateToLogin()
            }
        }

        registerViewModel.errorMessage.observe(this) { error ->
            error?.let {
                showErrorDialog(it)
                registerViewModel.clearErrorMessage()
            }
        }

        registerViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun navigateToLogin() {
        Intent(this, LoginActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
        finish()
    }

    private fun showErrorDialog(message: String) {
        showLoading(false)
        AlertDialog.Builder(this).apply {
            setTitle("Error")
            setMessage(message)
            setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            create()
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}