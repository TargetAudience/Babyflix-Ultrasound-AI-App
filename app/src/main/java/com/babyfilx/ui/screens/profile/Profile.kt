package com.babyfilx.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.babyfilx.ui.theme.*
import com.babyfilx.utils.Constant
import com.babyfilx.utils.commonviews.*
import com.babyfilx.utils.datepickers.DatePicker.Companion.setPastDate
import com.babyfilx.utils.logs.loge
import com.babyflix.mobileapp.R

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: ProfileViewModel = hiltViewModel()
    val scrollState = rememberScrollState()
    val state = viewModel.state
    val status = viewModel.response.collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        TopAppBars(
            title = stringResource(id = R.string.profile),
            rightTitle = stringResource(id = R.string.edit),
            isIcons = Icons.Filled.ArrowBack,
            isDone = !viewModel.buttonVisible
        ) {
            if (it == 2) {
                viewModel.buttonVisible = true
            } else {
                navController.navigateUp()
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = dp20)
                .verticalScroll(scrollState)
        ) {

            EditTextView(
                label = R.string.first_name,
                value = state.firstName,
                hint = R.string.enter_first_name,
                errorMessage = state.firstNameError,
                modifier = Modifier.padding(top = dp23),
                enabled = viewModel.buttonVisible
            ) {
                viewModel.formEvent(FormEvent.FirstName(it))
            }

            EditTextView(
                label = R.string.last_name,
                value = state.lastName,
                hint = R.string.enter_last_name,
                errorMessage = state.lastNameError,
                enabled = viewModel.buttonVisible
            ) {
                viewModel.formEvent(FormEvent.LastName(it))
            }

            EditTextView(
                label = R.string.phone_number,
                value = state.phoneNumber,
                hint = R.string.enter_phone_number,
                errorMessage = state.phoneNumberError,
                enabled = viewModel.buttonVisible,
                keyboardType = KeyboardType.Number
            ) {
                viewModel.formEvent(FormEvent.PhoneNumber(it))
            }

            EditTextView(
                label = R.string.e_mail,
                value = state.email,
                errorMessage = state.emailError,
                enabled = viewModel.buttonVisible
            ) {
                viewModel.formEvent(FormEvent.Email(it))
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        if(viewModel.buttonVisible) {
                            context.setPastDate {
                                viewModel.formEvent(FormEvent.ExpectedDueDate(it))
                            }
                        }
                    }) {
                EditTextView(
                    label = R.string.expected_due_date,
                    value = state.expectedDueDate,
                    hint = R.string.enter_eexpected_due_date,
                    errorMessage = state.expectedDueDateError,
                    enabled = false,
                ) {}
            }
            StringTextContent(
                message = "Format: ${Constant.getCurrentDate()}",
                color = hint_color,
                modifier = Modifier.padding(top = dp4)
            )

            AnimatedVisibility(
                visible = viewModel.buttonVisible,
                enter = fadeIn(animationSpec = tween(1000)),
                exit = fadeOut(animationSpec = tween(1000))
            ) {
                ButtonView(
                    name = R.string.save_changes,
                    modifier = Modifier.padding(top = dp79, bottom = dp20)
                ) {
                    loge("iddjndnj")
                    viewModel.formEvent(FormEvent.Submit)
                }
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