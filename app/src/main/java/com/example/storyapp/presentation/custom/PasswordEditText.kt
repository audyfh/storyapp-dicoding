package com.example.storyapp.presentation.custom

import android.content.Context
import android.graphics.Canvas
import android.text.InputType
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class PasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    init {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "password"
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if (text.toString().length<8){
            error = "Tidak boleh kurang dari 8 karakter"
        } else {
            error = null
        }
    }
}