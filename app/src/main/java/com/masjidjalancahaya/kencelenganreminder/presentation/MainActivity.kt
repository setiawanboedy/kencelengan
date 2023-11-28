package com.masjidjalancahaya.kencelenganreminder.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.masjidjalancahaya.kencelenganreminder.adapter.KencelenganAdapter
import com.masjidjalancahaya.kencelenganreminder.core.ResourceState
import com.masjidjalancahaya.kencelenganreminder.databinding.ActivityMainBinding
import com.masjidjalancahaya.kencelenganreminder.model.KencelenganModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private val viewModel: KencelenganViewModel by viewModels()
    private lateinit var kencelengAdapter: KencelenganAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.floatingActionButton.setOnClickListener {
            val moveIntent = Intent(this@MainActivity, AddActivity::class.java)
            startActivity(moveIntent)
        }

        viewModel.getKencelengans()
        initDataProduct()

        initRecyclerView()

        kencelengAdapter.setOnItemClick { data ->
            val moveWithObjectIntent = Intent(this@MainActivity, AddActivity::class.java)
            moveWithObjectIntent.putExtra(AddActivity.DATA_KEY, data)
            startActivity(moveWithObjectIntent)
        }
    }

    private fun initDataProduct(){
        viewModel.allKencelengan.observe(this) {items->
            when(items){
                is ResourceState.Loading -> {

                }
                is ResourceState.Success ->{
                    items.data?.let { onGetListKencelengans(it) }
                }
                else -> {}
            }

        }

    }

    private fun onGetListKencelengans(items: List<KencelenganModel>){

        kencelengAdapter.submitList(items)
    }

    private fun initRecyclerView(){
        kencelengAdapter = KencelenganAdapter()
        binding.rvList.apply {
            adapter = kencelengAdapter
            layoutManager = LinearLayoutManager(applicationContext)
        }
    }
}