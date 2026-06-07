package com.example.cinemasgv

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cinemasgv.adapter.LichChieuAdapter
import com.example.cinemasgv.adapter.NgayChieuAdapter
import com.example.cinemasgv.databinding.LichChieuLayoutBinding
import com.example.cinemasgv.model.CumRap
import com.example.cinemasgv.model.LichChieu
import com.example.cinemasgv.model.NgayChieu
import com.google.firebase.firestore.FirebaseFirestore

class LichChieuActivity : AppCompatActivity() {

    private lateinit var binding: LichChieuLayoutBinding
    private lateinit var lichChieuAdapter: LichChieuAdapter
    private lateinit var ngayAdapter: NgayChieuAdapter

    private val db = FirebaseFirestore.getInstance()
    private val dsCumRap = mutableListOf<CumRap>()
    private val dsNgay = mutableListOf<NgayChieu>()

    private var maPhimDangChon = ""
    private var tenPhimDangChon = ""
    private var ngayDangChon = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LichChieuLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        // Nhận dữ liệu thật từ màn hình trước. Nếu chạy test trực tiếp (Intent null), tự động lấy thông tin phim mặc định.
        maPhimDangChon = intent.getStringExtra("maPhim") ?: "P01"
        tenPhimDangChon = intent.getStringExtra("tenPhim") ?: "PHÍ THÔNG: QUỶ MÁU RỪNG THIÊNG"
/*
val intent = Intent(this, LichChieuActivity::class.java)
intent.putExtra("ma phim",phim.maPhim)
intent.putExtra("ten phim", phim.tenPhim)
startActivity(intent)

 */
        // Hiển thị tên phim lên Toolbar/TextView
        binding.txtTenPhim.text = tenPhimDangChon

        setupRecycler()
        setupNgayRecycler()
        loadNgay()
    }
    /*
    private fun loadMovieFromUser() {

    val uid =
        auth.currentUser?.uid

    if (uid == null) {

        Toast.makeText(
            this,
            "Chưa đăng nhập",
            Toast.LENGTH_SHORT
        ).show()

        return
    }

    db.collection("Account")
        .document(soDienThoai)
        .get()
        .addOnSuccessListener { result ->

            if (result.isEmpty) {

                Toast.makeText(
                    this,
                    "User chưa chọn phim",
                    Toast.LENGTH_SHORT
                ).show()

                return@addOnSuccessListener
            }

            val doc = result.documents[0]

            maPhimDangChon =
                doc.getString("maPhim") ?: ""

            tenPhimDangChon =
                doc.getString("tenPhim") ?: ""

            binding.txtTenPhim.text =
                tenPhimDangChon

            loadNgay()
        }
        .addOnFailureListener {

            Toast.makeText(
                this,
                "Lỗi lấy dữ liệu user",
                Toast.LENGTH_SHORT
            ).show()
        }
}
     */

    private fun setupRecycler() {
        lichChieuAdapter = LichChieuAdapter(dsCumRap)
        binding.rcvLichChieu.layoutManager = LinearLayoutManager(this)
        binding.rcvLichChieu.adapter = lichChieuAdapter
    }

    private fun setupNgayRecycler() {
        ngayAdapter = NgayChieuAdapter(dsNgay) { ngay ->
            dsNgay.forEach { it.duocChon = false }
            ngay.duocChon = true
            ngayDangChon = ngay.ngay
            ngayAdapter.notifyDataSetChanged()

            loadLichChieu()
        }

        binding.rcvNgay.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rcvNgay.adapter = ngayAdapter
    }

    private fun loadNgay() {
        dsNgay.clear()

        // Sử dụng maPhimDangChon đã cấu hình ở onCreate để truy vấn đúng phim
        db.collection("LichChieu")
            .whereEqualTo("maPhim", maPhimDangChon)
            .get()
            .addOnSuccessListener { result ->
                val setNgay = mutableSetOf<String>()

                for (doc in result) {
                    val lich = doc.toObject(LichChieu::class.java)
                    setNgay.add(lich.ngayChieu)
                }

                setNgay.sorted().forEachIndexed { index, ngay ->
                    dsNgay.add(
                        NgayChieu(
                            ngay = ngay,
                            duocChon = index == 0
                        )
                    )
                }

                if (dsNgay.isNotEmpty()) {
                    ngayDangChon = dsNgay[0].ngay
                }

                ngayAdapter.notifyDataSetChanged()
                loadLichChieu()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi tải ngày chiếu", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadLichChieu() {
        // Sử dụng maPhimDangChon để lọc suất chiếu, tránh lấy nhầm suất của phim khác
        db.collection("LichChieu")
            .whereEqualTo("maPhim", maPhimDangChon)
            .get()
            .addOnSuccessListener { result ->
                val mapCumRap = mutableMapOf<String, CumRap>()

                for (doc in result) {
                    val lich = doc.toObject(LichChieu::class.java)
                    lich.id = doc.id

                    if (lich.ngayChieu != ngayDangChon) {
                        continue
                    }

                    if (!mapCumRap.containsKey(lich.tenCumRap)) {
                        mapCumRap[lich.tenCumRap] = CumRap(tenCumRap = lich.tenCumRap)
                    }

                    mapCumRap[lich.tenCumRap]?.dsLichChieu?.add(lich)
                }

                dsCumRap.clear()
                dsCumRap.addAll(mapCumRap.values)
                lichChieuAdapter.notifyDataSetChanged()

                if (dsCumRap.isEmpty()) {
                    Toast.makeText(this, "Không có lịch chiếu", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi tải lịch chiếu", Toast.LENGTH_SHORT).show()
            }
    }
}