package com.example.tripview.select

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tripapp.R
import com.example.tripapp.ui.feature.baggage.baglist.BAG_NAVIGATION_ROUTE
import com.example.tripapp.ui.feature.member.GetUid
import com.example.tripapp.ui.feature.member.MemberRepository
import com.example.tripapp.ui.feature.member.login.MemberLoginViewModel
import com.example.tripapp.ui.feature.trip.notes.show.SHOW_SCH_ROUTE
import com.example.tripapp.ui.feature.trip.dataObjects.Plan
import com.example.tripapp.ui.feature.trip.plan.home.PlanHomeViewModel
import com.example.tripapp.ui.restful.RequestVM
import com.example.tripapp.ui.theme.purple100
import com.example.tripapp.ui.theme.purple300
import com.example.tripapp.ui.theme.white100
import com.example.tripapp.ui.theme.white400
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.log

@Composable
fun SelectScreenRoute(navController: NavController){
    SelectSchScreen(
        navController = navController,
        requestVM = RequestVM()
    )
}

@Composable
fun SelectSchScreen(
    navController: NavController,
    planHomeViewModel: PlanHomeViewModel = viewModel(),
    requestVM: RequestVM = viewModel(),
) {


    val plans by planHomeViewModel.plansState.collectAsState()
    val uid = GetUid(MemberRepository)

    Log.d("SelectSchScreen", "Plan $plans")
    LaunchedEffect(uid) {
        val  planResponse = requestVM.GetPlans()
        Log.d("planResponse", "Plan $planResponse")
        planHomeViewModel.setPlans(planResponse)

    }
    val userPlans = plans.filter { it.memNo == uid }
    val today = LocalDate.now()

    // 找出即將出發的行程
    val recentPlan = userPlans
        .mapNotNull { plan ->
            try {
                val startDate = LocalDate.parse(plan.schEnd, DateTimeFormatter.ISO_DATE)
                plan to startDate
            } catch (e: Exception) {
                null
            }
        }
        .filter { it.second >= today }
        .sortedBy { it.second }
        .firstOrNull()?.first

    // 所有已發生的行程（包括過去與未來）
    val allPlans = userPlans

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // 即將出發部分
        if (recentPlan != null) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "即將出發",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                RecentPlanCard(
                    navController = navController,
                    plan = recentPlan
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 所有行程部分
        Text(
            text = "所有行程",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(allPlans) { plan ->
                SelectSchCard(navController = navController, plan = plan)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun RecentPlanCard(
    navController: NavController,
    plan: Plan
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(R.color.white_200))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(205.dp)
                .background(color = colorResource(R.color.white_200))
                .clickable { navController.navigate("${SHOW_SCH_ROUTE}/${plan.schNo}") }
//                .clickable { navController.navigate(SHOW_SCH_ROUTE) }
        ) {
            Image(
                painter = painterResource(R.drawable.aaa),
                contentDescription = "image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.padding(8.dp)
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = plan.schName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF000000),
                )
                Text(
                    text = "${plan.schStart}~${plan.schEnd}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF000000),
                )
            }
            FloatingActionButton(
                onClick = { navController.navigate("${BAG_NAVIGATION_ROUTE}/${plan.schNo}") },
                shape = RoundedCornerShape(64.dp),
                modifier = Modifier
                    .size(50.dp)
                    .offset(-10.dp),
                containerColor = purple100
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ashley___suitcase_1_new),
                    contentDescription = "image description",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                    colorFilter = ColorFilter.tint(colorResource(id = R.color.purple_300))
                )
            }
        }
    }
}

@Composable
fun SelectSchCard(
    navController: NavController,
    plan: Plan
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(R.color.white_200))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(205.dp)
                .background(color = colorResource(R.color.white_200))
                .clickable { navController.navigate("${SHOW_SCH_ROUTE}/${plan.schNo}") }
        ) {
            Image(
                painter = painterResource(R.drawable.aaa),
                contentDescription = "image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.padding(8.dp)
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = plan.schName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF000000),
                )
                Text(
                    text = "${plan.schStart}~${plan.schEnd}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF000000),
                )
            }
            FloatingActionButton(
                onClick = { navController.navigate("${BAG_NAVIGATION_ROUTE}/${plan.schNo}") },
                shape = RoundedCornerShape(64.dp),
                modifier = Modifier
                    .size(50.dp)
                    .offset(-10.dp),
                containerColor = purple100
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ashley___suitcase_1_new),
                    contentDescription = "image description",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                    colorFilter = ColorFilter.tint(colorResource(id = R.color.purple_300))
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun SelectPreview() {
        SelectSchScreen(
            navController = rememberNavController(),
            requestVM = RequestVM()
            )
}