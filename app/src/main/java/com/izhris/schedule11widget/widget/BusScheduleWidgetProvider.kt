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
        val schedule = if (isWeekend) {
            ScheduleData.weekendSchedule
        } else {
            ScheduleData.workingDaysSchedule
        }

        val currentTimeInMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)

        for (trip in schedule.trips) {
            val boyarkaTime = trip.timeFromBoyarka
            val malyutyankaTime = trip.timeFromMalyutyanka

            val boyarkaTripTimeInMinutes = if (boyarkaTime.isNotEmpty()) {
                val parts = boyarkaTime.split(":")
                if (parts.size == 2) {
                    val hour = parts[0].toIntOrNull() ?: 0
                    val minute = parts[1].toIntOrNull() ?: 0
                    hour * 60 + minute
                } else null
            } else null

            val malyutyankaTripTimeInMinutes = if (malyutyankaTime.isNotEmpty()) {
                val parts = malyutyankaTime.split(":")
                if (parts.size == 2) {
                    val hour = parts[0].toIntOrNull() ?: 0
                    val minute = parts[1].toIntOrNull() ?: 0
                    hour * 60 + minute
                } else null
            } else null

            // Find the earliest valid time for the current trip
            val earliestTripTimeInMinutes = listOfNotNull(boyarkaTripTimeInMinutes, malyutyankaTripTimeInMinutes).minOrNull()

            if (earliestTripTimeInMinutes != null && earliestTripTimeInMinutes >= currentTimeInMinutes) {
                return Pair(boyarkaTime, malyutyankaTime)
            }
        }

        // If no more trips for today, return the first trip for tomorrow.
        val tomorrowCalendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
        val tomorrowDayOfWeek = tomorrowCalendar.get(Calendar.DAY_OF_WEEK)
        val isTomorrowWeekend = tomorrowDayOfWeek == Calendar.SATURDAY || tomorrowDayOfWeek == Calendar.SUNDAY
        val tomorrowSchedule = if (isTomorrowWeekend) {
            ScheduleData.weekendSchedule
        } else {
            ScheduleData.workingDaysSchedule
        }

        val tomorrowFirstTrip = tomorrowSchedule.trips.firstOrNull()
        return if (tomorrowFirstTrip != null) {
            Pair(tomorrowFirstTrip.timeFromBoyarka, tomorrowFirstTrip.timeFromMalyutyanka)
        } else {
            // Default to empty strings if no schedule is found
            Pair("", "")
        }
    }
}