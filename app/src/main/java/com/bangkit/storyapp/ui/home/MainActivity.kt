package com.bangkit.storyapp.ui.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.storyapp.R
import com.bangkit.storyapp.adapter.StoryAdapter
import com.bangkit.storyapp.ui.home.ViewModelFactory
import com.bangkit.storyapp.data.model.ListStoryItem
import com.bangkit.storyapp.data.model.LoginResult
import com.bangkit.storyapp.data.model.UserLogin
import com.bangkit.storyapp.data.preference.SettingPreference
import com.bangkit.storyapp.databinding.ActivityMainBinding
import com.bangkit.storyapp.ui.home.HomeViewModel
import com.bangkit.storyapp.ui.setting.SettingActivity
import com.bangkit.storyapp.ui.upload.UploadStoryActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var userLogin: UserLogin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        homeViewModel = ViewModelProvider(this, ViewModelFactory(SettingPreference.getInstance(dataStore), this))[HomeViewModel::class.java]
        homeViewModel.getUser().observe(this) {
            userLogin = UserLogin(it.name, it.userId, it.email, it.password, it.token, true)

            homeViewModel.getStories(userLogin.token).observe(this) {
                adapter.submitData(lifecycle, it)
            }
        }

        homeViewModel.isLoading.observe(this) {
            showLoading(it, binding.progressBar)
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager

//        val listStory = ArrayList<ListStoryItem>()

        adapter = StoryAdapter()
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

//        homeViewModel.getUser().observe(this) {
//            if (it != null) {
//                homeViewModel.getStories(it.token)
//                fun setDataStory(users: List<ListStoryItem>) {
//                    setStories(users)
//                }
//
//            }
//        }

//        homeViewModel.getUser().observe(this) {
//            if (it != null) {
//                homeViewModel.getStories(it.token)
//                fun setDataStory(users: PagingData<ListStoryItem>) {
//                    adapter.submitData(lifecycle, users)
//                }
//            }
//        }

//        homeViewModel.getAllStories.observe(this) {
//            setStories(it)
//        }


        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, UploadStoryActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return true
    }

//    private fun setStories(users: List<ListStoryItem>) {
//        val listStory = ArrayList<ListStoryItem>()
//        for (user in users) {
//            listStory.addAll(users)
//        }
//        adapter = StoryAdapter(listStory)
//        binding.rvStories.adapter = adapter
//    }

    private fun showLoading(isLoading: Boolean, view: View) {
        if (isLoading) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }

    companion object {
        const val NAME = "name"
    }
}