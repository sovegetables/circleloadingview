package com.xogrp.albert;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/*
 * Copyright 2016 Albert Liu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class CircleLoadingView extends ImageView{
    private static final int SHADOW_COLOR = 0xFFFAFAFA;
    private MaterialProgressDrawable mMaterialProgressDrawable;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LARGE, NORMAL})
    public @interface Size {}

    public static final int LARGE = MaterialProgressDrawable.LARGE;
    public static final int NORMAL = MaterialProgressDrawable.DEFAULT;

    private static final boolean UP_LOLLIPOP = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

    private static final ShadowHelperCompat IMPL;

    static {
        IMPL = UP_LOLLIPOP ? new LollipopShadowHelper() : new ShadowHelper();
    }

    public CircleLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleLoadingView(Context context) {
        super(context);
        init(context, null);
    }

    public CircleLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        ViewCompat.setBackground(this, IMPL.createShadowShapeDrawable(context, this, SHADOW_COLOR));
        mMaterialProgressDrawable = new MaterialProgressDrawable(context, this);
        mMaterialProgressDrawable.setAlpha(255);

        if(attrs != null){
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.CircleLoadingView);
            setBackgroundColor(a.getColor(R.styleable.CircleLoadingView_bg_color, SHADOW_COLOR));
            int size = a.getInteger(R.styleable.CircleLoadingView_size, NORMAL);
            if(size == NORMAL) {
                mMaterialProgressDrawable.updateSizes(MaterialProgressDrawable.DEFAULT);
            }else if(size == LARGE){
                mMaterialProgressDrawable.updateSizes(MaterialProgressDrawable.LARGE);
            }

            int color1 = a.getColor(R.styleable.CircleLoadingView_progress_color, Color.BLACK);
            int color2 = a.getColor(R.styleable.CircleLoadingView_progress_second_color, Color.BLACK);
            setColorSchemeColors(color1, color2);
            a.recycle();
        }
        setImageDrawable(mMaterialProgressDrawable);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mMaterialProgressDrawable.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mMaterialProgressDrawable.stop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!UP_LOLLIPOP) {
            setMeasuredDimension(getMeasuredWidth() + IMPL.getHorizontalShadowPadding(), getMeasuredHeight()
                    + IMPL.getVerticalShadowPadding());
        }
    }

    @Override
    public void setBackgroundColor(@ColorInt int color) {
        if (getBackground() instanceof ShapeDrawable) {
            ((ShapeDrawable) getBackground()).getPaint().setColor(color);
        }
    }

    /**
     * Set the colors used in the progress animation from color resources.
     * The first color will also be the color of the bar that grows in response
     * to a user swipe gesture.
     *
     * @param colors colors
     */
    public void setColorSchemeColors(int... colors) {
        mMaterialProgressDrawable.setColorSchemeColors(colors);
    }

    /**
     * Set Size
     * @param size  LARGE or NORMAL
     */
    public void setSize(@Size int size) {
        setImageDrawable(null);
        mMaterialProgressDrawable.updateSizes(size);
        setImageDrawable(mMaterialProgressDrawable);
    }

    /**
     * Set the background color of the progress spinner disc.
     *
     * @param colorRes Resource id of the color.
     */
    public void setProgressBackgroundColorSchemeResource(@ColorRes int colorRes) {
        setProgressBackgroundColorSchemeColor(ContextCompat.getColor(getContext(), colorRes));
    }

    /**
     * Set the background color of the progress spinner disc.
     *
     * @param color ColorInt
     */
    public void setProgressBackgroundColorSchemeColor(@ColorInt int color) {
        setBackgroundColor(color);
        mMaterialProgressDrawable.setBackgroundColor(color);
    }


    private interface ShadowHelperCompat {
        Drawable createShadowShapeDrawable(Context context, CircleLoadingView circleLoadingView, int shadowColor);
        int getHorizontalShadowPadding();
        int getVerticalShadowPadding();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static class LollipopShadowHelper implements ShadowHelperCompat {

        private static final int SHADOW_ELEVATION = 4;

        @Override
        public Drawable createShadowShapeDrawable(Context context, final CircleLoadingView circleLoadingView, int shadowColor) {
            final float density = context.getResources().getDisplayMetrics().density;
            ShapeDrawable circle = new ShapeDrawable(new OvalShape());
            circle.getPaint().setColor(shadowColor);
            final float elevation = SHADOW_ELEVATION * density;
            circleLoadingView.setElevation(elevation);

            circleLoadingView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ViewParent p = circleLoadingView.getParent();
                    if(p instanceof ViewGroup) {
                        final int margin = (int) elevation;
                        ViewGroup.LayoutParams params = circleLoadingView.getLayoutParams();
                        if(params instanceof ViewGroup.MarginLayoutParams){
                            ((ViewGroup.MarginLayoutParams) params).setMargins(margin, margin, margin, margin);
                        }
                    }

                    circleLoadingView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });

            return circle;
        }

        @Override
        public int getHorizontalShadowPadding() {
            return 0;
        }

        @Override
        public int getVerticalShadowPadding() {
            return 0;
        }
    }

    private static class ShadowHelper implements ShadowHelperCompat {

        private static final int KEY_SHADOW_COLOR = 0x1E000000;
        private static final int FILL_SHADOW_COLOR = 0x3D000000;

        // PX
        private static final float X_OFFSET = 0f;
        private static final float Y_OFFSET = 1.75f;
        private static final float SHADOW_RADIUS = 3.5f;
        private static final int RADIUS = 20;
        private int mShadowRadius;

        @Override
        public Drawable createShadowShapeDrawable(Context context, CircleLoadingView circleLoadingView, int shadowColor) {
            final float density = context.getResources().getDisplayMetrics().density;
            mShadowRadius = (int) (density * SHADOW_RADIUS);
            final int diameter = (int) (RADIUS * density * 2);
            final int shadowYOffset = (int) (density * Y_OFFSET);
            final int shadowXOffset = (int) (density * X_OFFSET);
            OvalShape oval = new OvalShadow(mShadowRadius, diameter);
            ShapeDrawable circle = new ShapeDrawable(oval);
            ViewCompat.setLayerType(circleLoadingView, ViewCompat.LAYER_TYPE_SOFTWARE, circle.getPaint());
            circle.getPaint().setShadowLayer(mShadowRadius, shadowXOffset, shadowYOffset,
                    KEY_SHADOW_COLOR);
            final int padding = mShadowRadius;
            // set padding so the inner image sits correctly within the shadow.
            circleLoadingView.setPadding(padding, padding, padding, padding);
            return circle;
        }

        @Override
        public int getHorizontalShadowPadding() {
            return mShadowRadius * 2;
        }

        @Override
        public int getVerticalShadowPadding() {
            return mShadowRadius * 2;
        }

        private class OvalShadow extends OvalShape {
            private RadialGradient mRadialGradient;
            private Paint mShadowPaint;
            private int mCircleDiameter;

            OvalShadow(int shadowRadius, int circleDiameter) {
                super();
                mShadowPaint = new Paint();
                mShadowRadius = shadowRadius;
                mCircleDiameter = circleDiameter;
                mRadialGradient = new RadialGradient(mCircleDiameter / 2, mCircleDiameter / 2,
                        mShadowRadius, new int[] {
                        FILL_SHADOW_COLOR, Color.TRANSPARENT
                }, null, Shader.TileMode.CLAMP);
                mShadowPaint.setShader(mRadialGradient);
            }

            @Override
            public void draw(Canvas canvas, Paint paint) {
                final float viewWidth = getWidth();
                final float viewHeight = getHeight();
                canvas.drawCircle(viewWidth / 2, viewHeight / 2, (mCircleDiameter / 2 + mShadowRadius),
                        mShadowPaint);
                canvas.drawCircle(viewWidth / 2, viewHeight / 2, (mCircleDiameter / 2), paint);
            }
        }
    }

}