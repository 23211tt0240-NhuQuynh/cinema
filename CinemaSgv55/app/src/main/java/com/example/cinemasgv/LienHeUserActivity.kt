package com.example.cinemasgv

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cinemasgv.adapter.LienHeUserAdapter
import com.example.cinemasgv.databinding.LienheUserLayoutBinding
import com.example.cinemasgv.model.LienHe
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class LienHeUserActivity : AppCompatActivity() {

    private lateinit var binding: LienheUserLayoutBinding
    private val db = FirebaseFirestore.getInstance()
    private val ds = mutableListOf<LienHe>()
    private lateinit var adapter: LienHeUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LienheUserLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecycler()

        loadLienHe()

        binding.btnGui.setOnClickListener {
            guiLienHe()
        }
    }

    private fun setupRecycler() {

        adapter = LienHeUserAdapter(ds) { item ->

            val intent = Intent(
                this,
                PhanHoiChoUserActivity::class.java
            )

            // truyền ID document liên hệ
            intent.putExtra("id", item.id)

            startActivity(intent)
        }

        binding.rcvLienHe.layoutManager = LinearLayoutManager(this)
        binding.rcvLienHe.adapter = adapter
    }

    private fun loadLienHe() {
        val currentUserId = intent.getStringExtra("userID") ?: return
        db.collection("LienHe")
            .whereEqualTo("userID", currentUserId)
            .get()
            .addOnSuccessListener { result ->
                ds.clear()
                for (doc in result) {
                    val item = doc.toObject(LienHe::class.java)
                    item.id = doc.id
                    ds.add(item)
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun guiLienHe() {

        val tieuDe = binding.edtTieuDe.text.toString().trim()

        val noiDung = binding.edtNoiDung.text.toString().trim()

        if (tieuDe.isEmpty()) {
            toast("Nhập tiêu đề")
            return
        }

        if (noiDung.isEmpty()) {
            toast("Nhập nội dung")
            return
        }

        val currentUserId = intent.getStringExtra("userID") ?: ""

        val data = hashMapOf(
            "userID" to currentUserId,
            "tieuDe" to tieuDe,
            "noiDung" to noiDung,
            "adminTraLoi" to "",
            "trangThai" to "Chưa phản hồi",
            "thoiGian" to Timestamp.now()
        )

        db.collection("LienHe")
            .add(data)
            .addOnSuccessListener {
                toast("Gửi thành công")
                binding.edtTieuDe.text?.clear()
                binding.edtNoiDung.text?.clear()
                loadLienHe()
            }
            .addOnFailureListener {
                toast("Gửi thất bại")
            }
    }

    private fun toast(msg: String) {

        Toast.makeText(
            this,
            msg,
            Toast.LENGTH_SHORT).show()
    }
}