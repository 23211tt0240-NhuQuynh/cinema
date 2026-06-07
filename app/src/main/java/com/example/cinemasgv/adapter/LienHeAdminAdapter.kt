package com.example.cinemasgv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cinemasgv.databinding.ItemLienHeAdminLayoutBinding
import com.example.cinemasgv.model.LienHe
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class LienHeAdminAdapter(

    private val ds: List<LienHe>,

    private val onClick: (LienHe)->Unit) :RecyclerView.Adapter<LienHeAdminAdapter.ViewHolder>(){

    inner class ViewHolder(val binding: ItemLienHeAdminLayoutBinding) :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(

        parent: ViewGroup,

        viewType:Int):ViewHolder{
        val binding= ItemLienHeAdminLayoutBinding.inflate(LayoutInflater.from(
                            parent.context),
                    parent,
                    false)

        return ViewHolder(
            binding
        )

    }

    override fun getItemCount()=
        ds.size

    override fun onBindViewHolder(
        holder:ViewHolder,
        position:Int){
        val item=
            ds[position]
        holder.binding
            .txtTieuDe
            .text= item.tieuDe



        if (ds[position].trangThai == true) {
            holder.binding.txtTrangThai.text = "Đã phản hồi"
        }
        else {
            holder.binding.txtTrangThai.text = "Chưa phản hồi"
        }

        holder.binding
            .txtNoiDung
            .text= item.noiDung

        val sdf=

            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        holder.binding
            .txtThoiGian
            .text= item.thoiGian?.toDate()?.let{
                sdf.format(it) } ?:"Không rõ"

        holder.binding
            .txtNguoiGui
            .text= "Đang tải..."

        if(item.userID.isNotEmpty()){

            FirebaseFirestore.getInstance()

                .collection(
                    "Accounts")

                .document(
                    item.userID)

                .get()
                .addOnSuccessListener{
                    holder.binding
                        .txtNguoiGui
                        .text=
                        it.getString(
                            "hoTen") ?:"Không rõ"
                }

                .addOnFailureListener{
                    holder.binding
                        .txtNguoiGui
                        .text= "Lỗi"
                }
        }

        else{
            holder.binding
                .txtNguoiGui
                .text= "Thiếu user"

        }

        holder.itemView
            .setOnClickListener{
                onClick(item)
            }

    }

}