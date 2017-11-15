package cn.edu.pku.zhangqixun.widget;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import cn.edu.pku.zhangqixun.minweather.R;

/**
 * Created by fxjzzyo on 2017/11/15.
 */

public class ClearEiditText extends EditText implements View.OnFocusChangeListener,TextWatcher{

    private Drawable mClearDrawable;

    public ClearEiditText(Context context) {
        this(context,null);
    }

    public ClearEiditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClearEiditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
            mClearDrawable = getResources().getDrawable(R.drawable.clear);
        }
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        setClearIconVisable(false);
        setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getCompoundDrawables()[2] != null) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                boolean touchable= event.getX() > (getWidth() - getPaddingRight() - mClearDrawable.getIntrinsicWidth())
                        && (event.getX() < (getWidth() - getPaddingRight()));

                if (touchable) {
                    this.setText("");
                }

            }
        }



        return super.onTouchEvent(event);
    }

    private void setClearIconVisable(boolean b) {
        Drawable right = b?mClearDrawable:null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }


    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        setClearIconVisable(text.length()>0);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (b) {
            setClearIconVisable(this.getText().length()>0);
        }else {
            setClearIconVisable(false);
        }
    }
}
