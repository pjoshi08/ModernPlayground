package com.example.modernplayground.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults.filledIconButtonColors
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.modernplayground.R
import com.example.modernplayground.data.Email

@Composable
fun ReplySearchBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(colorScheme.background, CircleShape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = stringResource(id = R.string.search),
            modifier = Modifier.padding(start = 16.dp),
            tint = colorScheme.outline
        )
        Text(
            text = stringResource(id = R.string.search_replies),
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            style = typography.bodyMedium,
            color = colorScheme.outline
        )
        ReplyProfileImage(
            drawableResource = R.drawable.avatar_6,
            description = stringResource(id = R.string.profile),
            modifier = Modifier
                .padding(12.dp)
                .size(32.dp)
        )
    }
}

@Composable
fun EmailDetailAppbar(
    email: Email,
    isFullScreen: Boolean,
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = if (isFullScreen) Alignment.CenterHorizontally
                else Alignment.Start
            ) {
                Text(
                    text = email.subject,
                    style = typography.titleMedium,
                    color = colorScheme.onSurfaceVariant
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = "${email.threads.size} ${stringResource(id = R.string.messages)}",
                    style = typography.labelMedium,
                    color = colorScheme.outline
                )
            }
        },
        navigationIcon = {
            if (isFullScreen) {
                FilledIconButton(
                    onClick = onBackPressed,
                    modifier = Modifier.padding(8.dp),
                    colors = filledIconButtonColors(
                        containerColor = colorScheme.surface,
                        contentColor = colorScheme.onSurface
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_button),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { /* Click Implementation */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(id = R.string.more_options_button),
                    tint = colorScheme.onSurfaceVariant
                )
            }
        }
    )
}