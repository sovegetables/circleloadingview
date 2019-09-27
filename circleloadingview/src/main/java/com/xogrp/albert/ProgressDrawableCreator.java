package com.xogrp.albert;

import android.content.Context;
import android.graphics.drawable.Drawable;

interface ProgressDrawableCreator {
    Drawable createDrawable(Context context, CircleLoadingView circleLoadingView);
    Drawable getDrawable();
    void setSize(int size);
    void setBackgroundColor(int color);
    void setColorSchemeColors(int[] colors);
    void stop();
    void start();
    void setArrowEnabled(boolean showArrow);

    class CircularProgressDrawableCreator implements ProgressDrawableCreator{

        private CircularProgressDrawable mCircularProgressDrawable;

        @Override
        public Drawable createDrawable(Context context, CircleLoadingView circleLoadingView) {
            mCircularProgressDrawable = new CircularProgressDrawable(context);
            return mCircularProgressDrawable;
        }

        @Override
        public Drawable getDrawable() {
            return mCircularProgressDrawable;
        }

        @Override
        public void setSize(int size) {
            mCircularProgressDrawable.setStyle(size);
        }

        @Override
        public void setBackgroundColor(int color) {
            mCircularProgressDrawable.setBackgroundColor(color);
        }

        @Override
        public void setColorSchemeColors(int[] colors) {
            mCircularProgressDrawable.setColorSchemeColors(colors);
        }

        @Override
        public void stop() {
            mCircularProgressDrawable.stop();
        }

        @Override
        public void start() {
            mCircularProgressDrawable.start();
        }

        @Override
        public void setArrowEnabled(boolean showArrow) {
            mCircularProgressDrawable.setArrowEnabled(showArrow);
        }
    }

    class MaterialProgressDrawableCreator implements ProgressDrawableCreator{

        private MaterialProgressDrawable mMaterialProgressDrawable;

        @Override
        public Drawable createDrawable(Context context, CircleLoadingView circleLoadingView) {
            mMaterialProgressDrawable = new MaterialProgressDrawable(context, circleLoadingView);
            return mMaterialProgressDrawable;
        }

        @Override
        public Drawable getDrawable() {
            return mMaterialProgressDrawable;
        }

        @Override
        public void setSize(int size) {
            mMaterialProgressDrawable.updateSizes(size);
        }

        @Override
        public void setBackgroundColor(int color) {
            mMaterialProgressDrawable.setBackgroundColor(color);
        }

        @Override
        public void setColorSchemeColors(int[] colors) {
            mMaterialProgressDrawable.setColorSchemeColors(colors);
        }

        @Override
        public void stop() {
            mMaterialProgressDrawable.stop();
        }

        @Override
        public void start() {
            mMaterialProgressDrawable.start();
        }

        @Override
        public void setArrowEnabled(boolean showArrow) {
            mMaterialProgressDrawable.showArrow(showArrow);
        }
    }
}
