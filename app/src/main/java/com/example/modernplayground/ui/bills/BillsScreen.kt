package com.example.modernplayground.ui.bills

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import com.example.modernplayground.R
import com.example.modernplayground.data.Bill
import com.example.modernplayground.data.UserData
import com.example.modernplayground.ui.components.BillRow
import com.example.modernplayground.ui.components.StatementBody

@Composable
fun BillsScreen(
    bills: List<Bill> = remember { UserData.bills }
) {
    StatementBody(
        modifier = Modifier.clearAndSetSemantics { contentDescription = "Bills" },
        items = bills,
        colors = { bill -> bill.color },
        amounts = { bill -> bill.amount },
        amountsTotal = bills.map { bill -> bill.amount }.sum(),
        circleLabel = stringResource(R.string.due),
        rows = { bill ->
            with(bill) { BillRow(name, due = due, amount = amount, color = color) }
            //BillRow(bill.name, bill.due, bill.amount, bill.color)
        }
    )
}