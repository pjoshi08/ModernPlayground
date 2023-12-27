package com.example.modernplayground.ui

import androidx.lifecycle.ViewModel
import com.example.modernplayground.data.Email
import com.example.modernplayground.data.LocalEmailsDataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReplyHomeViewModel : ViewModel() {
    // UI State exposed to the UI
    private val _uiState = MutableStateFlow(ReplyHomeUIState(loading = true))
    val uiState: StateFlow<ReplyHomeUIState> = _uiState

    init {
        initEmailList()
    }

    private fun initEmailList() {
        val emails = LocalEmailsDataProvider.allEmails
        _uiState.value = ReplyHomeUIState(
            emails = emails,
            selectedEmail = emails.first()
        )
    }

    fun setSelectedEmail(emailId: Long) {
        /**
         * We only set isDetailOnlyOpen to true when it's only single pane layout
         */
        val email = uiState.value.emails.find { it.id == emailId }
        _uiState.value = _uiState.value.copy(
            selectedEmail = email,
            isDetailOnlyOpen = true
        )
    }

    fun closeDetailScreen() {
        _uiState.value = _uiState
            .value.copy(
                isDetailOnlyOpen = false,
                selectedEmail = _uiState.value.emails.first()
            )
    }
}

data class ReplyHomeUIState(
    val emails: List<Email> = emptyList(),
    val selectedEmail: Email? = null,
    val isDetailOnlyOpen: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null
)