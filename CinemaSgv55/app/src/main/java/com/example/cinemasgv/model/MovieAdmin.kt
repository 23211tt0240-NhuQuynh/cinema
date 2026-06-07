package com.example.cinemasgv.model
data class MovieAdmin(
    var maPhim:String="",
    var tenPhim:String="",
    var daoDien:String="",
    var danhGia:Double=0.0,
    var thoiLuong:Int=0,
    var hinhAnh:String="",
    var moTa:String="",
    var ngayKhoiChieu:String="",
    var ngonNgu:String="",
    var dienVien: List<String> = mutableListOf(),
    var yeuThich:Boolean=false,
    var trangThai:Boolean=true,
    var doTuoi:Int=0
)