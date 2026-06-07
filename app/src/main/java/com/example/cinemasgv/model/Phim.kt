
package com.example.cinemasgv.model

data class Phim(
    var maPhim: String = "",
    var tenPhim: String = "",
    var moTa: String = "",
    var theLoai: List<String> = listOf(),
    var thoiLuong: Int = 0,
    var ngayKhoiChieu: String = "",
    var hinhAnh: String = "",
    var ngonNgu: String = "",
    var doTuoi: Int = 0,
    var danhGia: Double = 0.0,
    var daoDien: String = "",
    var dienVien: List<String> = listOf(),
    var trangThai: Boolean = false,
    var yeuThich: Boolean = false
)