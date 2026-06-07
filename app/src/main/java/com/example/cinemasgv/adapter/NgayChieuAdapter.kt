package com.example.cinemasgv.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cinemasgv.databinding.ItemNgayChieuBinding
import com.example.cinemasgv.model.NgayChieu

class NgayChieuAdapter(
    private val ds: List<NgayChieu>, private val onClick: (NgayChieu) -> Unit) : RecyclerView.Adapter<NgayChieuAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ItemNgayChieuBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding = ItemNgayChieuBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return ViewHolder(binding)
    }

    override fun getItemCount() = ds.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = ds[position]
        holder.binding.txtNgay.text =
            item.ngay.takeLast(2)

        if(item.duocChon){
            holder.binding.txtNgay.setTextColor(
                Color.RED
            )

        }else{
            holder.binding.txtNgay.setTextColor(
                Color.BLACK
            )
        }

        holder.itemView.setOnClickListener {

            onClick(item)
        }
    }
}