package com.izhris.schedule11widget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.izhris.schedule11widget.MainActivity
import com.izhris.schedule11widget.R
import com.izhris.schedule11widget.data.ScheduleData
import java.util.Calendar

class BusScheduleWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.bus_schedule_widget)
        val (closestTripBoyarka, closestTripMalyutyanka) = getClosestTrip()

        views.setTextViewText(R.id.boyarka_time, "Боярка: $closestTripBoyarka")
        views.setTextViewText(R.id.malyutyanka_time, "Малютянка: $closestTripMalyutyanka")

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getClosestTrip(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val isWeekend = dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
        val todaySchedule = if (isWeekend) {
            ScheduleData.weekendSchedule
        } else {
            ScheduleData.workingDaysSchedule
        }

        val currentTimeInMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)

        // Helper function to parse time string to minutes
        fun timeToMinutes(time: String): Int? {
            if (time.isEmpty()) return null
            val parts = time.split(":")
            return if (parts.size == 2) {
                val hour = parts[0].toIntOrNull() ?: 0
                val minute = parts[1].toIntOrNull() ?: 0
                hour * 60 + minute
            } else {
                null
            }
        }

        // --- NEW LOGIC START ---

        // 1. Find the next departure from Boyarka for TODAY
        var nextBoyarkaTime = todaySchedule.trips.firstOrNull { trip ->
            val tripTime = timeToMinutes(trip.timeFromBoyarka)
            tripTime != null && tripTime >= currentTimeInMinutes
        }?.timeFromBoyarka

        // 2. Find the next departure from Malyutyanka for TODAY
        var nextMalyutyankaTime = todaySchedule.trips.firstOrNull { trip ->
            val tripTime = timeToMinutes(trip.timeFromMalyutyanka)
            tripTime != null && tripTime >= currentTimeInMinutes
        }?.timeFromMalyutyanka

        // 3. If a trip is NOT found for today, find the first one for TOMORROW
        if (nextBoyarkaTime == null || nextMalyutyankaTime == null) {
            val tomorrowCalendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
            val tomorrowDayOfWeek = tomorrowCalendar.get(Calendar.DAY_OF_WEEK)
            val isTomorrowWeekend = tomorrowDayOfWeek == Calendar.SATURDAY || tomorrowDayOfWeek == Calendar.SUNDAY
            val tomorrowSchedule = if (isTomorrowWeekend) {
                ScheduleData.weekendSchedule
            } else {
                ScheduleData.workingDaysSchedule
            }

            // If Boyarka time was not found for today, get the FIRST one from tomorrow's schedule
            if (nextBoyarkaTime == null) {
                nextBoyarkaTime = tomorrowSchedule.trips
                    .firstOrNull { it.timeFromBoyarka.isNotEmpty() }
                    ?.timeFromBoyarka
            }

            // If Malyutyanka time was not found for today, get the FIRST one from tomorrow's schedule
            if (nextMalyutyankaTime == null) {
                nextMalyutyankaTime = tomorrowSchedule.trips
                    .firstOrNull { it.timeFromMalyutyanka.isNotEmpty() }
                    ?.timeFromMalyutyanka
            }
        }

        // --- NEW LOGIC END ---

        return Pair(nextBoyarkaTime ?: "", nextMalyutyankaTime ?: "")
    }
}