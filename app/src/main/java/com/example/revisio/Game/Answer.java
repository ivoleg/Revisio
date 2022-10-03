package com.example.revisio.Game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

import com.example.revisio.R;

import java.util.Random;

import kotlin.jvm.internal.Lambda;

public class Answer {
    private float left;
    private float top;
    private float right;
    private float bottom;
    private String text;
    private Paint paint;
    private Context context;
    private boolean isCorrect;
    private boolean isChecked;

    private double slope;
    private float delta;
    private float height;
    private float width;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "left=" + left +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                ", text='" + text + '\'' +
                ", isCorrect=" + isCorrect +
                ", isChecked=" + isChecked +
                '}';
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public float getBottom() {
        return bottom;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public Answer(float left, float top, float right, float bottom, Context context) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.context = context;
        this.isCorrect = false;
        this.isChecked = false;

        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.purple_200));

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        height = metrics.heightPixels;
        width = metrics.widthPixels;

        Random r = new Random();
        int combiningAngle = r.nextInt(180) + 90;
        while (combiningAngle % 45 == 0) {
            combiningAngle = r.nextInt(180) + 90;
        }
        double angleInRadians = Math.toRadians(combiningAngle);
        slope = Math.tan(angleInRadians);
        int sign = r.nextInt(2) * 2 - 1;
        delta = (float) (sign/slope);
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(left, top, right, bottom, paint);
    }

    public void update() {
        left += delta;
        top += (float) (slope * delta);
        right += delta;
        bottom +=(float) (slope * delta);
        if (left < 50) {
            slope = -slope;
            delta = -delta;
        }
        if (top < 50) {
            slope = -slope;
        }
        if (bottom > height - 50) {
            slope = -slope;
        }
        if (right > width - 50) {
            slope = -slope;
            delta = -delta;
        }
    }
}
