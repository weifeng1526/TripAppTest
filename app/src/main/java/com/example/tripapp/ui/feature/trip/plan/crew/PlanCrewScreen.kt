package com.example.tripapp.ui.feature.trip.plan.crew

import android.util.Log
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.swithscreen.PlanHomeScreen
import com.example.tripapp.R
import com.example.tripapp.ui.feature.trip.dataObjects.CrewMmeber
import com.example.tripapp.ui.feature.trip.plan.home.PLAN_HOME_ROUTE

@Composable
fun PlanCrewScreen(
    navController: NavController,
    planCrewViewModel: PlanCrewViewModel,
    schNo: Int,
    schName: String
) {
    val crewOfMembers by planCrewViewModel.crewOfMembersSatate.collectAsState()
    LaunchedEffect(Unit) {
        planCrewViewModel.getCrewMembersRequest(schNo) {
            planCrewViewModel.setCrewMembers(it)
            Log.d("tag_PlanCrewScreen", "${it}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(6.dp)
                    .background(Color.White),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.group),
                    contentDescription = "schedule Icon",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 6.dp),
                    tint = Color.Unspecified
                )
                Text(
                    text = crewOfMembers.firstOrNull {
                        it.crewIde.toInt() == 2
                    }?.crewName ?: ""
                )
            }
            //Spacer(Modifier.fillMaxWidth(1f))
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.White)
                    .clickable { },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.White),
                    onClick = { navController.navigate("${MEMBER_INVITE_ROUTE}/${schNo}/${schName}") }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.person_add),
                        contentDescription = "",
                        modifier = Modifier.size(48.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        }
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(1)
        ) {
            items(crewOfMembers.size) {
                ShowPersonRow(
                    planCrewViewModel = planCrewViewModel,
                    crewMmeber = crewOfMembers[it]
                )
            }
        }
    }
}

@Composable
fun ShowPersonRow(
    planCrewViewModel: PlanCrewViewModel,
    crewMmeber: CrewMmeber
) {
    Box(
        modifier = Modifier.wrapContentHeight()
    ) {
        ListItem(
            modifier = Modifier.border(1.dp, Color.LightGray),
            leadingContent = {
                Icon(
                    painter = painterResource(id = R.drawable.person),
                    contentDescription = "",
                    modifier = Modifier.size(48.dp),
                    tint = Color.Unspecified
                )
            },
            headlineContent = {
                Text(
                    text = crewMmeber.memName
                )
            },
            supportingContent = {
                Text(text = crewMmeber.memEmail)
            },
        )
        IconButton(
            onClick = {},
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.TopEnd),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.disabled),
                contentDescription = "",
                modifier = Modifier.size(30.dp),
                tint = Color.Unspecified
            )
        }
    }
}


@Preview
@Composable
fun PreviewPlanCrewScreen() {
    PlanCrewScreen(
        navController = rememberNavController(),
        planCrewViewModel = viewModel(),
        schNo = 1,
        schName = ""
    )
}