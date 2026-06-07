package com.example.cinemasgv

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cinemasgv.databinding.TraLoiLienHeLayoutBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class TraLoiLienHeActivity : AppCompatActivity() {
    private lateinit var binding: TraLoiLienHeLayoutBinding
    private val db = FirebaseFirestore.getInstance()
    private var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TraLoiLienHeLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        id = intent.getStringExtra("userID").orEmpty()
        if (id.isEmpty()) {
            toast(
                "Không tìm thấy liên hệ"
            )
            finish()
            return
        }

        binding.btnBack.setOnClickListener {
                finish()
            }

        binding.btnGui.setOnClickListener {
                guiPhanHoi()
            }
        loadData()
    }

    private fun loadData() {
        db.collection(
            "LienHe"
        )
            .document(id)
            .get()
            .addOnSuccessListener { lienHe ->
                if (!lienHe.exists()) {
                    toast(
                        "Không tìm thấy dữ liệu"
                    )
                    return@addOnSuccessListener
                }

                val userID = lienHe.getString("userID") ?: ""
                binding.txtTieuDe.text = lienHe.getString("tieuDe") ?: ""
                binding.txtNoiDung.text = lienHe.getString("noiDung") ?: ""
                binding.edtPhanHoi.setText(lienHe.getString("adminTraLoi") ?: "")
                val sdf = SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        Locale.getDefault()
                    )

                binding.txtThoiGian.text =
                    "Gửi lúc: " + (lienHe.getTimestamp("thoiGian")?.toDate()
                                        ?.let { sdf.format(it) } ?: "Không rõ")

                if (userID.isNotEmpty()) {
                    db.collection("Accounts")
                        .document(userID)
                        .get()
                        .addOnSuccessListener {
                            binding.txtNguoiGui.text =
                                "Người gửi: " + (it.getString("hoTen") ?: "Không rõ")
                        }
                }

                val trangThai = lienHe.getString("trangThai") ?: ""
                if (trangThai == "Đã phản hồi") {
                    binding.btnGui.text = "Đã phản hồi"
                    binding.btnGui.setBackgroundColor(
                            getColor(
                                android.R.color
                                    .holo_red_dark
                            )
                        )
                }
                else {
                    binding.btnGui.text = "Gửi phản hồi"
                    binding.btnGui.setBackgroundColor(
                            getColor(
                                android.R.color.holo_green_dark
                            )
                        )
                }
            }
            .addOnFailureListener {
                toast("Không tải dữ liệu")
            }
    }

    private fun guiPhanHoi() {
        val phanHoi = binding.edtPhanHoi.text.toString().trim()
        if (
            phanHoi.isEmpty()
        ) {
            toast("Nhập phản hồi")
            return
        }

        db.collection("LienHe")
            .document(id)
            .update(
                mapOf(
                    "adminTraLoi" to
                            phanHoi,
                    "trangThai" to
                            "Đã phản hồi"
                )
            )

            .addOnSuccessListener {
                toast("Phản hồi thành công")
                loadData()
            }

            .addOnFailureListener {
                toast("Lỗi: " + (it.message ?: "")
                )
            }
    }

    private fun toast(s: String) {
        Toast.makeText(
            this, s, Toast.LENGTH_SHORT).show()

    }

}