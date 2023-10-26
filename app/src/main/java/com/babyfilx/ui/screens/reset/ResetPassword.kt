package com.babyfilx.ui.screens.reset

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.babyfilx.api.apistate.Response
import com.babyfilx.data.models.FormEvent
import com.babyfilx.ui.theme.*
import com.babyfilx.utils.commonviews.*
import com.babyflix.mobileapp.R

@Composable
fun ResetPasswordScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: ResetPasswordViewModel = hiltViewModel()
    val scrollState = rememberScrollState()
    val state = viewModel.state
    val status = viewModel.response.collectAsState(initial = null)

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
            DrawableImage()
        }

        TextContent(
            text = R.string.reset_password,
            modifier = Modifier.padding(top = dp40, start = dp4),
            fontWeight = FontWeight.Bold,
            fontSize = sp24
        ) {}

        EditTextView(
            label = R.string.password,
            hint = R.string.enter_password,
            value = state.password,
            errorMessage = state.passwordError,
            keyboardType = KeyboardType.Password,
            modifier = Modifier.padding(top = dp30)
        ) {
            viewModel.formEvent(FormEvent.Password(it))
        }

        PasswordStrength(password = state.password, modifier = Modifier.padding(top = dp20, start = dp4))
        EditTextView(
            label = R.string.confirm_password,
            hint = R.string.enter_confirm_password,
            value = state.cnPassword,
            errorMessage = state.cnPasswordError,
            keyboardType = KeyboardType.Password,
            modifier = Modifier.padding(top = dp5)
        ) {
            viewModel.formEvent(FormEvent.CnPassword(it))
        }

        CheckBoxWithCondition(isCheck = state.isAccept, errorMessage = state.isAcceptError,navController) {
            viewModel.formEvent(FormEvent.Accept(!it))
        }

        ButtonView(name = R.string.submit, modifier = Modifier.padding(top = dp50)) {
            viewModel.formEvent(FormEvent.Submit)
        }
    }


    MessageAlert(name = viewModel.message) {
        viewModel.message = ""
        if (status.value?.data?.code == 200) {
           // navController.navigateUp()
        }
    }

    when (status.value) {
        is Response.Loading -> ProgressBar()
        else -> {}
    }
}