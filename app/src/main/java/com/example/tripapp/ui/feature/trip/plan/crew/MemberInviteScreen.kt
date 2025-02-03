import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tripapp.R
import com.example.tripapp.ui.feature.member.Member
import com.example.tripapp.ui.feature.member.home.MemberIcon
import com.example.tripapp.ui.feature.member.home.memIcon
import com.example.tripapp.ui.feature.trip.dataObjects.CrewMmeber
import com.example.tripapp.ui.feature.trip.dataObjects.Destination
import com.example.tripapp.ui.feature.trip.plan.crew.MEMBER_INVITE_ROUTE
import com.example.tripapp.ui.feature.trip.plan.crew.MemberInviteViewModel
import com.example.tripapp.ui.feature.trip.plan.crew.PlanCrewViewModel
import com.example.tripapp.ui.feature.trip.plan.home.PlanHomeViewModel
import com.example.tripapp.ui.theme.purple100
import com.example.tripapp.ui.theme.purple500
import com.example.tripapp.ui.theme.white100
import com.example.tripapp.ui.theme.white300
import com.example.tripapp.ui.theme.white400

@Composable
fun MemberInviteScreen(
    navController: NavController,
    memberInviteViewModel: MemberInviteViewModel,
    planCrewViewModel: PlanCrewViewModel,
    schNo: Int,
    schName: String
) {
    var expandAddDialog by remember { mutableStateOf(false) }
    val members by memberInviteViewModel.members.collectAsState()
    val crewMembers by planCrewViewModel.crewOfMembersSatate.collectAsState()
    val membersFilterResult by memberInviteViewModel.membersFiltedFromCrew.collectAsState()
    val memIcons = memIcon()

    LaunchedEffect(Unit) {
        memberInviteViewModel.getMembersRequest {
            memberInviteViewModel.setMembers(it)
        }
    }

    LaunchedEffect(members) {
        if (members.size > 0) {
            Log.d("members", "${members}")
            planCrewViewModel.getCrewMembersRequest(schNo) {
                planCrewViewModel.setCrewMembers(it)
            }
        }
    }

    LaunchedEffect(crewMembers) {
        if (crewMembers.size > 0) {
            Log.d("crewMembers", "${crewMembers}")
            memberInviteViewModel.filterMemberInThisCrew(crewMembers, members)
        }
    }

    LaunchedEffect(membersFilterResult) {
        if (membersFilterResult.size > 0) {
            Log.d("membersFilterResult", "${membersFilterResult}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(white100)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(white300),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 20.dp, end = 10.dp, top = 10.dp, bottom = 10.dp)
                    .border(1.dp, purple100, RoundedCornerShape(8.dp))
                    .wrapContentSize()
                    .clip(RoundedCornerShape(8.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.group),
                    contentDescription = "schedule Icon",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(horizontal = 6.dp),
                    tint = purple500
                )
                Text(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 3.dp),
                    fontSize = 20.sp,
                    text = "我的好友: ${members.size}"
                )
            }
        }
        // 顯示列表
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(1)
        ) {
            items(members.size) { index ->
                var member = members[index]
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        modifier = Modifier.background(white100),
                        colors = ListItemDefaults.colors(containerColor = white100),
                        leadingContent = {
                            Image(
                                painter = painterResource(id = memIcons[member.memNo].img),
                                contentDescription = "Person Icon",
                                modifier = Modifier.size(58.dp),
                            )
                        },
                        headlineContent = {
                            Text(
                                text = member.memName,
                                style = TextStyle(fontSize = 16.sp),
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        },
                        supportingContent = {
                            Text(
                                text = member.memEmail,
                                style = TextStyle(fontSize = 16.sp)
                            )
                        },
                        trailingContent = {
                            Log.d("members.size", "${members.size}")
                            if (membersFilterResult.isNotEmpty()) {
                                Log.d("membersFilterResult[index]", "${membersFilterResult[index]}")
                                if (!membersFilterResult[index]) {
                                    IconButton(
                                        onClick = {
                                            val newCrewMember = CrewMmeber(
                                                crewNo = 0,
                                                schNo = schNo,
                                                memNo = member.memNo,
                                                memName = member.memName,
                                                memIcon = byteArrayOf(0),
                                                crewPeri = 1,
                                                crewIde = 1,
                                                crewName = schName,
                                                crewInvited = 3
                                            )
                                            planCrewViewModel.setCrewMember(newCrewMember)
                                            expandAddDialog = true
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.add_box),
                                            contentDescription = "",
                                            modifier = Modifier
                                                .size(42.dp),
                                            tint = purple500
                                        )
                                    }
                                }
                            }
                        }
                    )
                    Divider(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(2.dp),
                        color = white400
                    )
                }
            }
        }
    }
    if (expandAddDialog == true) {
        showDstDeleteDialog(
            onConfirm = {
                val newCrewMember = planCrewViewModel.getCrewMember()
                planCrewViewModel.createCrewRequest(newCrewMember)
                navController.popBackStack()
            },
            onDismissRequest = { expandAddDialog = false },
            crewMmeber = planCrewViewModel.getCrewMember()
        )
    }
}

@Composable
fun showDstDeleteDialog(
    crewMmeber: CrewMmeber,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier.wrapContentSize(),
        shape = RectangleShape,
        title = { },
        text = {
            Text(
                text = "是否將${crewMmeber.memName}加入到${crewMmeber.crewName}",
                style = TextStyle(
                    fontSize = 20.sp
                )
            )
        },
        onDismissRequest = onDismissRequest,
        dismissButton = {
            Button(
                onClick = { onDismissRequest() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = purple500,
                    contentColor = white100
                )
            ) {
                Text(
                    text = "取消",
                    style = TextStyle(
                        fontSize = 18.sp
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismissRequest()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = purple500,
                    contentColor = white100
                )
            ) {
                Text(
                    text = "確定",
                    style = TextStyle(
                        fontSize = 18.sp
                    )
                )
            }
        }
    )
}