package com.example.cinemasgv

import android.app.AlertDialog
import android.app.DatePickerDialog // Đã thêm import
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cinemasgv.adapter.MovieAdminAdapter
import com.example.cinemasgv.databinding.QuanLiPhimAdminBinding
import com.example.cinemasgv.model.Phim
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.Calendar // Đã thêm import

class QuanLyPhimActivity : AppCompatActivity() {

    private lateinit var binding: QuanLiPhimAdminBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var movieAdapter: MovieAdminAdapter
    private val movieList = mutableListOf<Phim>()
    private var phimDangSua: Phim? = null

    private var movieListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = QuanLiPhimAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupClick()
        setupDatePicker() // Gọi hàm thiết lập lịch chọn ngày

        // Bắt đầu lắng nghe dữ liệu thay đổi từ Firebase
        loadMovies()
    }

    // --- MỚI: Xử lý chọn lịch ngày chiếu ---
    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()

        binding.tvNgayKhoiChieu.setOnClickListener {
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    // Format ngày theo chuẩn dd/MM/yyyy
                    val date = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    binding.tvNgayKhoiChieu.text = date
                },
                currentYear,
                currentMonth,
                currentDay
            ).show()
        }
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdminAdapter(
            movieList = movieList,
            onMovieClick = { movie ->
                fillDataToForm(movie)
            },
            onEditClick = { movie ->
                fillDataToForm(movie)
            },
            onDeleteClick = { movie ->
                xoaPhim(movie)
            }
        )

        binding.rvDanhSachPhim.apply {
            layoutManager = LinearLayoutManager(this@QuanLyPhimActivity)
            adapter = movieAdapter
        }
    }

    private fun setupClick() {
        binding.btnThem.setOnClickListener { themPhim() }
        binding.btnSua.setOnClickListener { suaPhim() }
        binding.btnLamMoi.setOnClickListener { clearForm() }
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun loadMovies() {
        movieListener = db.collection("movies")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(this, "Lỗi tải dữ liệu: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    movieList.clear()
                    for (doc in snapshots) {
                        val movie = doc.toObject(Phim::class.java)
                        movieList.add(movie)
                    }
                    movieAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun themPhim() {
        val movie = getMovieFromForm()

        if (movie.maPhim.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mã phim", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("movies")
            .document(movie.maPhim)
            .set(movie)
            .addOnSuccessListener {
                Toast.makeText(this, "Thêm phim thành công", Toast.LENGTH_SHORT).show()
                clearForm()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Thêm thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun suaPhim() {
        val movie = getMovieFromForm()

        if (movie.maPhim.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn phim cần sửa", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Xác nhận sửa")
            .setMessage("Bạn có chắc chắn muốn lưu các thay đổi cho phim này không?")
            .setPositiveButton("Có") { _, _ ->
                // Nếu bấm Có thì mới bắt đầu đẩy dữ liệu lên Firebase
                db.collection("movies")
                    .document(movie.maPhim)
                    .set(movie)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Sửa phim thành công", Toast.LENGTH_SHORT).show()
                        clearForm()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Sửa thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Không", null) // Bấm Không thì tắt hộp thoại, không làm gì cả
            .show()
    }

    private fun xoaPhim(movie: Phim) {
        AlertDialog.Builder(this)
            .setTitle("Xóa phim")
            .setMessage("Bạn có chắc muốn xóa phim: ${movie.tenPhim}?")
            .setPositiveButton("Xóa") { _, _ ->
                db.collection("movies")
                    .document(movie.maPhim)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Đã xóa phim thành công", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Xóa thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun fillDataToForm(movie: Phim) {
        phimDangSua = movie

        binding.edtMaPhim.setText(movie.maPhim)
        binding.edtTenPhim.setText(movie.tenPhim)
        binding.edtMoTa.setText(movie.moTa)

        // CẬP NHẬT: Load thêm dữ liệu lên Form
        binding.edtTheLoai.setText(movie.theLoai.joinToString(", "))
        binding.edtDaoDien.setText(movie.daoDien)
        binding.edtDienVien.setText(movie.dienVien.joinToString(", "))
        binding.edtDanhGia.setText(movie.danhGia.toString())
        binding.edtThoiLuong.setText(movie.thoiLuong.toString())
        binding.edtTuoi.setText(movie.doTuoi.toString())
        binding.edtNgonNgu.setText(movie.ngonNgu)
        binding.edtUrlAnh.setText(movie.hinhAnh)

        // TextView ngày chiếu
        binding.tvNgayKhoiChieu.text = movie.ngayKhoiChieu

        // Switch trạng thái
        binding.swTrangThai.isChecked = movie.trangThai
        binding.swYeuThich.isChecked = movie.yeuThich
    }

    private fun getMovieFromForm(): Phim {
        val theLoai = binding.edtTheLoai.text.toString()
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        // CẬP NHẬT: Xử lý chuỗi diễn viên sang List giống như Thể loại
        val dienVien = binding.edtDienVien.text.toString()
            .split(",") // Cắt chuỗi mỗi khi gặp dấu phẩy
            .map { it.trim() } // Xóa khoảng trắng thừa ở đầu/cuối tên
            .filter { it.isNotEmpty() } // Bỏ qua các tên rỗng

        return Phim(
            maPhim = binding.edtMaPhim.text.toString().trim(),
            tenPhim = binding.edtTenPhim.text.toString().trim(),
            moTa = binding.edtMoTa.text.toString().trim(),
            theLoai = theLoai,
            thoiLuong = binding.edtThoiLuong.text.toString().toIntOrNull() ?: 0,
            ngayKhoiChieu = binding.tvNgayKhoiChieu.text.toString(),
            hinhAnh = binding.edtUrlAnh.text.toString().trim(),

            // CẬP NHẬT: Gắn các trường bị thiếu
            ngonNgu = binding.edtNgonNgu.text.toString().trim(),
            doTuoi = binding.edtTuoi.text.toString().toIntOrNull() ?: 0,
            daoDien = binding.edtDaoDien.text.toString().trim(),
            dienVien = dienVien,

            // Giữ lại đánh giá cũ nếu đang sửa, nếu tạo mới mặc định là 0.0
            // Lấy điểm bạn nhập, nếu lỡ để trống thì mặc định là 0.0
            danhGia = binding.edtDanhGia.text.toString().toDoubleOrNull() ?: 0.0,

            trangThai = binding.swTrangThai.isChecked,
            yeuThich = binding.swYeuThich.isChecked
        )
    }

    private fun clearForm() {
        binding.edtMaPhim.text.clear()
        binding.edtTenPhim.text.clear()
        binding.edtMoTa.text.clear()
        binding.edtTheLoai.text?.clear()

        // CẬP NHẬT: Xóa trắng thêm các trường mới
        binding.edtDaoDien.text.clear()
        binding.edtDienVien.text.clear()
        binding.edtThoiLuong.text.clear()
        binding.edtTuoi.text.clear()
        binding.edtNgonNgu.text.clear()
        binding.edtDanhGia.text.clear()

        binding.edtUrlAnh.text.clear()
        binding.tvNgayKhoiChieu.text = "dd/mm/yyyy"
        binding.swTrangThai.isChecked = false
        binding.swYeuThich.isChecked = false

        phimDangSua = null
    }

    override fun onDestroy() {
        super.onDestroy()
        movieListener?.remove()
    }
}
