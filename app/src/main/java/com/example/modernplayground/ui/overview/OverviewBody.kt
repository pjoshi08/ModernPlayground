package com.example.modernplayground.ui.overview

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.modernplayground.R
import com.example.modernplayground.RallyScreen
import com.example.modernplayground.data.UserData
import com.example.modernplayground.ui.components.AccountRow
import com.example.modernplayground.ui.components.BillRow
import com.example.modernplayground.ui.components.RallyAlertDialog
import com.example.modernplayground.ui.components.RallyDivider
import com.example.modernplayground.ui.components.formatAmount
import com.example.modernplayground.ui.theme.RallyTheme
import java.util.Locale

@Composable
fun OverviewBody(onScreenChange: (RallyScreen) -> Unit = {}) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        AlertCard()
        Spacer(Modifier.height(RallyDefaultPadding))
        AccountsCard(onScreenChange)
        Spacer(Modifier.height(RallyDefaultPadding))
        BillsCard(onScreenChange)
    }
}

/**
 * The Alerts card within the Rally Overview screen.
 */
@Composable
private fun AlertCard() {
    var showDialog by remember { mutableStateOf(false) }
    val alertMessage = "Heads up, you've used up 90% of your Shopping budget for this month."

    if (showDialog) {
        RallyAlertDialog(
            onDismiss = { showDialog = false },
            bodyText = alertMessage,
            buttonText = "Dismiss".uppercase(Locale.getDefault())
        )
    }

    var currentTargetElevation by remember { mutableStateOf(1.dp) }
    LaunchedEffect(Unit) {
        // Start the animation
        currentTargetElevation = 8.dp
    }
    val animatedElevation = animateDpAsState(
        targetValue = currentTargetElevation,
        animationSpec = tween(durationMillis = 500),
        finishedListener = {
            currentTargetElevation = if (currentTargetElevation > 4.dp) {
                1.dp
            } else {
                8.dp
            }
        }
    )
    Card(elevation = animatedElevation.value) {
        Column {
            AlertHeader {
                showDialog = true
            }
            RallyDivider(
                modifier = Modifier.padding(start = RallyDefaultPadding, end = RallyDefaultPadding)
            )
            AlertItem(alertMessage)
        }
    }
}

@Preview
@Composable
fun AlertCardPreview() {
    RallyTheme {
        OverviewBody()
    }
}

@Composable
private fun AlertHeader(onClickSeeAll: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(RallyDefaultPadding)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Alerts",
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        TextButton(
            onClick = onClickSeeAll,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Text(text = "SEE ALL", style = MaterialTheme.typography.button)
        }
    }
}

@Composable
private fun AlertItem(message: String) {
    Row(
        modifier = Modifier
            .padding(RallyDefaultPadding)
            // Regard the whole row as one semantics node. This way each row will receive focus as
            // a whole and the focus bounds will be around the whole row content. The semantics
            // properties of the descendants will be merged. If we'd use clearAndSetSemantics instead,
            // we'd have to define the semantics properties explicitly.
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = {}, modifier = Modifier
            .align(Alignment.Top)
            .clearAndSetSemantics {}) {
            Icon(Icons.Filled.Sort, contentDescription = null)
        }
    }
}

/**
 * The Accounts card within the Rally Overview Screen
 */
@Composable
private fun AccountsCard(onScreenChange: (RallyScreen) -> Unit) {
    val amount = UserData.accounts.map { account -> account.balance }.sum()
    OverviewScreenCard(
        title = stringResource(R.string.accounts),
        amount = amount,
        onClickSeeAll = { onScreenChange(RallyScreen.Accounts) },
        data = UserData.accounts,
        values = { it.balance },
        colors = { it.color },
    ) { account ->
        AccountRow(
            name = account.name,
            number = account.number,
            amount = account.balance,
            color = account.color
        )
    }
}

/**
 * The Bills card within the Rally Overview screen
 */
@Composable
private fun BillsCard(onScreenChange: (RallyScreen) -> Unit) {
    val amount = UserData.bills.map { bill -> bill.amount }.sum()
    OverviewScreenCard(
        title = stringResource(R.string.bills),
        amount = amount,
        onClickSeeAll = { onScreenChange(RallyScreen.Bills) },
        values = { it.amount },
        colors = { it.color },
        data = UserData.bills
    ) { bill ->
        BillRow(name = bill.name, due = bill.due, amount = bill.amount, color = bill.color)
    }
}

/**
 * Base structure for cards in the Overview screen
 */
@Composable
private fun <T> OverviewScreenCard(
    title: String,
    amount: Float,
    onClickSeeAll: () -> Unit,
    values: (T) -> Float,
    colors: (T) -> Color,
    data: List<T>,
    row: @Composable (T) -> Unit
) {
    Card {
        Column {
            Column(Modifier.padding(RallyDefaultPadding)) {
                Text(text = title, style = MaterialTheme.typography.subtitle2)
                val amountText = "$" + formatAmount(amount)
                Text(text = amountText, style = MaterialTheme.typography.h2)
            }
            OverviewDivider(data, values, colors)
            Column(Modifier.padding(start = 16.dp, top = 4.dp, end = 8.dp)) {
                data.take(SHOWN_ITEMS).forEach { row(it) }
                SeeAllButton(onClick = onClickSeeAll)
            }
        }
    }
}

@Composable
private fun <T> OverviewDivider(
    data: List<T>,
    values: (T) -> Float,
    colors: (T) -> Color
) {
    Row(Modifier.fillMaxWidth()) {
        data.forEach { item: T ->
            Spacer(
                modifier = Modifier
                    .weight(values(item))
                    .height(1.dp)
                    .background(colors(item))
            )
        }
    }
}

@Composable
private fun SeeAllButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .height(44.dp)
            .fillMaxWidth()
    ) {
        Text(stringResource(R.string.see_all))
    }
}

private val RallyDefaultPadding = 12.dp

private const val SHOWN_ITEMS = 3