package com.example.cinemasgv.model

data class CumRap(
    var tenCumRap: String = "",
    var diaChi: String = "",
    var dsLichChieu: MutableList<LichChieu> = mutableListOf()
)