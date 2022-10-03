package com.example.revisio.Game;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class    GameLoop extends Thread{
    public static final float MAX_UPS = 30;
    private static final float UPDATE_PERIOD = (float) (1E+3/MAX_UPS);
    boolean isRunning = false;
    private SurfaceHolder surfaceHolder;
    private GameView gameView;

    public GameLoop(GameView gameView, SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
    }

    public void startLoop() {
        isRunning = true;
        start();
    }

    @Override
    public void run() {
        super.run();

        int updateCount = 0;

        long startTime;
        long elapsedTime;
        long sleepTime;

        Canvas canvas = null;
        startTime = System.currentTimeMillis();
        while (isRunning) {
            try {
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    gameView.update();
                    updateCount++;
                    gameView.draw(canvas);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            elapsedTime = System.currentTimeMillis() - startTime;
            sleepTime = (long) (updateCount * UPDATE_PERIOD - elapsedTime);
            if (sleepTime > 0){
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            while (sleepTime < 0 && updateCount < MAX_UPS - 1) {
                gameView.update();
                updateCount++;
                elapsedTime = System.currentTimeMillis() - startTime;
                sleepTime = (long) (updateCount * UPDATE_PERIOD - elapsedTime);
            }

            elapsedTime = System.currentTimeMillis() - startTime;
            if(elapsedTime >= 1000){
                updateCount = 0;
                startTime = System.currentTimeMillis();
            }
        }
    }

    public void stopLoop() {
        isRunning = false;
        try {
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
