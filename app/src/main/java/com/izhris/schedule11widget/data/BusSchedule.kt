package com.izhris.schedule11widget.data

data class BusSchedule(
    val trips: List<Trip>
)

data class Trip(
    val timeFromBoyarka: String,
    val timeFromMalyutyanka: String,
    val note: String? = null
)