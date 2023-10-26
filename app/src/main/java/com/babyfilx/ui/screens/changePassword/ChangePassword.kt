package com.babyfilx.ui.screens.changePassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.babyfilx.api.apistate.Response
import com.babyfilx.data.models.FormEvent
import com.babyfilx.ui.theme.dp20
import com.babyfilx.ui.theme.dp200
import com.babyfilx.ui.theme.dp23
import com.babyfilx.utils.commonviews.*
import com.babyflix.mobileapp.R

@Composable
fun ChangePasswordScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: ChangePasswordViewModel = hiltViewModel()
    val scrollState = rememberScrollState()
    val state = viewModel.state
    val status = viewModel.response.collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
    ) {
        TopAppBars(
            title = stringResource(id = R.string.change_password),
            isIcons = Icons.Filled.ArrowBack,
            isDone = false
        ) {
            navController.navigateUp()
        }

        Column(
            modifier = Modifier
                .padding(horizontal = dp20)
        ) {
            EditTextView(
                label = R.string.current_password,
                value = state.oldPassword,
                hint = R.string.enter_current_password,
                errorMessage = state.oldPasswordError,
                modifier = Modifier.padding(top = dp23),
                keyboardType = KeyboardType.Password
            ) {
                viewModel.formEvent(FormEvent.OldPassword(it))
            }
            EditTextView(
                label = R.string.new_password,
                hint = R.string.enter_new_password,
                value = state.password,
                errorMessage = state.passwordError,
                keyboardType = KeyboardType.Password
            ) {
                viewModel.formEvent(FormEvent.Password(it))
            }

            PasswordStrength(password = state.password)
            EditTextView(
                label = R.string.confirm_password,
                hint = R.string.enter_confirm_password,
                value = state.cnPassword,
                errorMessage = state.cnPasswordError,
                keyboardType = KeyboardType.Password
            ) {
                viewModel.formEvent(FormEvent.CnPassword(it))
            }

            ButtonView(name = R.string.save_changes, modifier = Modifier.padding(top = dp200)) {
                viewModel.formEvent(FormEvent.Submit)
            }
        }
    }

    MessageAlert(name = viewModel.message) {
        viewModel.message = ""
        if (status.value?.data?.code == 200) {
            navController.navigateUp()
        }
    }

    when (status.value) {
        is Response.Loading -> ProgressBar()
        else -> {}
    }
}