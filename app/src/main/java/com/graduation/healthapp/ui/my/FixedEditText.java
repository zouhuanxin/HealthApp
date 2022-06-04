package com.graduation.healthapp.ui.my;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;

import com.graduation.healthapp.ui.find.RoundedEditText;

/**
 * 固定前缀editview
 */
public class FixedEditText extends RoundedEditText implements View.OnFocusChangeListener {

    private String fixText;
    @ColorInt
    private int fixColor = 0xff8c7ef5;//固定文本颜色

    public FixedEditText(Context context) {
        super(context);
        init();
    }

    public FixedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        this.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        //this.setLongClickable(false);需要支持长按弹出全选、复制框
        this.setOnFocusChangeListener(this);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        int fixTextLength = fixText != null ? fixText.length() : 0;
        // 若选中的光标位置在固定文本内，则重置光标位置到固定文本之后;
        if (selStart < fixTextLength) {
            selStart = selStart < fixText.length() ? fixText.length() : selStart;
            selEnd = selStart;
            if (getText().toString().length() != 0) {
                this.setSelection(selStart, selEnd);
            }
        }
        super.onSelectionChanged(selStart, selEnd);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == 67) {//删除键
            //当标记为不能删除的时候拦截操作
            if (!isDelete()) {
                return !isDelete();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus && getText().toString().length() <= fixText.length()) {
            // 在获取焦点的时候，去设置头部文本；(默认进入页面不获取焦点，这样可以显示默认文本)
            if (fixColor != 0) {
                setEdtTxtColor(fixText);
            } else {
                this.setText(fixText);
            }
        }
    }

    /**
     * 输入固定文本
     * @param fixText：固定文本前缀
     * @param fixColor：固定文本的颜色
     */
    public void setFixText(String fixText, @ColorRes int fixColor) {
        this.fixColor = getResources().getColor(fixColor);
        this.fixText = TextUtils.isEmpty(fixText) ? "" : fixText;
    }

    /**
     * 设置固定文本的颜色
     * @param fixText
     */
    private void setEdtTxtColor(String fixText) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        ForegroundColorSpan fixTextSpan = new ForegroundColorSpan(fixColor);
        builder.append(fixText);
        builder.setSpan(fixTextSpan, 0, fixText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        this.setText(builder);
        this.setSelection(fixText.length());
    }

    /**
     * 设置Hint文本
     * @param fixText：固定头部文本
     * @param hintText：输入提示文字
     */
    public void setEdtHint(String fixText, String hintText) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        ForegroundColorSpan fixTextSpan = new ForegroundColorSpan(fixColor);
        builder.append(fixText);
        builder.setSpan(fixTextSpan, 0, fixText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append(hintText);
        this.setHint(builder);
    }

    /**
     * 判断除了固定头，是否有其他输入文本
     * @return
     */
    public boolean isEdtContentEmpty() {
        return getText().toString().trim().length() <= fixText.length();
    }

    /**
     * 获取输入文本内容
     * @param symbol：输入的符号
     * @return
     */
    public String getContentText(String symbol) {
        String content = getText().toString().trim();
        int index = content.indexOf(symbol, 1);
        return content.substring(index + 1).trim();
    }

    private boolean isDelete() {
        int fixTextLength = fixText != null ? fixText.length() : 0;
        if (getSelectionStart() == getSelectionEnd()) {
            // 单个删除
            return getSelectionStart() > fixTextLength;
        }
        // 长按弹出选中删除
        return getSelectionStart() >= fixTextLength;
    }
}
