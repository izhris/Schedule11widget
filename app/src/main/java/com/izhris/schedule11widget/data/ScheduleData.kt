package com.izhris.schedule11widget.data

object ScheduleData {
    val workingDaysSchedule = BusSchedule(
        trips = listOf(
            Trip(timeFromBoyarka = "", timeFromMalyutyanka = "5:50", note = "Танкістів"),
            Trip(timeFromBoyarka = "6:15", timeFromMalyutyanka = "6:25"),
            Trip(timeFromBoyarka = "6:35", timeFromMalyutyanka = "6:45"),
            Trip(timeFromBoyarka = "7:05", timeFromMalyutyanka = "7:15", note = "Іванків"),
            Trip(timeFromBoyarka = "7:40", timeFromMalyutyanka = "7:50"),
            Trip(timeFromBoyarka = "8:10", timeFromMalyutyanka = "8:20", note = "Танкістів"),
            Trip(timeFromBoyarka = "8:35", timeFromMalyutyanka = "9:05", note = "Глеваха"),
            Trip(timeFromBoyarka = "9:40", timeFromMalyutyanka = "9:50"),
            Trip(timeFromBoyarka = "10:00", timeFromMalyutyanka = "10:10"),
            Trip(timeFromBoyarka = "11:00", timeFromMalyutyanka = "11:10", note = "Іванків"),
            Trip(timeFromBoyarka = "12:00", timeFromMalyutyanka = "12:10"),
            Trip(timeFromBoyarka = "13:00", timeFromMalyutyanka = "13:10"),
            Trip(timeFromBoyarka = "14:00", timeFromMalyutyanka = "14:10"),
            Trip(timeFromBoyarka = "15:00", timeFromMalyutyanka = "15:10", note = "Танкістів"),
            Trip(timeFromBoyarka = "16:00", timeFromMalyutyanka = "16:10"),
            Trip(timeFromBoyarka = "17:00", timeFromMalyutyanka = "17:10"),
            Trip(timeFromBoyarka = "18:00", timeFromMalyutyanka = "18:10"),
            Trip(timeFromBoyarka = "18:30", timeFromMalyutyanka = "18:40"),
            Trip(timeFromBoyarka = "19:00", timeFromMalyutyanka = "19:30", note = "Глеваха"),
            Trip(timeFromBoyarka = "20:10", timeFromMalyutyanka = "20:20", note = "Танкістів"),
            Trip(timeFromBoyarka = "21:05", timeFromMalyutyanka = "21:15")
        )
    )
    val weekendSchedule = BusSchedule(
        trips = listOf(
            Trip(timeFromBoyarka = "", timeFromMalyutyanka = "6:00"),
            Trip(timeFromBoyarka = "6:20", timeFromMalyutyanka = "6:30"),
            Trip(timeFromBoyarka = "7:05", timeFromMalyutyanka = "7:20", note = "Іванків"),
            Trip(timeFromBoyarka = "8:00", timeFromMalyutyanka = "8:30", note = "Глеваха"),
            Trip(timeFromBoyarka = "9:00", timeFromMalyutyanka = "9:30", note = "Глеваха"),
            Trip(timeFromBoyarka = "10:00", timeFromMalyutyanka = "10:30", note = "Глеваха"),
            Trip(timeFromBoyarka = "11:00", timeFromMalyutyanka = "11:10"),
            Trip(timeFromBoyarka = "12:00", timeFromMalyutyanka = "12:10"),
            Trip(timeFromBoyarka = "13:00", timeFromMalyutyanka = "13:10"),
            Trip(timeFromBoyarka = "14:00", timeFromMalyutyanka = "14:15", note = "Іванків"),
            Trip(timeFromBoyarka = "15:00", timeFromMalyutyanka = "15:10", note = "Танкістів"),
            Trip(timeFromBoyarka = "16:00", timeFromMalyutyanka = "16:10"),
            Trip(timeFromBoyarka = "17:00", timeFromMalyutyanka = "17:10"),
            Trip(timeFromBoyarka = "18:00", timeFromMalyutyanka = "18:20", note = "Іванків"),
            Trip(timeFromBoyarka = "19:00", timeFromMalyutyanka = "19:10"),
            Trip(timeFromBoyarka = "20:00", timeFromMalyutyanka = "20:10", note = "Танкістів"),
            Trip(timeFromBoyarka = "21:20", timeFromMalyutyanka = "21:30")
        )
    )
}