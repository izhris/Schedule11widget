package com.izhris.schedule11widget

// In MainActivity.kt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.izhris.schedule11widget.R
import com.izhris.schedule11widget.data.BusSchedule
import com.izhris.schedule11widget.data.ScheduleData
import com.izhris.schedule11widget.data.ScheduleViewModel
import com.izhris.schedule11widget.ui.theme.BusScheduleTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // Get an instance of the ViewModel
    private val viewModel: ScheduleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Your theme would be defined in ui/theme/Theme.kt
            BusScheduleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BusScheduleScreen(viewModel)
                }
            }
        }
    }

    // You can also handle app lifecycle events here to refresh the data
    override fun onResume() {
        super.onResume()
        viewModel.checkDayOfWeek()
        viewModel.findClosestTrips()
    }
}

// This is the main Composable, equivalent to your BusSchedulePage widget
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusScheduleScreen(viewModel: ScheduleViewModel) {
    val tabIndex by viewModel.tabIndex
    val tabs = listOf("Робочі дні", "Вихідні")

    val workingDayHighlightIndex by viewModel.closestWorkingDayTripIndex
    val weekendHighlightIndex by viewModel.closestWeekendTripIndex

    // Coroutine scope for launching animations
    val coroutineScope = rememberCoroutineScope()

    // State for each list, equivalent to ScrollController
    val workingListState = rememberLazyListState()
    val weekendListState = rememberLazyListState()

    // This effect runs when the highlight index changes, triggering the scroll
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Розклад руху", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.bus_logo),
                        contentDescription = "Bus Logo",
                        modifier = Modifier.size(48.dp).padding(start = 8.dp)
                    )
                },
                actions = {
                    Text("25 ₴", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 16.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF455A64), // blueGrey.shade800
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = tabIndex == index,
                        onClick = { viewModel.selectTab(index) }
                    )
                }
            }

            // This acts like your TabBarView
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


// This is your _ScheduleList widget equivalent
@Composable
fun ScheduleList(
    schedule: BusSchedule,
    highlightedIndex: Int,
    listState: androidx.compose.foundation.lazy.LazyListState // Specify full path to avoid ambiguity
) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text("Боярка", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Малютянка", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // This is the equivalent of ListView.builder
        LazyColumn(state = listState) {
            itemsIndexed(schedule.trips) { index, trip ->
                val isHighlighted = index == highlightedIndex

                ScheduleRow(
                    timeFromBoyarka = trip.timeFromBoyarka,
                    timeFromMalyutyanka = trip.timeFromMalyutyanka,
                    note = trip.note,
                    isHighlighted = isHighlighted
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 10.dp), // Adds 16dp of space on both left and right
                    color = Color.LightGray
                )
            }
        }
    }
}

// This is your list item row equivalent
@Composable
fun ScheduleRow(
    timeFromBoyarka: String,
    timeFromMalyutyanka: String,
    note: String?,
    isHighlighted: Boolean
) {
    val highlightModifier = if (isHighlighted) {
        Modifier
            .border(
                width = 2.dp,
                color = Color(0xFF29B6F6), // lightBlue
                shape = RoundedCornerShape(10.dp)
            )
            .padding(4.dp)
    } else {
        Modifier.padding(4.dp)
    }

    Column(modifier = highlightModifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimeCard(text = timeFromBoyarka, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(16.dp))
            TimeCard(text = timeFromMalyutyanka, modifier = Modifier.weight(1f))
        }
        if (note != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TimeCard(text: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
    }
}