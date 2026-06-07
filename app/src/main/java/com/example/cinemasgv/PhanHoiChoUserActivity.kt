package com.example.cinemasgv

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cinemasgv.databinding.ChiTietPhanHoiUserTuAdminLayoutBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class PhanHoiChoUserActivity : AppCompatActivity(){
    private lateinit var binding: ChiTietPhanHoiUserTuAdminLayoutBinding
    private val db= FirebaseFirestore.getInstance()
    override fun onCreate(
        savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding= ChiTietPhanHoiUserTuAdminLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener{
                finish()
            }

        val id= intent.getStringExtra("userID") ?:return
        loadData(id)
    }

    private fun loadData(id:String){
        db.collection("LienHe")
            .document(id)
            .get()
            .addOnSuccessListener{
                binding.txtTieuDe.text= it.getString("tieuDe")
                binding.txtNoiDung.text= it.getString("noiDung")
                binding.txtPhanHoi.text=
                    it.getString("adminTraLoi")
                        ?.ifBlank {
                            "Admin chưa phản hồi"
                        }

                binding.txtThoiGian.text=
                    it.getTimestamp("thoiGian")?.toDate()?.let{time-> SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(time) } ?: ""
            }
    }
}