package com.example.animebook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animebook.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var animeList: ArrayList<Anime>
    private lateinit var animeAdapter: AnimeAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        animeList = ArrayList<Anime>()
        animeAdapter = AnimeAdapter(animeList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = animeAdapter

        try {

            val database = this.openOrCreateDatabase("Animes", MODE_PRIVATE,null)
            val cursor = database.rawQuery("select * from animes", null)

            val idIx = cursor.getColumnIndex("id")
            val nameIx = cursor.getColumnIndex("animename")

            while (cursor.moveToNext()){
                val name = cursor.getString(nameIx)
                val id = cursor.getInt(idIx)
                val anime = Anime(name, id)
                animeList.add(anime)
            }
            animeAdapter.notifyDataSetChanged()
            cursor.close()

        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //inflater
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_anime, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_anime){
            val intent = Intent(this@MainActivity, AnimeActivity::class.java)
            intent.putExtra("info", "new")
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}