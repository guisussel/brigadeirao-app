package com.sussel.brigadeirao

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sussel.brigadeirao.data.DataSource
import com.sussel.brigadeirao.ui.view.DefaultMessageScreen
import com.sussel.brigadeirao.ui.view.OrderSummaryScreen
import com.sussel.brigadeirao.ui.view.SelectOptionsScreen
import com.sussel.brigadeirao.ui.view.StartOrderScreen
import com.sussel.brigadeirao.ui.view.TrackOrderStatusScreen
import com.sussel.brigadeirao.ui.viewmodel.UnifiedOrderViewModel
import com.sussel.brigadeirao.utils.Logger

enum class BrigadeiraoScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Filling(title = R.string.choose_filling),
    Pickup(title = R.string.pickup_date),
    Summary(title = R.string.order_summary),
    TrackOrder(title = R.string.track_order)
}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrigadeiraoAppBar(
    currentScreen: BrigadeiraoScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(text = stringResource(currentScreen.title)) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = navigateUp
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun BrigadeiraoApp(
    viewModel: UnifiedOrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val log = Logger("--BAPP_BrigadeiraoApp")

    log.i("application initialized")

    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen = BrigadeiraoScreen.valueOf(
        backStackEntry?.destination?.route ?: BrigadeiraoScreen.Start.name
    )

    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        // TODO: make this a splashScreen
        DefaultMessageScreen(stringResource(id = R.string.loading), false)
    } else if (uiState.errorMessage != null) {
        log.e("${uiState.errorMessage}")
        DefaultMessageScreen(uiState.errorMessage!!, true)
    } else {
        Scaffold(
            topBar = {
                BrigadeiraoAppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = BrigadeiraoScreen.Start.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = BrigadeiraoScreen.Start.name) {
                    StartOrderScreen(
                        quantityOptions = DataSource.quantityOptions,
                        onNextButtonClicked = {
                            viewModel.setQuantity(it)
                            navController.navigate(BrigadeiraoScreen.Filling.name)
                        },
                        onTrackOrderButtonClicked = {
                            navController.navigate(BrigadeiraoScreen.TrackOrder.name)
                            viewModel.trackOrderStatus()
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
                composable(route = BrigadeiraoScreen.Filling.name) {
                    val context = LocalContext.current
                    SelectOptionsScreen(
                        subtotal = uiState.total,
                        onNextButtonClicked = { navController.navigate(BrigadeiraoScreen.Pickup.name) },
                        onCancelButtonClicked = {
                            cancelOrderAndNavigateBackToStart(viewModel, navController)
                        },
                        options = DataSource.fillings.map { id -> context.resources.getString(id) },
                        onSelectionChanged = { viewModel.setFilling(it) },
                        modifier = Modifier.fillMaxHeight()
                    )
                }
                composable(route = BrigadeiraoScreen.Pickup.name) {
                    SelectOptionsScreen(
                        subtotal = uiState.total,
                        onNextButtonClicked = { navController.navigate(BrigadeiraoScreen.Summary.name) },
                        onCancelButtonClicked = {
                            cancelOrderAndNavigateBackToStart(viewModel, navController)
                        },
                        options = uiState.pickupOptions,
                        onSelectionChanged = { viewModel.setPickupDate(it) },
                        modifier = Modifier.fillMaxHeight()
                    )
                }
                composable(route = BrigadeiraoScreen.Summary.name) {
//                    val context = LocalContext.current
                    OrderSummaryScreen(
                        unifiedOrderUiState = uiState,
                        onSendButtonClicked = { subject: String, summary: String ->
//                            shareOrder(context, subject, summary)
                            viewModel.createOrder()
                            cancelOrderAndNavigateBackToStart(viewModel, navController)
                        },
                        onCancelButtonClicked = {
                            cancelOrderAndNavigateBackToStart(viewModel, navController)
                        },
                        modifier = Modifier.fillMaxHeight()
                    )
                }
                composable(route = BrigadeiraoScreen.TrackOrder.name) {
                    TrackOrderStatusScreen(
                        onBackButtonClicked = {
                            navController.popBackStack(
                                BrigadeiraoScreen.Start.name,
                                inclusive = false
                            )
                        },
                        viewModel
                    )
                }
            }
        }
    }
}

private fun cancelOrderAndNavigateBackToStart(
    viewModel: UnifiedOrderViewModel,
    navController: NavHostController
) {
    viewModel.resetOrder()
    navController.popBackStack(BrigadeiraoScreen.Start.name, inclusive = false)
}
