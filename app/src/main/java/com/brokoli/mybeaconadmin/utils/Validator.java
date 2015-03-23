package com.brokoli.mybeaconadmin.utils;

import android.widget.EditText;

import com.brokoli.mybeaconadmin.MyApp;
import com.brokoli.mybeaconadmin.R;

public class Validator {
    public static boolean validate(EditText editText, boolean ret){
        if (editText.getText().toString().isEmpty()) {
            editText.setError(MyApp.getAppContext().getString(R.string.mandatory));
            if (ret) {
                editText.requestFocus();
            }
            return false;
        }
        return ret;
    }
}
