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

    private var ngayDangChon = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            LichChieuLayoutBinding.inflate(
                layoutInflater
            )

        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }
        /*
        binding.txtTenPhim.text =
    intent.getStringExtra(
        "tenPhim"
    ) ?: "Lịch chiếu phim"
         */

        setupRecycler()

        setupNgayRecycler()

        loadNgay()
    }

    private fun setupRecycler() {

        lichChieuAdapter =
            LichChieuAdapter(dsCumRap)

        binding.rcvLichChieu.layoutManager =
            LinearLayoutManager(this)

        binding.rcvLichChieu.adapter =
            lichChieuAdapter
    }

    private fun setupNgayRecycler() {

        ngayAdapter =
            NgayChieuAdapter(
                dsNgay
            ) { ngay ->

                dsNgay.forEach {
                    it.duocChon = false
                }

                ngay.duocChon = true

                ngayDangChon = ngay.ngay

                ngayAdapter.notifyDataSetChanged()

                loadLichChieu()
            }

        binding.rcvNgay.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        binding.rcvNgay.adapter =
            ngayAdapter
    }

    private fun loadNgay() {
        /*
                val maPhim =
                    intent.getStringExtra("maPhim")
                        ?: return
                        db.collection("LichChieu")
            .whereEqualTo(
                "maPhim",
                maPhim
            )
            .get()
        */
        db.collection("LichChieu")

            .get()
            .addOnSuccessListener { result ->

                val setNgay =
                    mutableSetOf<String>()

                dsNgay.clear()

                for (doc in result) {

                    val lich =
                        doc.toObject(
                            LichChieu::class.java
                        )

                    setNgay.add(
                        lich.ngayChieu
                    )
                }

                setNgay.sorted()
                    .forEachIndexed { index, ngay ->

                        dsNgay.add(
                            NgayChieu(
                                ngay = ngay,
                                duocChon = index == 0
                            )
                        )
                    }

                if (dsNgay.isNotEmpty()) {

                    ngayDangChon =
                        dsNgay[0].ngay
                }

                ngayAdapter.notifyDataSetChanged()

                loadLichChieu()
            }
    }

    private fun loadLichChieu() {
        /*
                val maPhim =
                    intent.getStringExtra("maPhim")
                        ?: return
        */
        db.collection("LichChieu")

            .get()
            .addOnSuccessListener { result ->

                val mapCumRap =
                    mutableMapOf<String, CumRap>()

                for (doc in result) {

                    val lich =
                        doc.toObject(
                            LichChieu::class.java
                        )

                    lich.id = doc.id

                    if (
                        lich.ngayChieu
                        !=
                        ngayDangChon
                    ) {
                        continue
                    }

                    if (
                        !mapCumRap.containsKey(
                            lich.tenCumRap
                        )
                    ) {

                        mapCumRap[
                            lich.tenCumRap
                        ] =
                            CumRap(
                                tenCumRap =
                                    lich.tenCumRap
                            )
                    }

                    mapCumRap[
                        lich.tenCumRap
                    ]?.dsLichChieu?.add(
                        lich
                    )
                }

                dsCumRap.clear()

                dsCumRap.addAll(
                    mapCumRap.values
                )

                lichChieuAdapter.notifyDataSetChanged()

                if (
                    dsCumRap.isEmpty()
                ) {

                    Toast.makeText(
                        this,
                        "Không có lịch chiếu",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Lỗi tải dữ liệu",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

}
