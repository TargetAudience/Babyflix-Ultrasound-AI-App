package com.babyfilx.ui.screens.createaccount

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.babyfilx.api.apistate.Response
import com.babyfilx.data.models.FormEvent
import com.babyfilx.data.models.Screens
import com.babyfilx.ui.theme.*
import com.babyfilx.utils.commonviews.*
import com.babyfilx.utils.logs.loge
import com.babyflix.mobileapp.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignUpClinicScreen(navController: NavController) {

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

    val mCities = listOf(
        "Delhi", "Mumbai", "Chennai", "Kolkata",
        "Hyderabad", "Bengaluru", "Pune"
    )

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
            modifier = Modifier
                .padding(top = dp65)
                .clickable {
                    navController.navigateUp()
                },
        )

        TextContentView(
            text = R.string.signup_clinic,
            modifier = Modifier
                .padding(top = dp40)
                .width(dp250),
            fontWeight = FontWeight.Bold,
            fontSize = sp24
        ) {}

        Text(
            text = stringResource(R.string.signup_link_your_clinic),
            fontFamily = fontsFamilyPoppins,
            fontWeight = FontWeight.Normal,
            fontSize = sp16,
            color = create_account_grey,
            modifier = Modifier
                .padding(top = dp10)
        )


        MyContent("Select State", mCities)

        MyContent("Select Clinic", mCities)

        NewEditTextView(
            value = state.clinicCode,
            hint = R.string.enter_clinic_code,
            errorMessage = state.clinicCodeError,
            modifier = Modifier.padding(top = dp18),
            keyboardType = KeyboardType.Number,
        ) {
            viewModel.formClinicEvent(FormEvent.ClinicCode(it))
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
                viewModel.formClinicEvent(FormEvent.Submit)
            }

        }

        // Use LaunchedEffect to navigate when the trigger becomes true
        LaunchedEffect(navigateToNextScreen.value) {
            loge("Navigate ${navigateToNextScreen.value}")
            if (navigateToNextScreen.value) {
                navController.navigate(Screens.SignUpPassword.root)
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


@Composable
fun MyContent(item: String, mCities: List<String>) {
    // Declaring a boolean value to store
    // the expanded state of the Text Field
    var mExpanded by remember { mutableStateOf(false) }

    // Create a list of cities


    // Create a string value to store the selected city
    var mSelectedText by remember { mutableStateOf("") }

    // Up Icon when expanded and down icon when collapsed
    val icon = if (mExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column(Modifier.padding(top = dp18)) {
        // Create an Outlined Text Field
        // with icon and not expanded
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = mSelectedText,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dp70), // Set the height to 60.dp
                readOnly = true, // Make it read-only
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = new_black_text, // Set the border color to #282829
                    unfocusedBorderColor = create_account_grey // Set the border color to #282829
                ),
                label = {
                    Text(
                        text = item,
                        fontFamily = fontsFamilyPoppins,
                        fontSize = sp16,
                        fontWeight = FontWeight.Normal,
                        color = new_black_text,
                    )
                },
                trailingIcon = {
                    Icon(icon, "contentDescription",
                        Modifier.clickable { mExpanded = !mExpanded })
                }
            )
        }

        // Create a drop-down menu with list of cities,
        // when clicked, set the Text Field text as the city selected
        DropdownMenu(
            expanded = mExpanded,
            onDismissRequest = { mExpanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            mCities.forEach { label ->
                DropdownMenuItem(onClick = {
                    mSelectedText = label
                    mExpanded = false
                }) {
                    Text(
                        text = label,
                        fontFamily = fontsFamilyPoppins,
                        fontSize = sp16,
                        fontWeight = FontWeight.Normal,
                        color = new_black_text,
                    )
                }
            }
        }
    }
}
