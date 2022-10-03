package com.example.revisio.Game;

import static com.example.revisio.CompetitiveSetActivity.COMPETITIVE;
import static com.example.revisio.Helpers.CustomDialog.END_GAME;
import static com.example.revisio.Helpers.DataBaseHelper.HIGHSCORE;
import static com.example.revisio.Helpers.DataBaseHelper.SET_NAME;
import static com.example.revisio.SetActivity.PURPOSE;
import static com.example.revisio.SetActivity.TAG;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.revisio.Helpers.CustomDialog;
import com.example.revisio.Helpers.DataBaseHelper;
import com.example.revisio.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    public static final String ACTIVITY_FROM = "Activity from where the game started";
    private GameLoop gameLoop;

    private Answer topLeftAnswer;
    private Answer topRightAnswer;
    private Answer bottomLeftAnswer;
    private Answer bottomRightAnswer;
    private ArrayList<Answer> allAnswers = new ArrayList<>();

    private SurfaceHolder surfaceHolder;

    private String difficulty;
    private String setName;
    private String language;
    private int numberOfWords;
    private int correctlyGuessed;
    private String wordToLearn = "";
    private ArrayList<String> engWords;
    private ArrayList<String> translations;
    private Enemy enemy = new Enemy();
    private String variant;
    private int score;
    private int streak;


    public GameView(Context context, String difficulty, String setName, String language, int numberOfWords, String variant) {
        super(context);
        this.difficulty = difficulty;
        this.setName = setName;
        this.language = language;
        this.numberOfWords = numberOfWords;
        this.variant = variant;
        this.score = 0;
        this.streak = 0;

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        gameLoop = new GameLoop(this, surfaceHolder);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext());
        Pair<ArrayList<String>, ArrayList<String>> words = dataBaseHelper.getRandomWords(setName, language, numberOfWords);
        engWords = words.first;
        translations = words.second;

        initGame();

        setFocusable(true);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initGame() {
        if (numberOfWords == correctlyGuessed) {
            endGame();
            return;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        int answerHeight = metrics.heightPixels/8;
        int answerWidth = metrics.widthPixels/8;

        topLeftAnswer = new Answer(answerWidth, answerHeight, 3*answerWidth, 3*answerHeight, getContext());
        topRightAnswer = new Answer(5*answerWidth, answerHeight, 7*answerWidth, 3*answerHeight,  getContext());
        bottomLeftAnswer = new Answer(answerWidth, 5*answerHeight, 3*answerWidth, 7*answerHeight,  getContext());
        bottomRightAnswer = new Answer(5*answerWidth, 5*answerHeight, 7*answerWidth, 7*answerHeight, getContext());

        allAnswers = new ArrayList<>();
        allAnswers.add(topLeftAnswer);
        allAnswers.add(topRightAnswer);
        allAnswers.add(bottomLeftAnswer);
        allAnswers.add(bottomRightAnswer);


        ArrayList<Answer> answers = new ArrayList<>(Arrays.asList(topLeftAnswer, bottomLeftAnswer, topRightAnswer, bottomRightAnswer));
        Random r = new Random();
        int correctAnswerIndex = r.nextInt(4);
        int wordToLearnIndex = r.nextInt(numberOfWords - correctlyGuessed);

        wordToLearn = engWords.get(wordToLearnIndex);
        String translationOfWordToLearn = translations.get(wordToLearnIndex);
        answers.get(correctAnswerIndex).setText(translationOfWordToLearn);
        answers.get(correctAnswerIndex).setCorrect(true);
        if (difficulty.equals("Medium") || difficulty.equals("Hard")) {
            if (enemy.getType() == 1) {
                enemy = new Enemy(getContext(), answers.get(correctAnswerIndex), 4 * answerWidth, 4* answerHeight, 100, streak);
                answers.remove(correctAnswerIndex);
            } else {
                answers.remove(correctAnswerIndex);
                enemy = new Enemy(getContext(), answers.get(r.nextInt(answers.size())), 4 * answerWidth, 4* answerHeight, 100, streak);
            }
        } else {
            answers.remove(correctAnswerIndex);
        }

        engWords.set(wordToLearnIndex, engWords.get(numberOfWords - correctlyGuessed - 1));
        engWords.set(numberOfWords - correctlyGuessed - 1, wordToLearn);

        translations.set(wordToLearnIndex, translations.get(numberOfWords - correctlyGuessed - 1));
        translations.set(numberOfWords - correctlyGuessed - 1, translationOfWordToLearn);

        ArrayList<String> translationsCopy = new ArrayList<>(translations);
        for (int i = 0; i < 3; ++i) {
            int index = r.nextInt(numberOfWords - i);
            while (translationsCopy.get(index).equals(translationOfWordToLearn)) {
                index = r.nextInt(numberOfWords - i);
            }
            String incorrectAnswerText = translationsCopy.get(index);
            answers.get(i).setText(incorrectAnswerText);
            translationsCopy.set(index, translationsCopy.get(numberOfWords - 1 - i));
            translationsCopy.set(numberOfWords - 1 - i, incorrectAnswerText);
        }

    }

    public void pauseGame() {
        gameLoop.stopLoop();
    }

    private void endGame() {
        CustomDialog customDialog = new CustomDialog();
        Bundle bundle = new Bundle();
        bundle.putString(PURPOSE, END_GAME);
        bundle.putString(ACTIVITY_FROM, variant);
        if (variant.equals(COMPETITIVE)) {
            bundle.putString(SET_NAME, setName);
            bundle.putInt(HIGHSCORE, score);
        }
        customDialog.setArguments(bundle);
        customDialog.setCancelable(false);
        customDialog.show(((AppCompatActivity)getContext()).getSupportFragmentManager(), TAG);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Pair<Boolean, Answer> booleanAnswerPair = answerPressed(event.getX(), event.getY());
                if (booleanAnswerPair.first) {
                    if (booleanAnswerPair.second.isCorrect()) {
                        score+=10 + streak;
                        streak++;
                        correctlyGuessed++;
                        initGame();
                    } else {
                        streak = 0;
                        enemy.setStreak(streak);
                        score-=5;
                        booleanAnswerPair.second.setChecked(true);
                    }
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    private Pair<Boolean, Answer> answerPressed(float x, float y) {
        Answer correctAnswer = null;
        boolean topLeftPressed = topLeftAnswer.getLeft() < x && x < topLeftAnswer.getRight() &&
                topLeftAnswer.getTop() < y && y < topLeftAnswer.getBottom();
        if (topLeftPressed) {
            correctAnswer = topLeftAnswer;
        }
        boolean topRightPressed = topRightAnswer.getLeft() < x && x < topRightAnswer.getRight() &&
                topRightAnswer.getTop() < y && y < topRightAnswer.getBottom();
        if (topRightPressed) {
            correctAnswer = topRightAnswer;
        }
        boolean bottomLeftPressed = bottomLeftAnswer.getLeft() < x && x < bottomLeftAnswer.getRight() &&
                bottomLeftAnswer.getTop() < y && y < bottomLeftAnswer.getBottom();
        if (bottomLeftPressed) {
            correctAnswer = bottomLeftAnswer;
        }
        boolean bottomRightPressed = bottomRightAnswer.getLeft() < x && x < bottomRightAnswer.getRight() &&
                bottomRightAnswer.getTop() < y && y < bottomRightAnswer.getBottom();
        if (bottomRightPressed) {
            correctAnswer = bottomRightAnswer;
        }
        return new Pair<>(topLeftPressed || topRightPressed || bottomLeftPressed || bottomRightPressed, correctAnswer);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if (gameLoop.getState().equals(Thread.State.TERMINATED)) {
            gameLoop = new GameLoop(this, holder);
        }
        gameLoop.startLoop();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawAnswers(canvas);
        if (difficulty.equals("Medium") || difficulty.equals("Hard")) {
            enemy.draw(canvas);
        }
        if (variant.equals(COMPETITIVE)) {
            drawScore(canvas);
        }
        drawWordToLearn(canvas);
        drawHowManyLearned(canvas);
    }

    private void drawScore(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), R.color.purple_200));
        paint.setTextSize(40);
        canvas.drawText("Score: " + score, 900, 85, paint);
    }

    private void drawHowManyLearned(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), R.color.purple_200));
        paint.setTextSize(40);
        canvas.drawText(correctlyGuessed + "/" + numberOfWords, 50, 85, paint);
    }

    private void drawWordToLearn(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), R.color.green));
        paint.setTextSize(40);
        canvas.drawText("What is the translation of the word: " + wordToLearn, 100, 200, paint);
    }

    private void drawAnswers(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), R.color.white));
        paint.setTextSize(30);
        for (Answer answer : allAnswers) {
            answer.draw(canvas);
            int length  = answer.getText().length();
            if (length > 17) {
                canvas.drawText(answer.getText().substring(0,17) + "-", answer.getLeft() + 10, (answer.getBottom()+answer.getTop())/2, paint);
                canvas.drawText(answer.getText().substring(17), answer.getLeft() + 10, (answer.getBottom()+answer.getTop())/2 + 50, paint);
            } else {
                canvas.drawText(answer.getText(), answer.getLeft() + 10, (answer.getBottom()+answer.getTop())/2, paint);
            }
        }
    }
    public void update() {
        if (numberOfWords == correctlyGuessed) {
            return;
        }
        if (difficulty.equals("Medium") || difficulty.equals("Hard")) {
            enemy.update();
            if (enemy.getAnswer().isChecked()) {
                if (enemy.getAnswer().isCorrect()) {
                    streak = 0;
                    correctlyGuessed++;
                    initGame();
                } else {
                    allAnswers.remove(enemy.getAnswer());
                    Random r = new Random();
                    enemy.setAnswer(allAnswers.get(r.nextInt(allAnswers.size())));
                }
            }
        }
        allAnswers.removeIf(Answer::isChecked);
        if (difficulty.equals("Hard")) {
            for (Answer answer : allAnswers) {
                answer.update();
            }
        }
    }
}
