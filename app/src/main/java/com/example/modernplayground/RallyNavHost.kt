package com.example.modernplayground

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.modernplayground.ui.accounts.AccountsScreen
import com.example.modernplayground.ui.accounts.SingleAccountScreen
import com.example.modernplayground.ui.bills.BillsScreen
import com.example.modernplayground.ui.overview.OverviewScreen

@Composable
fun RallyNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // The 3 main parts of Navigation are the NavController, NavGraph, and NavHost.
    // The NavController is always associated with a single NavHost composable.
    // The NavHost acts as a container and is responsible for displaying the current
    // destination of the graph. As you navigate between composables, the content of
    // the NavHost is automatically recomposed. It also links the NavController with
    // a navigation graph (NavGraph) that maps out the composable destinations to
    // navigate between. It is essentially a collection of fetchable destinations.
    NavHost(
        navController = navController,
        startDestination = Overview.route,
        modifier = modifier
    ) {
        composable(route = Overview.route) {
            OverviewScreen(
                onClickSeeAllAccounts = {
                    /// keeping the navController at the top level of your
                    // navigation hierarchy and hoisted to the level of your
                    // App composable (instead of passing it directly into,
                    // for example, OverviewScreen) makes it easy to preview,
                    // reuse and test OverviewScreen composable in isolation –
                    // without having to rely on an actual or mocked navController
                    // instances. Passing callbacks instead also allows quick changes
                    // to your click events!
                    navController.navigateSingleTopTo(Accounts.route)
                },
                onClickSeeAllBills = {
                    navController.navigateSingleTopTo(Bills.route)
                },
                onAccountClick = { accountType ->
                    navController.navigateToSingleAccount(accountType)
                }
            )
        }
        composable(route = Accounts.route) {
            AccountsScreen(
                onAccountClick = { accountType ->
                    navController.navigateToSingleAccount(accountType)
                }
            )
        }
        composable(route = Bills.route) {
            BillsScreen()
        }
        composable(
            // "route/{argument}"
            route = SingleAccount.routeWithArgs,
            // Second step to this is to make this composable aware that it should
            // accept arguments. You do that by defining its arguments parameter.
            // You could define as many arguments as you need, as the composable
            // function by default accepts a list of arguments. In your case, you
            // just need to add a single one called accountTypeArg and add some
            // additional safety by specifying it as type String. If you don't
            // set a type explicitly, it will be inferred from the default value
            // of this argument:
            arguments = SingleAccount.arguments,
            deepLinks =  SingleAccount.deepLinks
        ) { navBackEntry ->
            // In Compose Navigation, each NavHost composable function has access
            // to the current NavBackStackEntry - a class which holds the information
            // on the current route and passed arguments of an entry in the back stack.
            // You can use this to get the required arguments list from navBackStackEntry
            // and then search and retrieve the exact argument you need, to pass it down
            // further to your composable screen.

            // Retrieve the passed arg
            val accountType = navBackEntry.arguments?.getString(SingleAccount.accountTypeArg)
            // Pass the accountType to SingleAccountScreen
            SingleAccountScreen(accountType)
        }
    }
}

/// [Back Stack Doc](https://developer.android.com/guide/navigation/backstack/multi-back-stacks)
fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        // pop up to the start destination of the graph to avoid building up a large stack
        // of destinations on the back stack as you select tabs
        popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) {
            saveState = true
        }
        // this makes sure there will be at most one copy of a given destination on the
        // top of the back stack
        launchSingleTop = true
        // determines whether this navigation action should restore any state
        // previously saved by PopUpToBuilder.saveState or the popUpToSaveState
        // attribute. Note that, if no state was previously saved with the destination
        // ID being navigated to, this has no effect
        restoreState = true
    }

private fun NavHostController.navigateToSingleAccount(accountType: String) {
    this.navigateSingleTopTo("${SingleAccount.route}/$accountType")
}