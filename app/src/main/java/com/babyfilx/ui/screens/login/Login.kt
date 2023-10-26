package com.babyfilx.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.babyfilx.api.apistate.Response
import com.babyfilx.data.models.FormEvent
import com.babyfilx.data.models.Screens
import com.babyfilx.ui.theme.*
import com.babyfilx.utils.commonviews.*
import com.babyfilx.utils.removeScreen
import com.babyflix.mobileapp.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {

    val viewModel: LoginViewModel = hiltViewModel()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val state = viewModel.state
    var status by remember {
        mutableStateOf(false)
    }

    var message by remember {
        mutableStateOf("")
    }
    //val status = viewModel.response.collectAsState(initial = null)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = dp20)
            .padding(bottom = dp20)
            .verticalScroll(scrollState)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dp50), contentAlignment = Alignment.Center
        ) {
            DrawableImage(image = R.drawable.babyflix_full_icon)
        }

        TextContent(
            text = R.string.login_to_your_account,
            modifier = Modifier.padding(top = dp60),
            fontWeight = FontWeight.Bold,
            fontSize = sp24
        ) {}

        EditTextView(
            value = state.email,
            hint = R.string.enter_email_address,
            errorMessage = state.emailError,
            modifier = Modifier.padding(top = dp30)
        ) {
            viewModel.formEvent(FormEvent.Email(it))
        }

        EditTextView(
            value = state.password,
            hint = R.string.enter_password,
            label = R.string.password,
            keyboardType = KeyboardType.Password,
            errorMessage = state.passwordError
        ) {
            viewModel.formEvent(FormEvent.Password(it))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dp8), contentAlignment = Alignment.CenterEnd
        ) {
            TextContent(text = R.string.forgetPassword) {
                navController.navigate(Screens.ForgotPassword.root)
            }
        }

        ButtonView(name = R.string.login, modifier = Modifier.padding(top = dp30)) {
            viewModel.formEvent(FormEvent.Submit)
        }

        StringTextContent(
            message = message,
            textAlign = TextAlign.Center,
            color = Color.Red
        )

    }

    if (status)
        ProgressBar()
    LaunchedEffect(Unit) {
        scope.launch {
            viewModel.response.collectLatest {
                when (it) {
                    is Response.Loading -> {
                        message = ""
                        status = true
                    }
                    is Response.Success -> {
                        status = false
                        navController.navigate(Screens.BottomBar.root)
                    }
                    is Response.Error -> {
                        status = false
                        message = it.message
                    }

                }
            }
        }
    }


}