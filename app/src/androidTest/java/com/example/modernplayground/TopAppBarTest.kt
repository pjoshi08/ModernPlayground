package com.example.modernplayground

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import com.example.modernplayground.ui.components.RallyTopAppBar
import org.junit.Rule
import org.junit.Test

class TopAppBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // In a Compose test, we can start the app's main activity similarly to how you would do it
    // in the Android View world using Espresso, for example. You can do this with
    // createAndroidComposeRule
    // @get:Rule
    // val composeTestRule2 = createAndroidComposeRule(RallyActivity::class.java)
    // However, with Compose, we can simplify things considerably by testing a component
    // in isolation. You can choose what Compose UI content to use in the test. This is done with
    // the setContent method of the ComposeTestRule, and you can call it anywhere (but just once).

    // Compose Testing Cheat Sheet: https://developer.android.com/jetpack/compose/testing-cheatsheet
    @Test
    fun rallyTopAppBarTest_currentTabSelected() {
        val allScreens = RallyScreen.values().toList()
        // If we do not wrap with RallyTheme here, the topappbar is displayed with default
        // light theme
        composeTestRule.setContent {
            RallyTopAppBar(
                allScreens = allScreens,
                onTabSelected = { },
                currentScreen = RallyScreen.Accounts
            )
        }

        //Thread.sleep(10000)

        // Pattern: composeTestRule{.finder}{.assertion}{.action}
        composeTestRule
            .onNodeWithContentDescription(RallyScreen.Accounts.name, ignoreCase = true)
            .assertIsSelected()
    }

    @Test
    fun rallyTopAppBarTest_currentLabelExists() {
        val allScreens = RallyScreen.values().toList()
        composeTestRule.setContent {
            RallyTopAppBar(
                allScreens = allScreens,
                onTabSelected = {},
                currentScreen = RallyScreen.Accounts
            )
        }

        // Warning: Composables don't have IDs and you can't use the Node numbers shown in
        // the tree to match them. If matching a node with its semantics properties is
        // impractical or impossible, you can use the testTag modifier with the hasTestTag
        // matcher as a last resort.

        // Semantics: https://developer.android.com/jetpack/compose/testing#semantics

        //composeTestRule.onRoot(useUnmergedTree = true).printToLog("TAG >>>>>>")
        composeTestRule.onRoot().printToLog("TAG >>>>>>")

        composeTestRule
            //.onNodeWithText(RallyScreen.Accounts.name.uppercase()) // this fails
            .onNodeWithContentDescription(RallyScreen.Accounts.name)
            .assertExists()
    }

    @Test
    fun rallyTopAppBarTest_currentLabelExists2() {
        val allScreens = RallyScreen.values().toList()
        composeTestRule.setContent {
            RallyTopAppBar(
                allScreens = allScreens,
                onTabSelected = {},
                currentScreen = RallyScreen.Accounts
            )
        }

        composeTestRule.onRoot(useUnmergedTree = true).printToLog("TAG >>>>>>")

        composeTestRule
            .onNode(
                hasText(RallyScreen.Accounts.name.uppercase()) and
                        hasParent(
                            hasContentDescription(RallyScreen.Accounts.name)
                        ),
                useUnmergedTree = true
            )
            .assertExists()
    }

    @Test
    fun rallyTopAppBarTest_tabSelection() {
        val allScreens = RallyScreen.values().toList()
        composeTestRule.setContent {
            RallyApp()
        }

        composeTestRule
            .onNodeWithContentDescription(RallyScreen.Accounts.name)
            .performClick()
        composeTestRule
            .onNode(
                hasText(RallyScreen.Accounts.name.uppercase()) and
                        hasParent(
                            hasContentDescription(RallyScreen.Accounts.name)
                        ),
                useUnmergedTree = true
            )
            .assertExists()
    }
}