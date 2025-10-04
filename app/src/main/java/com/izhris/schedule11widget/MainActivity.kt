package com.izhris.schedule11widget

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import com.izhris.schedule11widget.data.BusSchedule
import com.izhris.schedule11widget.data.ScheduleData
import com.izhris.schedule11widget.data.ScheduleViewModel
import com.izhris.schedule11widget.ui.theme.BusScheduleTheme
import kotlinx.coroutines.launch

class TicketShape(private val cornerRadius: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = Path().apply {
                addRoundRect(RoundRect(Rect(0f, 0f, size.width, size.height), CornerRadius(cornerRadius)))
            }
        )
    }
}


class MainActivity : ComponentActivity() {

    private val viewModel: ScheduleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BusScheduleTheme {
                // --- NEW: This block makes the status bar icons (time, battery) dark ---
                val view = LocalView.current
                if (!view.isInEditMode) {
                    SideEffect {
                        val window = (view.context as Activity).window
                        WindowInsetsControllerCompat(window, view).isAppearanceLightStatusBars = true
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BusScheduleScreen(viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkDayOfWeek()
        viewModel.findClosestTrips()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusScheduleScreen(viewModel: ScheduleViewModel) {
    val tabIndex by viewModel.tabIndex
    val tabIcons = listOf(R.drawable.ic_working_days, R.drawable.ic_weekend)

    val workingDayHighlightIndex by viewModel.closestWorkingDayTripIndex
    val weekendHighlightIndex by viewModel.closestWeekendTripIndex
    val coroutineScope = rememberCoroutineScope()
    val workingListState = rememberLazyListState()
    val weekendListState = rememberLazyListState()

    // --- MODIFIED: Color scheme for the new Tab design ---
    val selectedTabBackgroundColor = Color(0xFF38C16F)
    val unselectedTabBackgroundColor = Color.LightGray
    val selectedIconColor = Color.White
    val unselectedIconColor = Color.DarkGray


    LaunchedEffect(workingDayHighlightIndex) {
        coroutineScope.launch {
            if (workingDayHighlightIndex >= 0) {
                val scrollIndex = (workingDayHighlightIndex - 1).coerceAtLeast(0)
                workingListState.animateScrollToItem(index = scrollIndex)
            }
        }
    }
    LaunchedEffect(weekendHighlightIndex) {
        coroutineScope.launch {
            if (weekendHighlightIndex >= 0) {
                val scrollIndex = (weekendHighlightIndex - 1).coerceAtLeast(0)
                weekendListState.animateScrollToItem(index = scrollIndex)
            }
        }
    }

    Scaffold { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // --- MODIFIED: TabRow updated for the new split-background design ---
            TabRow(
                selectedTabIndex = tabIndex,
                containerColor = Color.Transparent, // Container is transparent
                indicator = { /* Indicator is not needed for this design */ },
                divider = { /* No divider */ }
            ) {
                tabIcons.forEachIndexed { index, iconId ->
                    val isSelected = tabIndex == index
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(index) },
                        modifier = Modifier.background(
                            if (isSelected) selectedTabBackgroundColor else unselectedTabBackgroundColor
                        ),
                        icon = {
                            Image(
                                painter = painterResource(id = iconId),
                                contentDescription = if (index == 0) "Робочі дні" else "Вихідні",
                                modifier = Modifier.size(28.dp)
                            )
                        },
                        selectedContentColor = selectedIconColor,
                        unselectedContentColor = unselectedIconColor
                    )
                }
            }

            when (tabIndex) {
                0 -> ScheduleList(
                    schedule = ScheduleData.workingDaysSchedule,
                    highlightedIndex = workingDayHighlightIndex,
                    listState = workingListState
                )
                1 -> ScheduleList(
                    schedule = ScheduleData.weekendSchedule,
                    highlightedIndex = weekendHighlightIndex,
                    listState = weekendListState
                )
            }
        }
    }
}

@Composable
fun ScheduleList(
    schedule: BusSchedule,
    highlightedIndex: Int,
    listState: androidx.compose.foundation.lazy.LazyListState
) {
    val leftColor = Color(0xFF96cace)
    val rightColor = Color(0xFF88dcaa)
    val backgroundBrush = Brush.horizontalGradient(listOf(leftColor, rightColor))
    val headerColor = Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundBrush)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = "Боярка",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = headerColor,
                    modifier = Modifier.weight(1f), // Takes up 50% of the space
                    textAlign = TextAlign.Center    // Centers the text in its 50% space
                )
                Text(
                    text = "Малютянка",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = headerColor,
                    modifier = Modifier.weight(1f), // Takes up 50% of the space
                    textAlign = TextAlign.Center    // Centers the text in its 50% space
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                state = listState
            ) {
                itemsIndexed(schedule.trips) { index, trip ->
                    ScheduleRow(
                        timeFromBoyarka = trip.timeFromBoyarka,
                        timeFromMalyutyanka = trip.timeFromMalyutyanka,
                        note = trip.note,
                        isHighlighted = index == highlightedIndex
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun ScheduleRow(
    timeFromBoyarka: String,
    timeFromMalyutyanka: String,
    note: String?,
    isHighlighted: Boolean
) {
    val backgroundColor = if (isHighlighted) Color(0xFF38c16f) else Color(0xFFF1F1F1)
    val contentColor = if (isHighlighted) Color.White else Color.DarkGray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(
                color = backgroundColor,
                shape = TicketShape(cornerRadius = 40f)
            )
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = timeFromBoyarka,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier.width(40.dp),
            contentAlignment = Alignment.Center
        ) {
            if (note != null) {
                NoteIndicator(note = note)
            }
        }
        Text(
            text = timeFromMalyutyanka,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NoteIndicator(note: String) {
    val (letters, color) = when (note) {
        "Іванків" -> note.take(2) to Color(0xFF7DBEC3)
        "Танкістів" -> note.take(2) to Color(0xFF7DBEC3)
        "Глеваха" -> note.take(2) to Color(0xFF7DBEC3)
        else -> null to Color.Transparent
    }

    if (letters != null) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color, shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letters.uppercase(),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}