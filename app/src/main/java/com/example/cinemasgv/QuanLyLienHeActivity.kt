package com.example.cinemasgv

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cinemasgv.adapter.LienHeAdminAdapter
import com.example.cinemasgv.databinding.LienheAdminLayoutBinding
import com.example.cinemasgv.model.LienHe
import com.google.firebase.firestore.FirebaseFirestore

class QuanLyLienHeActivity : AppCompatActivity() {
    private lateinit var binding: LienheAdminLayoutBinding
    private lateinit var adapter: LienHeAdminAdapter
    private val ds = mutableListOf<LienHe>()
    // Danh sách gốc để phục vụ tìm kiếm
    private val dsGoc = mutableListOf<LienHe>()
    private var lienHeListener: com.google.firebase.firestore.ListenerRegistration? = null
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LienheAdminLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecycler()

        loadLienHe()

        binding.btnBack.setOnClickListener {
            finish()
        }
        setupSearch()
    }

    private fun setupRecycler() {
        adapter = LienHeAdminAdapter(ds) { lienHe ->
            val intent = Intent(
                    this,
                    TraLoiLienHeActivity::class.java
                )

            // truyền ID document liên hệ
            intent.putExtra(
                "userID",
                lienHe.id
            )
            startActivity(intent)
        }

        binding.rcvLienHe.layoutManager = LinearLayoutManager(this)
        binding.rcvLienHe.adapter = adapter
    }

    private fun loadLienHe() {
        // Sử dụng addSnapshotListener thay vì get() để lắng nghe realtime
        lienHeListener = db.collection("LienHe")
            // Bạn có thể sắp xếp theo thời gian mới nhất lên đầu nếu muốn
            .orderBy("thoiGian", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FIREBASE_REALTIME", "Lắng nghe thất bại: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    ds.clear()
                    dsGoc.clear()

                    for (doc in snapshot.documents) {
                        try {
                            val item = doc.toObject(LienHe::class.java)
                            if (item != null) {
                                item.id = doc.id
                                ds.add(item)
                                dsGoc.add(item)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    // Nếu người dùng đang gõ tìm kiếm thì vẫn giữ nguyên bộ lọc, ngược lại thì hiện hết
                    val query = binding.searchUser.query.toString()
                    if (query.isNotBlank()) {
                        timKiemTheoUserID(query)
                    } else {
                        adapter.notifyDataSetChanged()
                    }
                }
            }
    }

    private fun setupSearch() {
        binding.searchUser
            .setOnQueryTextListener(
                object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        timKiemTheoUserID(
                            query ?: ""
                        )
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        timKiemTheoUserID(
                            newText ?: ""
                        )
                        return true
                    }
                }
            )
    }

    override fun onDestroy() {
        super.onDestroy()
        lienHeListener?.remove()
    }

    private fun timKiemTheoUserID(keyword: String) {
        ds.clear()
        if (keyword.isBlank()) {
            ds.addAll(dsGoc)
        } else {
            val ketQua = dsGoc.filter {
                it.userID.contains(
                    keyword,
                    ignoreCase = true
                )
            }

            ds.addAll(ketQua)
        }

        adapter.notifyDataSetChanged()
    }
}