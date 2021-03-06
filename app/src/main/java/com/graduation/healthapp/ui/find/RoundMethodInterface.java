package com.graduation.healthapp.ui.find;

import android.graphics.Bitmap;

public interface RoundMethodInterface {

    void setRadius(float radiusDp);

    void setRadius(float radiusTopLeftDp, float radiusTopRightDp, float radiusBottomLeftDp, float radiusBottomRightDp);

    void setRadiusLeft(float radiusDp);

    void setRadiusRight(float radiusDp);

    void setRadiusTop(float radiusDp);

    void setRadiusBottom(float radiusDp);

    void setRadiusTopLeft(float radiusDp);

    void setRadiusTopRight(float radiusDp);

    void setRadiusBottomLeft(float radiusDp);

    void setRadiusBottomRight(float radiusDp);

    void setStrokeWidth(float widthDp);

    void setStrokeColor(int color);

    void setBlur(int num);

    void setTargetBitmap(Bitmap bitmap);
}
