package com.example.user.eventreminder;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by user on 17.09.2017.
 */

public class InputFilterDateTime implements InputFilter {
    private int minimumValue;
    private int maximumValue;

    public InputFilterDateTime(int minimumValue, int maximumValue) {
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.subSequence(0, dstart).toString() + source + dest.subSequence(dend, dest.length()));
            if (isInRange(minimumValue, maximumValue, input))
                return null;
        }
        catch (NumberFormatException nfe) {
        }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
