package com.example.cinemasgv

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cinemasgv.databinding.MemberLayoutBinding
import com.google.firebase.firestore.FirebaseFirestore

class MemberActivity : AppCompatActivity() {
    private lateinit var binding: MemberLayoutBinding
    private val db = FirebaseFirestore.getInstance()
    private var currentRank = "U22"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MemberLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnDetail.setOnClickListener {
            showPrivilegeDialog(binding.txtPrivilegeTitle.text.toString())
        }

        // ==========================
        // TEST USER
        val sdt = "0123456789"

        loadMember(sdt)

        /*
        // SAU NÀY LOGIN

        val sdt =
            intent.getStringExtra(
                "sdt"
            ) ?: ""

        if(sdt.isEmpty()){

            Toast.makeText(

                this,

                "Không tìm thấy tài khoản",

                Toast.LENGTH_SHORT

            ).show()

            finish()

            return
        }

        loadMember(sdt)
        */

        setupTabs()
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadMember(sdt: String) {
        db.collection("Accounts")
            .document(sdt)
            .get()
            .addOnSuccessListener {
                if(!it.exists()){
                    Toast.makeText(
                        this,
                        "Không tìm thấy thành viên",
                        Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val member = it.toObject(Member::class.java)

                member?.let {

                    // HỌ TÊN
                    binding.txtName.text = it.hoTen

                    // ĐIỂM
                    binding.txtPoint.text = "${it.diem} điểm"

                    // TỔNG CHI
                    binding.txtTongChi.text = "${formatMoney(it.tongChi)} đ"

                    // TỔNG VÉ
                    binding.txtTongVe.text = "${it.tongVe} vé"

                    // HẠNG USER
                    currentRank = tinhHang(it.diem)
                    binding.txtRank.text = currentRank
                    binding.progressMember.max = 3000
                    binding.progressMember.progress = it.diem

                    // AUTO CHỌN TAB HẠNG HIỆN TẠI
                    selectTab(currentRank)
                }
            }

            .addOnFailureListener { Toast.makeText(
                    this,
                    "Load thất bại",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }
    private fun setupTabs() {

        binding.tabU22.setOnClickListener {
            selectTab("U22")
        }

        binding.tabMember.setOnClickListener {
            selectTab("MEMBER")
        }

        binding.tabElite.setOnClickListener {
            selectTab("ELITE")
        }

        binding.tabVip.setOnClickListener {
            selectTab("VIP")
        }
    }

    private fun selectTab(rank: String) {
        resetTabs()
        when(rank){
            "U22" -> {
                activeTab(
                    binding.tabU22,
                    binding.txtU22
                )
                showPrivilege(rank)
            }

            "MEMBER" -> {
                activeTab(
                    binding.tabMember,
                    binding.txtMember
                )
                showPrivilege(rank)
            }

            "ELITE" -> {
                activeTab(
                    binding.tabElite,
                    binding.txtElite
                )
                showPrivilege(rank)
            }

            "VIP" -> {
                activeTab(
                    binding.tabVip,
                    binding.txtVip
                )
                showPrivilege(rank)
            }
        }
    }

    private fun activeTab(card: com.google.android.material.card.MaterialCardView, text: android.widget.TextView){ card.setCardBackgroundColor(
            Color.parseColor("#D32F2F")
        )
        text.setTextColor(
            Color.WHITE
        )
    }

    private fun resetTabs(){
        binding.tabU22.setCardBackgroundColor(
            Color.WHITE
        )
        binding.tabMember.setCardBackgroundColor(
            Color.WHITE
        )
        binding.tabElite.setCardBackgroundColor(
            Color.WHITE
        )
        binding.tabVip.setCardBackgroundColor(
            Color.WHITE
        )
        binding.txtU22.setTextColor(
            Color.parseColor("#666666")
        )
        binding.txtMember.setTextColor(
            Color.parseColor("#666666")
        )
        binding.txtElite.setTextColor(
            Color.parseColor("#666666")
        )
        binding.txtVip.setTextColor(
            Color.parseColor("#666666")
        )
    }

    // ĐẶC QUYỀN
    private fun showPrivilege(rank:String){
        when(rank){
            "U22" -> {
                binding.txtPrivilegeTitle.text = "Đặc quyền U22"
                binding.txtPrivilegeContent.text =
                    "✓ Vé sinh viên giá ưu đãi\n\n" +
                            "✓ Combo bắp nước tiết kiệm\n\n" +
                            "✓ Tích điểm đổi quà"

            }

            "MEMBER" -> {
                binding.txtPrivilegeTitle.text = "Đặc quyền MEMBER"
                binding.txtPrivilegeContent.text =
                    "✓ Giảm 5% giá vé\n\n" +
                            "✓ Voucher hàng tháng\n\n" +
                            "✓ Ưu tiên chương trình khuyến mãi"
            }

            "ELITE" -> {
                binding.txtPrivilegeTitle.text =
                    "Đặc quyền ELITE"
                binding.txtPrivilegeContent.text =
                    "✓ Giảm 10% giá vé\n\n" +
                            "✓ Ưu tiên vé hot\n\n" +
                            "✓ Combo ưu đãi premium"

            }
            "VIP" -> {
                binding.txtPrivilegeTitle.text = "Đặc quyền VIP"

                binding.txtPrivilegeContent.text =
                    "✓ Giảm 15% giá vé\n\n" +
                            "✓ Voucher VIP độc quyền\n\n" +
                            "✓ Quà sinh nhật\n\n" + "✓ Ưu tiên đặt vé"
            }
        }
    }

    // TÍNH HẠNG
    private fun tinhHang(diem: Int): String {
        return when {
            diem >= 3000 -> "VIP"
            diem >= 1500 -> "ELITE"
            diem >= 500 -> "MEMBER"
            else -> "U22"
        }
    }

    // FORMAT TIỀN
    private fun formatMoney(money: Int): String {

        return String.format("%,d", money)

    }
    private fun showPrivilegeDialog(rankTitle: String){

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_chi_tiet_dac_quyen_layout)

        val txtTitle = dialog.findViewById<TextView>(R.id.txtTitle)
        val txtContent = dialog.findViewById<TextView>(R.id.txtDetail)
        val btnClose = dialog.findViewById<Button>(R.id.btnClose)

        txtTitle.text = rankTitle
        txtContent.text = when(rankTitle){

            "Đặc quyền U22" ->

                """
            • Vé sinh viên giá ưu đãi
            
            • Combo bắp nước tiết kiệm
            
            • Tích điểm đổi quà
            
            • Ưu tiên các chương trình dành riêng cho sinh viên
            
            • Áp dụng cho khách hàng dưới 22 tuổi
            """.trimIndent()

            "Đặc quyền MEMBER" ->

                """
            • Giảm 5% giá vé
            
            • Voucher hàng tháng
            
            • Tích điểm đổi quà
            
            • Nhận thông báo khuyến mãi sớm
            
            • Tích lũy điểm để nâng hạng ELITE
            """.trimIndent()

            "Đặc quyền ELITE" ->

                """
            • Giảm 10% giá vé
            
            • Ưu tiên đặt vé phim hot
            
            • Combo Premium
            
            • Nhận voucher độc quyền
            
            • Nâng hạng VIP khi đủ điều kiện
            """.trimIndent()

            else ->

                """
            • Giảm 15% giá vé
            
            • Voucher VIP độc quyền
            
            • Quà sinh nhật
            
            • Ưu tiên đặt vé
            
            • Quà tặng hằng năm
            
            • Đặc quyền cao nhất của CGV
            """.trimIndent()
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()


    }

}