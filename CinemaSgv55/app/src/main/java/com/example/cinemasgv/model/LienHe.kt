package com.example.cinemasgv.model

import com.google.firebase.Timestamp

data class LienHe(
    var id:String="",
    var userID:String="",
    var tieuDe:String="",
    var noiDung:String="",
    var adminTraLoi:String="",
    var trangThai:String="",
    var thoiGian: Timestamp?=null
)