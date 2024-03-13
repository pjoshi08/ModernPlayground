package com.example.modernplayground.ui.bills

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.modernplayground.R
import com.example.modernplayground.data.Bill
import com.example.modernplayground.ui.components.BillRow
import com.example.modernplayground.ui.components.StatementBody

/**
 * The Bills screen.
 */
@Composable
fun BillsBody(bills: List<Bill>) {
    StatementBody(
        items = bills, colors = { bill -> bill.color },
        amounts = { bill -> bill.amount },
        amountsTotal = bills.map { bill -> bill.amount }.sum(),
        circleLabel = stringResource(R.string.due),
        rows = { bill ->
            BillRow(bill.name, bill.due, bill.amount, bill.color)
        }
    )
}