package com.example.modernplayground.ui.accounts

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.modernplayground.R
import com.example.modernplayground.data.Account
import com.example.modernplayground.ui.components.AccountRow
import com.example.modernplayground.ui.components.StatementBody

/**
 * The Accounts Screen
 */
@Composable
fun AccountsBody(accounts: List<Account>) {
    StatementBody(
        items = accounts,
        colors = { account -> account.color },
        amounts = { account -> account.balance },
        amountsTotal = accounts.map { account -> account.balance }.sum(),
        circleLabel = stringResource(R.string.total),
        rows = { account ->
            AccountRow(
                name = account.name,
                number = account.number,
                amount = account.balance,
                color = account.color
            )
        }
    )
}