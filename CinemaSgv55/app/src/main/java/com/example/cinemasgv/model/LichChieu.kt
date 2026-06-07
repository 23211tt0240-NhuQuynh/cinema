package com.example.cinemasgv.model

data class LichChieu(
    var id:String = "",
    var maPhim:String = "",
    var tenPhim:String = "",
    var hinhAnhPhim:String = "",
    var maCumRap:String = "",
    var tenCumRap:String = "",
    var maPhong:String = "",
    var tenPhong:String = "",
    var ngayChieu:String = "",
    var gioBatDau:String = "",
    var gioKetThuc:String = "",
    var giaVe:Long = 0,
    var trangThai:String = ""
)