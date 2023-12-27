package com.example.modernplayground.data

import androidx.annotation.DrawableRes

data class Email(
    val id: Long,
    val sender: Account,
    val recipients: List<Account> = emptyList(),
    val subject: String,
    val body: String,
    val attachments: List<EmailAttachment> = emptyList(),
    val isImportant: Boolean = false,
    val isStarred: Boolean = false,
    val mailbox: MailboxType = MailboxType.INBOX,
    val createdAt: String,
    val threads: List<Email> = emptyList()
)

enum class MailboxType {
    INBOX, DRAFTS, SENT, SPAM, TRASH
}

data class EmailAttachment(
    @DrawableRes val resId: Int,
    val contentDesc: String
)