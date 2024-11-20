package com.example.submissionintermediate.view

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissionintermediate.R
import com.example.submissionintermediate.adapter.StoriesAdapter
import com.example.submissionintermediate.data.response.ListStoryItem
import com.example.submissionintermediate.databinding.ActivityMainBinding
import com.example.submissionintermediate.view.auth.LoginActivity
import com.example.submissionintermediate.viewmodel.MainViewModel
import com.example.submissionintermediate.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getAuthInstance(this)
    }
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
    private fun setEventData(data: List<ListStoryItem>) {
        val adapter = StoriesAdapter()
        adapter.submitList(data)

        binding.rvStories.adapter = adapter
    }
    private fun checkUserSession() {
        // Check if the user is logged in by examining the session
        mainViewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                Log.d("UserSession", "Get session: $user")
                // User is logged in, proceed with loading stories
                mainViewModel.fetchStories(user.token)

            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fab.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(ContextCompat.getColor(this, R.color.dark_green)) // Set your desired color here
        )

        // Optionally, change the title text color (if ActionBar is visible)
        supportActionBar?.setTitle("Beranda")

        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        lifecycleScope.launch {
            mainViewModel.stories.collect { result ->
                result?.onSuccess { listStoryResponse ->
                    setEventData(listStoryResponse.listStory)
                }?.onFailure {
                    // Handle the error
                }
            }
        }

        checkUserSession()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_stories, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                mainViewModel.logout()
                mainViewModel.isLoggedOut.observe(this) { isLoggedOut ->
                    if (isLoggedOut) {
                        showLoading(true)
                        Toast.makeText(this, "Logout Success", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}