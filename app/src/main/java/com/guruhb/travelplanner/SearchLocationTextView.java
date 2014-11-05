package com.guruhb.travelplanner;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

/**
 * Created by guruhb@gmail.com on 04-11-2014.
 */
public class SearchLocationTextView extends AutoCompleteTextView {
    public SearchLocationTextView(Context context) {
        super(context);
    }


    public SearchLocationTextView(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public SearchLocationTextView(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    private static final int MESSAGE_TEXT_CHANGED = 100; //FIXME : check this ?
    private int mSearchAutoCompleteDelay = 200;
    private ProgressBar mProgressBar;

    private final Handler mHandler  = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            Log.v("SearchLocationTextView", "handleMessage ");
            SearchLocationTextView.super.performFiltering((CharSequence) msg.obj, msg.arg1);
        }
    };

    public void setLoadingProgress(ProgressBar progressBar) { mProgressBar = progressBar; }
    public void setSearchAutoCompleteDelay(int autoCompleteDelay) { mSearchAutoCompleteDelay = autoCompleteDelay; }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        Log.v("SearchLocationTextView", "performFiltering " + text.toString() + " keyCode : " + keyCode);
        //super.performFiltering(text, keyCode);
        if(mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        mHandler.removeMessages(MESSAGE_TEXT_CHANGED);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_TEXT_CHANGED, text), mSearchAutoCompleteDelay);
    }

    @Override
    public void onFilterComplete(int count) {
        Log.v("SearchLocationTextView", "onFilterComplete " + count);
        if(mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
        super.onFilterComplete(count);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        Log.v("SearchLocationTextView", "onFocusChanged " + focused + " direction : " + direction );
        if(focused && previouslyFocusedRect != null) {
            if(getAdapter() != null) {
                Log.v("SearchLocationTextView", "onFocusChanged getAdapter() count " + getAdapter().getCount() );
                if(getAdapter().getCount() > 1) {
                    showDropDown();
                }
            }
        }
    }
}