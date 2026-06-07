package com.example.cinemasgv.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.cinemasgv.R
import com.example.cinemasgv.databinding.ItemLichChieuLayoutBinding
import com.example.cinemasgv.model.CumRap

class LichChieuAdapter(

    private val ds: List<CumRap>
) : RecyclerView.Adapter<LichChieuAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemLichChieuLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemLichChieuLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = ds.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val cumRap = ds[position]

        holder.binding.txtCumRap.text =
            cumRap.tenCumRap

        holder.binding.txtInfo.text =
            "Số suất: ${cumRap.dsLichChieu.size}"

        holder.binding.layoutTime.removeAllViews()

        cumRap.dsLichChieu.forEach { lich ->

            val tv = TextView(holder.itemView.context)

            tv.text = lich.gioBatDau

            tv.textSize = 15f

            tv.setTextColor(
                android.graphics.Color.parseColor("#D32F2F")
            )

            tv.setBackgroundResource(
                R.drawable.bg_time
            )

            tv.setPadding(
                32,
                16,
                32,
                16
            )

            val params =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            params.marginEnd = 12
            params.bottomMargin = 8

            tv.layoutParams = params

            tv.isClickable = true
            tv.isFocusable = true

            tv.setOnClickListener {
/*
tv.setOnClickListener {

    val intent =
        Intent(
            holder.itemView.context,
            DatVeActivity::class.java
        )

    intent.putExtra(
        "lichChieuId",
        lich.id
    )

    intent.putExtra(
        "tenCumRap",
        lich.tenCumRap
    )

    intent.putExtra(
        "tenPhong",
        lich.tenPhong
    )

    intent.putExtra(
        "gioBatDau",
        lich.gioBatDau
    )

    intent.putExtra(
        "giaVe",
        lich.giaVe
    )

    holder.itemView.context
        .startActivity(intent)
}
 */
                Toast.makeText(
                    holder.itemView.context,
                    "Đã chọn suất ${lich.gioBatDau}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            holder.binding.layoutTime.addView(tv)

        }
    }

}