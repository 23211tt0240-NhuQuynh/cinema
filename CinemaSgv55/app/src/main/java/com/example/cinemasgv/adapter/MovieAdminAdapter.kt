package com.example.cinemasgv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cinemasgv.R
import com.example.cinemasgv.databinding.ItemMovieAdminBinding
import com.example.cinemasgv.model.MovieAdmin
import com.squareup.picasso.Picasso

class MovieAdminAdapter(

    private val movieList: List<MovieAdmin>,
    private val onMovieClick: (MovieAdmin) -> Unit) : RecyclerView.Adapter<MovieAdminAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMovieAdminBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(

        parent: ViewGroup,
        viewType: Int

    ): ViewHolder {

        val binding = ItemMovieAdminBinding.inflate(

            LayoutInflater.from(parent.context),
            parent,
            false

        )

        return ViewHolder(binding)

    }

    override fun getItemCount(): Int {

        return movieList.size

    }

    override fun onBindViewHolder(

        holder: ViewHolder,
        position: Int

    ) {
        val movie = movieList[position]
        holder.binding.txtTenPhim.text = movie.tenPhim
        holder.binding.txtDaoDien.text = "Đạo diễn: ${movie.daoDien}"
        holder.binding.txtThongTin.text = "${movie.thoiLuong} phút ⭐ ${movie.danhGia}"

        Picasso.get()
            .load(movie.hinhAnh)
            .resize(300,400)
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(holder.binding.imgMovie)

        holder.itemView.setOnClickListener {
            onMovieClick(movie)

        }

    }

}