package com.example.modernplayground.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.modernplayground.data.Email
import com.example.modernplayground.ui.components.EmailDetailAppbar
import com.example.modernplayground.ui.components.ReplyEmailListItem
import com.example.modernplayground.ui.components.ReplyEmailThreadItem
import com.example.modernplayground.ui.components.ReplySearchBar

@Composable
fun ReplyInboxScreen(
    replyHomeUIState: ReplyHomeUIState,
    closeDetailScreen: () -> Unit,
    navigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val emailLazyListState = rememberLazyListState()

    Box(modifier = modifier.fillMaxSize()) {
        ReplyEmailListContent(
            replyHomeUIState = replyHomeUIState,
            emailLazyListState = emailLazyListState,
            modifier = Modifier.fillMaxSize(),
            closeDetailScreen = closeDetailScreen,
            navigateToDetail = navigateToDetail
        )
    }
}

@Composable
fun ReplyEmailListContent(
    replyHomeUIState: ReplyHomeUIState,
    emailLazyListState: LazyListState,
    modifier: Modifier = Modifier,
    closeDetailScreen: () -> Unit,
    navigateToDetail: (Long) -> Unit
) {
    if (replyHomeUIState.selectedEmail != null && replyHomeUIState.isDetailOnlyOpen) {
        BackHandler { closeDetailScreen() }
        ReplyEmailDetail(email = replyHomeUIState.selectedEmail)
    } else {
        ReplyEmailList(
            emails = replyHomeUIState.emails,
            emailLazyListState = emailLazyListState,
            modifier = modifier,
            navigateToDetail = navigateToDetail
        )
    }
}

@Composable
fun ReplyEmailList(
    emails: List<Email>,
    emailLazyListState: LazyListState,
    selectedEmail: Email? = null,
    modifier: Modifier = Modifier,
    navigateToDetail: (Long) -> Unit
) {
    LazyColumn(modifier = modifier, state = emailLazyListState) {
        item { ReplySearchBar(modifier = Modifier.fillMaxWidth()) }
        items(items = emails, key = { it.id }) { email ->
            ReplyEmailListItem(
                email = email,
                isSelected = email.id == selectedEmail?.id,
                navigateToDetail = navigateToDetail
            )
        }
    }
}

@Composable
fun ReplyEmailDetail(
    email: Email,
    isFullScreen: Boolean = true,
    modifier: Modifier = Modifier.fillMaxSize(),
    onBackPressed: () -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .padding(top = 16.dp)
    ) {
        item { EmailDetailAppbar(email, isFullScreen) { onBackPressed() } }
        items(items = email.threads, key = { it.id }) { email ->
            ReplyEmailThreadItem(email = email)
        }
    }
}