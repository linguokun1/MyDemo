package com.sponia.opyfunctioindemo.customedview;

import android.view.animation.BounceInterpolator;

/**
 * @author linguokun
 * @packageName com.sponia.opyfunctioindemo.customedview
 * @description
 * @date 16/7/7
 */
public class SpringInterpolator extends BounceInterpolator {
    @Override
    public float getInterpolation(float input) {
        return -(super.getInterpolation(input));
    }
}
