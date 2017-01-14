package com.example.ger.myapplication;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by Ger on 14/01/2017.
 */

public class CustomEditText extends EditText {

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CustomEditText(Context context) {super(context);}
    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {super(context, attrs, defStyle);}

    private FloatingActionButton mAddButton;
    private FloatingActionButton mSendButton;

    public void setButtonRefs(FloatingActionButton addButton, FloatingActionButton sendButton)
    {
        mAddButton = addButton;
        mSendButton = sendButton;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            this.setVisibility(INVISIBLE);
            mAddButton.setVisibility(VISIBLE);
            mSendButton.setVisibility(INVISIBLE);
        }
        return super.onKeyPreIme(keyCode, event);
    }

}
