package com.example.tripapp.ui.feature.member.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tripapp.R
import com.example.tripapp.ui.feature.member.login.MEMBER_LOGIN_ROUTE
import com.example.tripapp.ui.feature.member.login.MemberLoginScreen
import com.example.tripapp.ui.feature.member.turfav.TUR_FAV_ROUTE
import com.example.tripapp.ui.feature.trip.plan.home.PLAN_HOME_ROUTE
import com.example.tripapp.ui.theme.black600
import com.example.tripapp.ui.theme.purple200
import com.example.tripapp.ui.theme.white200
import com.example.tripapp.ui.theme.white300
import kotlinx.coroutines.coroutineScope

@Composable
fun MemberSignUpRoute(
    viewModel: MemberSignUpViewModel = viewModel(),
    navController: NavHostController
) {
    val inputName by viewModel.name.collectAsState()

    MemberSignUpScreen(
        viewModel = viewModel,
//        onPlanHomeClick = { navController.navigate(PLAN_HOME_ROUTE) },
        onLoginClick =  { navController.navigate(MEMBER_LOGIN_ROUTE) },
        )
}

@Preview
@Composable
fun PreviewMemberSignUpRoute() {
    MemberSignUpScreen(
        viewModel = viewModel(),
    )
}

@Composable
fun MemberSignUpScreen(
    viewModel: MemberSignUpViewModel = viewModel(),
//    onPlanHomeClick: () -> Unit = { }
    onLoginClick: () -> Unit = { },

    ) {
    val name by viewModel.name.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val icon by viewModel.icon.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState() // 監聽錯誤訊息
    val isButtonEnabled by viewModel.isButtonEnabled.collectAsState()
    var confirmPassword by remember { mutableStateOf("") }
    var passwordsMatch by remember { mutableStateOf(true) }
    val isSingUpSuccess by viewModel.isSignUpSuccess.collectAsState()

    LaunchedEffect(isSingUpSuccess) {
        if (isSingUpSuccess) {
//            onPlanHomeClick()
            onLoginClick()
        }
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = white200)
    ) {
        //logo圖案要再改
        Image(
            painter = painterResource(R.drawable.lets_icons__suitcase_light),
            contentDescription = "AppLogo",
            modifier = Modifier
                .padding(top = 16.dp)
                .size(70.dp)
                .graphicsLayer {
                    this.alpha = 0.5f
                }
        )
        Text(
            text = "旅友 TravelMate",
            modifier = Modifier
                .graphicsLayer {
                    this.alpha = 0.5f
                }
        )

        Spacer(modifier = Modifier.padding(16.dp))

        Text(
            text = "註冊",
            fontSize = 24.sp,
        )

        Spacer(modifier = Modifier.padding(12.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "電子信箱",
                fontSize = 12.sp,
            )
            OutlinedTextField(
                value = email,
                onValueChange = viewModel::onEmailChanged,
                placeholder = { Text(text = "example@mail.com") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "password"
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "clear",
                        modifier = Modifier.clickable {
                            viewModel.onEmailChanged("")
                        }
                    )
                },
            )
        }
        Spacer(modifier = Modifier.padding(4.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "暱稱",
                fontSize = 12.sp,
            )
            OutlinedTextField(
                value = name,
                onValueChange = viewModel::onNameChanged,
                placeholder = { Text(text = "請輸入英數文字") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "password"
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "clear",
                        modifier = Modifier.clickable {
                            viewModel.onNameChanged("")
                        }
                    )
                },
            )
        }

        Spacer(modifier = Modifier.padding(4.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "密碼",
                fontSize = 12.sp,
            )
            OutlinedTextField(
                value = password,
                onValueChange = {
                    viewModel.onPasswordChange(it)
                    passwordsMatch = it == confirmPassword
                },
                placeholder = { Text(text = "請輸入6-8位數英數文字") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "password"
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "clear",
                        modifier = Modifier.clickable {
                            viewModel.onPasswordChange("")
                        }
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )
        }

        Spacer(modifier = Modifier.padding(4.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "確認密碼",
                fontSize = 12.sp,
            )
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    passwordsMatch = password == it // 即時更新比對結果
                },
                placeholder = { Text(text = "請再輸入一次密碼") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "password"
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "clear",
                        modifier = Modifier.clickable {
                            confirmPassword = ""
                        }
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )
        }

        Spacer(modifier = Modifier.padding(20.dp))

        if (!errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage?: "",
                color = Color.Red,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        Button(
            onClick = {
                when {
                    email.isBlank() -> viewModel.showErrorMessage("請輸入電子郵件")
                    !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+(\\.[a-zA-Z]{2,})?$".toRegex()) ->
                        viewModel.showErrorMessage("電子郵件格式不正確")
                    name.isBlank() -> viewModel.showErrorMessage("請輸入暱稱")
                    name.length !in 1..30 -> viewModel.showErrorMessage("暱稱必須在1至30個字元之間")
                    password.isBlank() || confirmPassword.isBlank() -> viewModel.showErrorMessage("請輸入密碼與確認密碼")
                    password != confirmPassword -> viewModel.showErrorMessage("密碼與確認密碼不一致")
                    password.length < 6 || password.length > 8 -> viewModel.showErrorMessage("密碼長度必須為6到8位數字")
                    else -> {
                        viewModel.clearErrorMessage() // 清空錯誤訊息
//                        onPlanHomeClick()
                        onLoginClick()
                        viewModel.onSignUpClick()
                    }
                }
            },
            //,
            enabled = isButtonEnabled,
            modifier = Modifier
                .padding(),
            colors = ButtonDefaults.buttonColors(
                containerColor = purple200
            )
        ) {
            Text(
                text = "註冊",
                fontSize = 16.sp,
                color = white300
            )
        }
    }
}
