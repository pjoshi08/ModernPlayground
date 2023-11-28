package com.example.modernplayground

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    lateinit var navController: TestNavHostController

    @Before
    fun setupRallyNavHost() {
        composeTestRule.setContent {
            // Creates a TestNavHostController
            navController = TestNavHostController(LocalContext.current)
            // Sets a ComposeNavigator to the navController so it can navigate through composables
            navController.navigatorProvider.addNavigator(
                ComposeNavigator()
            )
            RallyNavHost(navController = navController)
        }
    }

    @Test
    fun rallyNavHost_verifyOverviewStartDestination() {
        // Note: To check that your RallyNavHost is working correctly, its hierarchy will
        // have to be composed first before anything else can be tested. This means that your
        // test assertions and verifications will have to be written outside of and after the
        // setContent function.

        // To verify that the correct screen composable is displayed, you can use its
        // contentDescription and assert that it is displayed.
        composeTestRule
            .onNodeWithContentDescription("Overview Screen")
            .assertIsDisplayed()
    }

    @Test
    fun rallyNavHost_clickAllAccount_navigatesToAccounts() {
        composeTestRule
            .onNodeWithContentDescription("All Accounts")
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Accounts Screen")
            .assertIsDisplayed()
    }

    /**
     * You also can use the [navController] to check your assertions by comparing the current
     * String routes to the expected one. To do this, perform a click on the UI, same as
     * in the previous section, and then, compare the current route to the one you expect,
     * using [navController.currentBackStackEntry?.destination?.route].
     */
    @Test
    fun rallyNavHost_clickAllBills_navigateToBills() {
        composeTestRule
            .onNodeWithContentDescription("All Bills")
            .performScrollTo()
            .performClick()

        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(route, "bills")
    }
}