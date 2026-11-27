package com.example.tempapp.main.task

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tempapp.R
import com.example.tempapp.ui.theme.Gray200
import com.example.tempapp.ui.theme.Gray500
import com.example.tempapp.ui.theme.Gray600
import com.example.tempapp.ui.theme.Gray900
import com.example.tempapp.ui.theme.Purple25
import com.example.tempapp.ui.theme.Purple100
import com.example.tempapp.ui.theme.Purple300
import com.example.tempapp.ui.theme.Purple500
import com.example.tempapp.ui.theme.Purple600

private enum class TaskFilter(val label: String) {
    All("All"),
    InProgress("In Progress"),
    Finish("Finish")
}

private enum class TaskStage {
    ToDo,
    InProgress,
    Done
}

private data class TaskItem(
    val title: String,
    val description: String,
    val stage: TaskStage,
    val priority: String,
    val deadline: String,
    val progress: Float,
    val comments: Int,
    val participantLabels: List<String>
)

private val taskItems = listOf(
    TaskItem(
        title = "Mobile App Design",
        description = "Create an engaging experience for the Work Mate mobile application.",
        stage = TaskStage.InProgress,
        priority = "High",
        deadline = "18 Sept, 2024",
        progress = .72f,
        comments = 8,
        participantLabels = listOf("JD", "AM", "LS")
    ),
    TaskItem(
        title = "Dashboard Development",
        description = "Build the employee overview widgets and task status interactions.",
        stage = TaskStage.InProgress,
        priority = "Medium",
        deadline = "22 Sept, 2024",
        progress = .46f,
        comments = 4,
        participantLabels = listOf("AR", "MH")
    ),
    TaskItem(
        title = "Project Brief Review",
        description = "Review the latest notes and align on sprint priorities.",
        stage = TaskStage.ToDo,
        priority = "Low",
        deadline = "26 Sept, 2024",
        progress = .12f,
        comments = 2,
        participantLabels = listOf("KA")
    ),
    TaskItem(
        title = "Brand Assets Library",
        description = "Prepare the handoff-ready brand asset collection.",
        stage = TaskStage.Done,
        priority = "Medium",
        deadline = "12 Sept, 2024",
        progress = 1f,
        comments = 11,
        participantLabels = listOf("YN", "TJ", "SY")
    )
)

@Composable
fun TaskOverviewScreen() {
    var selectedFilter by rememberSaveable { mutableStateOf(TaskFilter.InProgress) }
    val summary = TaskSummary.from(taskItems)
    val visibleTasks = taskItems.filter { task ->
        when (selectedFilter) {
            TaskFilter.All -> true
            TaskFilter.InProgress -> task.stage == TaskStage.InProgress
            TaskFilter.Finish -> task.stage == TaskStage.Done
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Purple25)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 94.dp)
        ) {
            TaskHero()
            TaskSummaryCard(
                summary = summary,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .offset(y = (-42).dp)
            )
            Spacer(Modifier.height(18.dp))
            BurnoutStatsCard(modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(22.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "My Tasks",
                    color = Gray900,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "View all",
                    color = Purple500,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(14.dp))
            TaskFilters(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                visibleTasks.forEach { task -> TaskCard(task) }
            }
        }
        CreateTaskButton(
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = {}
        )
    }
}

private data class TaskSummary(
    val toDo: Int,
    val inProgress: Int,
    val done: Int
) {
    companion object {
        fun from(tasks: List<TaskItem>) = TaskSummary(
            toDo = tasks.count { it.stage == TaskStage.ToDo } + 4,
            inProgress = tasks.count { it.stage == TaskStage.InProgress },
            done = tasks.count { it.stage == TaskStage.Done }
        )
    }
}

@Composable
private fun TaskHero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(Brush.horizontalGradient(listOf(Purple500, Purple600)))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 26.dp, top = 36.dp, end = 130.dp)
        ) {
            Text(
                text = "Challanges Awaiting",
                color = Color.White,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )
            Spacer(Modifier.height(7.dp))
            Text(
                text = "Let’s tackle your to do list",
                color = Color.White.copy(alpha = .9f),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
        Image(
            painter = painterResource(R.drawable.task_doc),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 42.dp, end = 22.dp)
                .size(width = 104.dp, height = 96.dp)
        )
    }
}

@Composable
private fun TaskSummaryCard(summary: TaskSummary, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(16.dp), clip = false)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SummaryMetric("To Do", summary.toDo.toString(), Purple500, Modifier.weight(1f))
        SummaryDivider()
        SummaryMetric("In Progress", summary.inProgress.toString(), Color(0xFFFFA92B), Modifier.weight(1f))
        SummaryDivider()
        SummaryMetric("Done", summary.done.toString(), Color(0xFF28B687), Modifier.weight(1f))
    }
}

@Composable
private fun SummaryMetric(label: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, color = Gray900, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(5.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(accent, CircleShape)
            )
            Spacer(Modifier.width(5.dp))
            Text(label, color = Gray600, fontSize = 11.sp)
        }
    }
}

@Composable
private fun SummaryDivider() {
    Spacer(
        modifier = Modifier
            .width(1.dp)
            .height(45.dp)
            .background(Gray200)
    )
}

@Composable
private fun BurnoutStatsCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Sprint 20", color = Gray900, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("Burnout Stats", color = Gray500, fontSize = 12.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Purple25)
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
                Text("12 days left", color = Purple500, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(18.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            listOf(38, 64, 45, 78, 58, 84, 68).forEachIndexed { index, percent ->
                BurnoutBar(
                    heightPercent = percent,
                    emphasized = index == 5
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                Text(day, color = Gray500, fontSize = 10.sp, modifier = Modifier.width(23.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
    }
}

@Composable
private fun BurnoutBar(heightPercent: Int, emphasized: Boolean) {
    Box(
        modifier = Modifier
            .width(23.dp)
            .height(76.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Purple25),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((76 * heightPercent / 100).dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (emphasized) Purple500 else Purple300)
        )
    }
}

@Composable
private fun TaskFilters(
    selectedFilter: TaskFilter,
    onFilterSelected: (TaskFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TaskFilter.entries.forEach { filter ->
            val isSelected = filter == selectedFilter
            Text(
                text = filter.label,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) Purple500 else Color.White)
                    .border(
                        width = if (isSelected) 0.dp else 1.dp,
                        color = Gray200,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 16.dp, vertical = 9.dp),
                color = if (isSelected) Color.White else Gray600,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun TaskCard(task: TaskItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StagePill(task.stage)
            PriorityPill(task.priority)
        }
        Spacer(Modifier.height(13.dp))
        Text(task.title, color = Gray900, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(5.dp))
        Text(
            text = task.description,
            color = Gray500,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Progress", color = Gray600, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text("${(task.progress * 100).toInt()}%", color = Purple500, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(7.dp))
        TaskProgress(task.progress)
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Participants(task.participantLabels)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.calendar),
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(Gray500)
                )
                Spacer(Modifier.width(5.dp))
                Text(task.deadline, color = Gray600, fontSize = 11.sp)
                Spacer(Modifier.width(10.dp))
                Image(
                    painter = painterResource(R.drawable.message),
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(Gray500)
                )
                Spacer(Modifier.width(4.dp))
                Text(task.comments.toString(), color = Gray600, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun StagePill(stage: TaskStage) {
    val (label, color, background) = when (stage) {
        TaskStage.ToDo -> Triple("To Do", Purple500, Purple25)
        TaskStage.InProgress -> Triple("In Progress", Color(0xFFFFA92B), Color(0xFFFFF2D9))
        TaskStage.Done -> Triple("Finished", Color(0xFF208E6D), Color(0xFFDDF6EC))
    }
    Text(
        text = label,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        color = color,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun PriorityPill(priority: String) {
    val color = when (priority) {
        "High" -> Color(0xFFE36464)
        "Medium" -> Color(0xFFFFA92B)
        else -> Color(0xFF268B6E)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(6.dp).background(color, CircleShape))
        Spacer(Modifier.width(5.dp))
        Text(priority, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun TaskProgress(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(7.dp)
            .clip(CircleShape)
            .background(Purple100)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(7.dp)
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(Purple500, Purple600)))
        )
    }
}

@Composable
private fun Participants(labels: List<String>) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        labels.forEachIndexed { index, label ->
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .padding(start = if (index == 0) 0.dp else 4.dp)
                    .clip(CircleShape)
                    .background(if (index % 2 == 0) Purple100 else Color(0xFFF7E0CF)),
                contentAlignment = Alignment.Center
            ) {
                Text(label, color = Purple600, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CreateTaskButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 28.dp, vertical = 13.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Brush.horizontalGradient(listOf(Purple500, Purple600))),
            contentAlignment = Alignment.Center
        ) {
            Text("Create Task", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
