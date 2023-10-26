package com.babyfilx.ui.screens.createaccount

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.babyfilx.utils.logs.loge
import com.babyfilx.utils.removeScreen
import com.babyflix.mobileapp.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignUpMobileScreen(navController: NavController) {

    val viewModel: SignUpViewModel = hiltViewModel()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val state = viewModel.state
    var status by remember {
        mutableStateOf(false)
    }

    var message by remember {
        mutableStateOf("")
    }

    // Observe the navigation trigger from the ViewModel
    val navigateToNextScreen = viewModel.navigateToNextScreen.collectAsState()

    //val status = viewModel.response.collectAsState(initial = null)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = dp25)
            .padding(bottom = dp5)
    ) {
        Image(
            painter = painterResource(id = R.drawable.back_icon),
            contentDescription = "back",
            modifier = Modifier.padding(top = dp65)
                .clickable {
                    navController.navigateUp()
                },
        )

        TextContentView(
            text = R.string.mobile_phone,
            modifier = Modifier
                .padding(top = dp40)
                .width(dp250),
            fontWeight = FontWeight.Bold,
            fontSize = sp24
        ) {}

        Text(
            text = stringResource(R.string.mobile_phone_desc),
            fontFamily = fontsFamilyPoppins,
            fontWeight = FontWeight.Normal,
            fontSize = sp16,
            color = create_account_grey,
            modifier = Modifier
                .padding(top = dp10)
        )

        NewEditTextView(
            value = state.phoneNumber,
            hint = R.string.mobile_hint,
            errorMessage = state.phoneNumberError,
            modifier = Modifier.padding(top = dp18),
            keyboardType = KeyboardType.Number,
            maxChar = false
        ) {
            viewModel.formMobileEvent(FormEvent.PhoneNumber(it))
        }


        StringTextContent(
            message = message,
            textAlign = TextAlign.Center,
            color = Color.Red
        )

        Box(modifier = Modifier.weight(1f)) {
            NewButton(
                label = R.string.next,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dp10)
                    .height(dp60)
                    .align(Alignment.BottomCenter)
            ) {
                viewModel.formMobileEvent(FormEvent.Submit)
            }

        }

        // Use LaunchedEffect to navigate when the trigger becomes true
        LaunchedEffect(navigateToNextScreen.value) {
            loge("Navigate ${navigateToNextScreen.value}")
            if (navigateToNextScreen.value) {
                navController.navigate(Screens.SignUpClinic.root)
                // Reset the navigation trigger in the ViewModel
                viewModel.onNavigationComplete()
            }
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
}