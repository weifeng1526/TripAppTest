import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tripapp.R
import com.example.tripapp.ui.feature.member.Member
import com.example.tripapp.ui.feature.trip.dataObjects.CrewMmeber
import com.example.tripapp.ui.feature.trip.dataObjects.Destination
import com.example.tripapp.ui.feature.trip.plan.crew.MemberInviteViewModel
import com.example.tripapp.ui.feature.trip.plan.crew.PlanCrewViewModel
import com.example.tripapp.ui.feature.trip.plan.home.PlanHomeViewModel

@Composable
fun MemberInviteScreen(
    navController: NavController,
    memberInviteViewModel: MemberInviteViewModel,
    planCrewViewModel: PlanCrewViewModel,
    schNo: Int,
    schName: String
) {
    var expandAddDialog by remember { mutableStateOf(false) }
    var isInThisCrew by remember { mutableStateOf(emptyList<Any>()) }
    val members by memberInviteViewModel.members.collectAsState()
    val crewMembers by planCrewViewModel.crewOfMembersSatate.collectAsState()

    LaunchedEffect(Unit) {
        memberInviteViewModel.getMembersRequest {
            memberInviteViewModel.setMembers(it)
        }
        planCrewViewModel.getCrewMembersRequest(schNo) {
            planCrewViewModel.setCrewMembers(it)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        // 顯示列表
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(1)
        ) {
            items(members.size) { index ->
                var member = members[index]
                ListItem(
                    modifier = Modifier
                        .border(1.dp, Color.LightGray)
                        .padding(8.dp), // 增加內邊距
                    leadingContent = {
                        Icon(
                            painter = painterResource(id = R.drawable.person),
                            contentDescription = "Person Icon",
                            modifier = Modifier.size(48.dp),
                            tint = Color.Unspecified
                        )
                    },
                    headlineContent = {
                        Text(
                            text = member.memName, // 顯示每個項目的標題
                        )
                    },
                    trailingContent = {
                        IconButton(
                            onClick = {},
                            modifier = Modifier
                                .size(32.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.add_box),
                                contentDescription = "",
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable
                                     {
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
                                    },
                                tint = Color.Unspecified
                            )
                        }
                    },
                    supportingContent = {
                        Text(
                            text = member.memEmail, // 顯示每個項目的描述
                        )
                    },
                )
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
        title = { },
        text = {
            Text(text = "是否將${crewMmeber.memName}加入到${crewMmeber.crewName}")
        },
        onDismissRequest = onDismissRequest,
        dismissButton = {
            Button(onClick = {
                onDismissRequest()
            }) {
                Text(text = "取消")
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm()
                onDismissRequest()
            }) {
                Text(text = "確定")
            }
        }
    )
}