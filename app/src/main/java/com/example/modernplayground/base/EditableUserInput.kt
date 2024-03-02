package com.example.modernplayground.base

import androidx.annotation.DrawableRes
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.SolidColor
import com.example.modernplayground.ui.captionTextStyle

@Composable
fun CraneEditableUserInput(
    state: EditableUserInputState = rememberEditableUserInputState(""),
    caption: String? = null,
    @DrawableRes vectorImageId: Int? = null,
) {
    // COMPLETED: Encapsulate this state in a state holder

    CraneBaseUserInput(
        caption = caption,
        tintIcon = { !state.isHint },
        showCaption = { !state.isHint },
        vectorImageId = vectorImageId
    ) {
        BasicTextField(
            value = state.text,
            onValueChange = { state.updateText(it) },
            textStyle = if (state.isHint) {
                captionTextStyle.copy(color = LocalContentColor.current)
            } else {
                MaterialTheme.typography.body1.copy(color = LocalContentColor.current)
            },
            cursorBrush = SolidColor(LocalContentColor.current)
        )
    }
}

// State holders always need to be remembered in order to keep them in the Composition and not
// create a new one every time.
@Composable
fun rememberEditableUserInputState(hint: String): EditableUserInputState =
// If you only remember this state, it won't survive activity recreations. To achieve that,
// you can use the rememberSaveable API instead which behaves similarly to remember, but the
// stored value also survives activity and process recreation. Internally, it uses the saved
// instance state mechanism.
// rememberSaveable does all this with no extra work for objects that can be stored inside
// a Bundle. That's not the case for the EditableUserInputState class that you created in
// your project. Therefore, you need to tell rememberSaveable how to save and restore an
    // instance of this class using a Saver.
    rememberSaveable(hint, saver = EditableUserInputState.Saver) {
        EditableUserInputState(hint, hint)
    }

// The logic to update the textState and determine whether what's been displayed corresponds to
// the hint or not is all in the body of the CraneEditableUserInput composable. This brings some
// downsides with it:
//
// 1. The value of the TextField is not hoisted and therefore cannot be controlled from outside,
// making testing harder.
// 2. The logic of this composable could become more complex and the internal state could be out
// of sync more easily.

// By creating a state holder responsible for the internal state of this composable, you can
// centralize all state changes in one place. With this, it's more difficult for the state to
// be out of sync, and the related logic is all grouped together in a single class. Furthermore,
// this state can be easily hoisted up and can be consumed from callers of this composable.
//
// In this case, hoisting the state is a good idea since this is a low-level UI component that
// might be reused in other parts of the app. Therefore, the more flexible and controllable it
// is, the better.
class EditableUserInputState(private val hint: String, initialText: String) {

    var text by mutableStateOf(initialText)
        private set

    fun updateText(newText: String) {
        text = newText
    }

    val isHint: Boolean
        get() = text == hint

    // A Saver describes how an object can be converted into something which is Saveable.
    // Implementations of a Saver need to override two functions:
    //
    // 1. `save` to convert the original value to a saveable one.
    // 2. `restore` to convert the restored value to an instance of the original class.
    // For this case, instead of creating a custom implementation of Saver for the
    // EditableUserInputState class, you can use some of the existing Compose APIs such as
    // listSaver or mapSaver (that stores the values to save in a List or Map) to reduce the
    // amount of code that you need to write.
    companion object {
        val Saver: Saver<EditableUserInputState, *> = listSaver(
            save = { listOf(it.hint, it.text) },
            restore = {
                EditableUserInputState(hint = it[0], initialText = it[1])
            }
        )
    }
}