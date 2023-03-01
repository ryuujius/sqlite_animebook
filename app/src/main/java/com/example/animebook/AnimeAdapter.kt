package com.example.animebook

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.animebook.databinding.RecyclerRowBinding

class AnimeAdapter(val artList : ArrayList<Anime>) : RecyclerView.Adapter<AnimeAdapter.AnimeHolder>() {
    class AnimeHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AnimeHolder(binding)
    }

    override fun onBindViewHolder(holder: AnimeHolder, position: Int) {
        holder.binding.recyclerViewTextView.text = artList.get(position).name
        holder.itemView.setOnClickListener(){
            val intent = Intent(holder.itemView.context, AnimeActivity::class.java)
            intent.putExtra("info", "old")
            intent.putExtra("id",artList.get(position).id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return artList.size
    }
}