package com.example.cinemasgv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cinemasgv.databinding.ItemPhimAdminBinding
import com.example.cinemasgv.model.Phim
import com.squareup.picasso.Picasso

class MovieAdminAdapter(
    private var movieList: List<Phim>,
    private val onMovieClick: (Phim) -> Unit,
    private val onEditClick: (Phim) -> Unit,
    private val onDeleteClick: (Phim) -> Unit
) : RecyclerView.Adapter<MovieAdminAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ItemPhimAdminBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemPhimAdminBinding.inflate(
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

        holder.binding.apply {
            // Tên phim
            tvItemTenPhim.text = movie.tenPhim

            // Đạo diễn
            tvItemDaoDien.text = "Đạo diễn: ${movie.daoDien}"

            // Thể loại (Chuyển List<String> thành chuỗi phân cách bởi dấu phẩy)
            tvItemTheLoai.text = movie.theLoai.joinToString(", ")

            // Thời lượng
            tvItemThoiLuong.text = "Thời lượng: ${movie.thoiLuong} phút"


            // Trạng thái phát hành
            tvItemStatus.text = if (movie.trangThai) "Đang chiếu" else "Sắp chiếu"

            // Ảnh poster (Thêm kiểm tra rỗng để tránh crash khi link ảnh lỗi hoặc trống)
            if (movie.hinhAnh.isNotBlank()) {
                Picasso.get()
                    .load(movie.hinhAnh)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(imgPoster)
            } else {
                imgPoster.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            // Xử lý sự kiện click vào item
            root.setOnClickListener {
                onMovieClick(movie)
            }

            // Xử lý sự kiện click nút Sửa
            btnEditItem.setOnClickListener {
                onEditClick(movie)
            }

            // Xử lý sự kiện click nút Xóa
            btnDeleteItem.setOnClickListener {
                onDeleteClick(movie)
            }
        }
    }

    // Hàm cập nhật dữ liệu cho Adapter
    fun updateData(newList: List<Phim>) {
        movieList = newList
        notifyDataSetChanged()
    }
}