package com.example.cinemasgv

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cinemasgv.adapter.MovieAdminAdapter
import com.example.cinemasgv.databinding.QuanlyphimLayoutBinding
import com.example.cinemasgv.model.MovieAdmin
import com.google.firebase.firestore.FirebaseFirestore

class QuanLyPhimActivity : AppCompatActivity() {
    private lateinit var binding: QuanlyphimLayoutBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var movieAdapter: MovieAdminAdapter
    private val movieList = mutableListOf<MovieAdmin>()
    private val movieListGoc = mutableListOf<MovieAdmin>()
    private var isEditing = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = QuanlyphimLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecycler()
        loadMovie()
        binding.searchMovie
            .setOnQueryTextListener(
                object :
                    SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        timKiem(query ?: "")
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        timKiem(newText ?: "")
                        return true
                    }
                }
            )
        binding.btnThem.setOnClickListener {
                if(isEditing){
                    toast(
                        "Đang sửa phim, không thể thêm"
                    )
                    return@setOnClickListener
                }
                themPhim()
            }

        binding.btnSua.setOnClickListener {
                xacNhanSua()
            }
        binding.btnXoa.setOnClickListener {
                xacNhanXoa()
            }
    }

    private fun setupRecycler(){
        binding.rcvMovie.layoutManager = LinearLayoutManager(this)

        movieAdapter =
            MovieAdminAdapter(movieList){ movie ->
                binding.edtMaPhim
                    .setText(movie.maPhim)
                binding.edtTenPhim
                    .setText(movie.tenPhim)
                binding.edtDaoDien.setText(movie.daoDien)
                binding.edtDanhGia.setText(movie.danhGia.toString())
                binding.edtThoiLuong.setText(movie.thoiLuong.toString())
                binding.edtHinhAnh.setText(movie.hinhAnh)
                binding.edtMaPhim.isEnabled = false
                isEditing = true
            }
        binding.rcvMovie.adapter = movieAdapter
    }

    private fun loadMovie(){
        db.collection("movies")
            .get()
            .addOnSuccessListener {
                movieList.clear()
                movieListGoc.clear()
                for(doc in it){
                    try{
                        val movie = doc.toObject(MovieAdmin::class.java)
                        movieList.add(movie)
                        movieListGoc.add(movie)
                    }

                    catch(e:Exception){

                        e.printStackTrace()
                    }
                }
                android.util.Log.d(
                    "MOVIE_SIZE",
                    movieList.size.toString()
                )

                movieAdapter.notifyDataSetChanged()
            }
    }
    private fun timKiem(keyword: String){
        movieList.clear()
        if(keyword.isBlank()){
            movieList.addAll(
                movieListGoc
            )

        }else{
            movieList.addAll(
                movieListGoc.filter {
                    it.maPhim.contains(
                        keyword,
                        true
                    ) || it.tenPhim.contains(
                                keyword,
                                true
                            )
                }
            )
        }

        movieAdapter.notifyDataSetChanged()
    }

    private fun validate(): Boolean{
        val maPhim = binding.edtMaPhim.text.toString().trim()
        val tenPhim = binding.edtTenPhim.text.toString().trim()
        val daoDien = binding.edtDaoDien.text.toString().trim()
        val danhGia = binding.edtDanhGia.text.toString().trim()
        val thoiLuong = binding.edtThoiLuong.text.toString().trim()

        if(maPhim.isEmpty()){
            toast("Nhập mã phim")
            return false
        }
        if(tenPhim.isEmpty()){
            toast("Nhập tên phim")
            return false
        }

        if(daoDien.isEmpty()){
            toast("Nhập đạo diễn")
            return false
        }

        if(danhGia.isEmpty()){
            toast("Nhập đánh giá")
            return false
        }

        if(danhGia.toDoubleOrNull() == null){
            toast("Đánh giá phải là số thực")
            return false
        }

        if(thoiLuong.isEmpty()){
            toast("Nhập thời lượng")
            return false
        }

        if(thoiLuong.toIntOrNull() == null){
            toast("Thời lượng phải là số")
            return false
        }
        return true
    }

    private fun themPhim(){
        if(!validate()){
            return
        }

        val movie = MovieAdmin(
                maPhim = binding.edtMaPhim.text.toString(),
                tenPhim = binding.edtTenPhim.text.toString(),
                daoDien = binding.edtDaoDien.text.toString(),

                danhGia = binding.edtDanhGia.text.toString().toDouble(),
                thoiLuong = binding.edtThoiLuong.text.toString().toInt(),
                hinhAnh = binding.edtHinhAnh.text.toString()

            )

        db.collection("movies")
            .document(movie.maPhim)
            .set(movie)
            .addOnSuccessListener {
                toast("Thêm thành công")
                clearInput()
                loadMovie()
            }
    }

    private fun xacNhanSua(){

        AlertDialog.Builder(this)

            .setTitle("Xác nhận")
            .setMessage("Bạn có muốn sửa phim không?")
            .setPositiveButton("Có"){_,_->
                suaPhim()
            }

            .setNegativeButton("Không", null)
            .show()
    }

    private fun suaPhim(){
        if(!validate()){
            return
        }

        val maPhim = binding.edtMaPhim.text.toString()
        db.collection("movies")
            .document(maPhim)
            .update(
                "tenPhim",
                binding.edtTenPhim.text
                    .toString(),
                "daoDien",
                binding.edtDaoDien.text
                    .toString(),
                "danhGia",
                binding.edtDanhGia.text
                    .toString()
                    .toDouble(),
                "thoiLuong",
                binding.edtThoiLuong.text
                    .toString()
                    .toInt(),
                "hinhAnh",
                binding.edtHinhAnh.text.toString()
            )

            .addOnSuccessListener{
                toast("Sửa thành công")
                clearInput()
                loadMovie()
                isEditing = false
                binding.edtMaPhim.isEnabled = true

            }
    }

    private fun xacNhanXoa(){
        AlertDialog.Builder(this)
            .setTitle("Xác nhận")
            .setMessage("Bạn muốn xóa phim?")
            .setPositiveButton(
                "Xóa"
            ){_,_->
                xoaPhim()
            }
            .setNegativeButton("Hủy",
                null
            ).show()
    }

    private fun xoaPhim(){
        val maPhim = binding.edtMaPhim.text.toString()
        db.collection("movies")
            .document(maPhim)
            .delete()
            .addOnSuccessListener{
                toast("Xóa thành công")
                clearInput()
                loadMovie()
            }
    }

    private fun clearInput(){
        binding.edtMaPhim.isEnabled = true
        isEditing = false
        binding.edtMaPhim.text?.clear()
        binding.edtTenPhim.text?.clear()
        binding.edtDaoDien.text?.clear()
        binding.edtDanhGia.text?.clear()
        binding.edtThoiLuong.text?.clear()
        binding.edtHinhAnh.text?.clear()
    }

    private fun toast(msg:String){
        Toast.makeText(
            this,
            msg,
            Toast.LENGTH_SHORT).show()
    }
}