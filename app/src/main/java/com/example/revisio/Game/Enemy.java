package com.example.revisio.Game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

import com.example.revisio.R;

import java.util.Random;

public class Enemy {
    private static float SPEED = 3;
    private Context context;
    private Answer answer;
    private float coordinateX;
    private float coordinateY;
    private Paint paint;
    private float radius;
    private float velocityX;
    private float velocityY;
    private int type;
    private int streak;

    public Enemy() {
        Random r = new Random();
        type = r.nextInt(2);
    }

    public Enemy(Context context, Answer answer, float coordinateX, float coordinateY, float radius, int streak) {
        this.context = context;
        this.answer = answer;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
        this.streak = streak;
        this.radius = radius;
        this.paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.red));

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public float getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(float coordinateX) {
        this.coordinateX = coordinateX;
    }

    public float getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(float coordinateY) {
        this.coordinateY = coordinateY;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void update() {
        float distanceToAnswerX = (answer.getRight() + answer.getLeft())/2 - coordinateX;
        float distanceToAnswerY = (answer.getBottom() + answer.getTop())/2 - coordinateY;

        float distanceToAnswer = (float) Math.sqrt(
                Math.pow(distanceToAnswerX, 2) +
                Math.pow(distanceToAnswerY, 2)
        );
        if (distanceToAnswer > radius) {
            float directionX = distanceToAnswerX/distanceToAnswer;
            float directionY = distanceToAnswerY/distanceToAnswer;
            velocityX = directionX * (SPEED + streak);
            velocityY = directionY * (SPEED + streak);
        } else {
            velocityX = 0;
            velocityY = 0;
            answer.setChecked(true);
        }
        coordinateX +=velocityX;
        coordinateY +=velocityY;
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle( coordinateX, coordinateY, radius, paint);
    }

    public int getType() {
        return type;
    }

}
