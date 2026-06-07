package com.example.cinemasgv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cinemasgv.databinding.ItemLienheUserBinding
import com.example.cinemasgv.model.LienHe

class LienHeUserAdapter(

    private val ds: List<LienHe>,

    private val onClick: (LienHe)->Unit

) : RecyclerView.Adapter<LienHeUserAdapter.ViewHolder>(){

    inner class ViewHolder(
        val binding: ItemLienheUserBinding) :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType:Int
    ):ViewHolder{ return ViewHolder(
            ItemLienheUserBinding
                .inflate(LayoutInflater
                        .from(parent.context),
                    parent,
                    false
                )
        )
    }

    override fun getItemCount()=
        ds.size

    override fun onBindViewHolder(
        holder:ViewHolder,
        position:Int){
        val item= ds[position]

        holder.binding
            .txtTieuDe
            .text= item.tieuDe

        holder.binding.txtTrangThai
            .text= item.trangThai

        holder.binding
            .txtThoiGian
            .text= item.thoiGian?.toDate()?.toString()

        holder.itemView.setOnClickListener{
                onClick(item)
            }
    }

}