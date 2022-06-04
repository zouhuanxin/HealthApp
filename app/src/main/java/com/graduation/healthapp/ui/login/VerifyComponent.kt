package com.graduation.healthapp.ui.login

import android.content.Context
import android.text.InputFilter
import android.text.TextUtils
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import com.graduation.healthapp.R

class VerifyComponent : LinearLayout {
    private lateinit var arr: Array<EditText?>
    private lateinit var callback: (str: String) -> Unit

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        orientation = HORIZONTAL
        val array = context!!.obtainStyledAttributes(attrs, R.styleable.VerifyComponent) ?: return

        val num = array.getInteger(R.styleable.VerifyComponent_number, 4)

        arr = arrayOfNulls<EditText>(num)
        for (i in 0 until num) {
            val item = EditText(context)
            val params = LayoutParams(
                resources.getDimension(R.dimen.dp_70)
                    .toInt() - num * resources.getDimension(R.dimen.dp_5).toInt(),
                resources.getDimension(R.dimen.dp_70)
                    .toInt() - num * resources.getDimension(R.dimen.dp_5).toInt()
            )
            params.topMargin = resources.getDimension(R.dimen.dp_5).toInt()
            params.bottomMargin = resources.getDimension(R.dimen.dp_5).toInt()
            params.leftMargin = resources.getDimension(R.dimen.dp_5).toInt()
            params.rightMargin = resources.getDimension(R.dimen.dp_5).toInt()
            item.background = getResources().getDrawable(R.drawable.verifycomponent_item_edit)
            item.inputType = 0x00000002
            item.filters = arrayOf(InputFilter.LengthFilter(1))
            item.gravity = 0x11
            item.isEnabled = false
            item.addTextChangedListener {
                if (it!!.length == 1) {
                    if (i == num - 1) {
                        //验证码输入完毕
                        var res = ""
                        for (j in 0 until num) res = res + arr[j]!!.text
                        callback.invoke(res)
                    } else {
                        arr[i]!!.isEnabled = false
                        arr[i + 1]!!.apply {
                            this.isEnabled = true
                            this.isFocusable = true
                            this.isFocusableInTouchMode = true
                            this.requestFocus()
                            val inputManager: InputMethodManager =
                                this.getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE
                                ) as InputMethodManager
                            inputManager.showSoftInput(this, 0) //展示软键盘

                        }
                    }
                }
            }
            item.setOnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && i > 0) {
                    if (!TextUtils.isEmpty(arr[i]!!.text)) {
                        arr[i]!!.text.clear()
                    } else {
                        arr[i]!!.isEnabled = false
                        arr[i - 1]!!.apply {
                            this.text.clear()
                            this.isEnabled = true
                            this.requestFocus()
                        }
                    }
                }
                false
            }
            arr[i] = item
            addView(item, params)
        }
        arr[0]!!.apply {
            this.isEnabled = true
            this.requestFocus()
        }
    }

    fun setCallback(block: (str: String) -> Unit) {
        callback = block
    }

}