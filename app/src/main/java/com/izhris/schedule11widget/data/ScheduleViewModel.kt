package com.izhris.schedule11widget.data

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.util.Calendar

class ScheduleViewModel : ViewModel() {

    // This holds the currently selected tab index (0 for working, 1 for weekend)
    private val _tabIndex = mutableStateOf(0)
    val tabIndex: State<Int> = _tabIndex

    // These hold the index of the next trip to highlight
    private val _closestWorkingDayTripIndex = mutableStateOf(0)
    val closestWorkingDayTripIndex: State<Int> = _closestWorkingDayTripIndex

    private val _closestWeekendTripIndex = mutableStateOf(0)
    val closestWeekendTripIndex: State<Int> = _closestWeekendTripIndex

    // This function is called when the ViewModel is first created
    init {
        checkDayOfWeek()
        findClosestTrips()
    }

    // Equivalent to your logic for setting the initial tab
    fun checkDayOfWeek() {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        _tabIndex.value = if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) 1 else 0
    }

    // Equivalent to your _findClosestTrips method
    fun findClosestTrips() {
        _closestWorkingDayTripIndex.value = getClosestTripIndex(ScheduleData.workingDaysSchedule.trips)
        _closestWeekendTripIndex.value = getClosestTripIndex(ScheduleData.weekendSchedule.trips)
    }

    fun selectTab(index: Int) {
        _tabIndex.value = index
    }

    // Equivalent to your _getClosestTripIndex method
    private fun getClosestTripIndex(trips: List<Trip>): Int {
        val calendar = Calendar.getInstance()
        val currentTimeInMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)

        for ((index, trip) in trips.withIndex()) {
            val time = trip.timeFromMalyutyanka
            if (time.isEmpty()) continue

            // Basic parsing, can be made more robust
            val timeString = time.split(" ")[0]
            val parts = timeString.split(":")
            if (parts.size == 2) {
                val hour = parts[0].toIntOrNull() ?: 0
                val minute = parts[1].toIntOrNull() ?: 0
                val tripTimeInMinutes = hour * 60 + minute
                if (tripTimeInMinutes >= currentTimeInMinutes) {
                    return index
                }
            }
        }
        return trips.size - 1 // Default to the last one if no future trip is found
    }
}