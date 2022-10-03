package com.example.revisio.Game;

import static com.example.revisio.Helpers.DataBaseHelper.SET_NAME;
import static com.example.revisio.Game.GameView.ACTIVITY_FROM;
import static com.example.revisio.Helpers.SetRecyclerViewAdapter.DIFFICULTY;
import static com.example.revisio.Helpers.SetRecyclerViewAdapter.LANGUAGE;
import static com.example.revisio.Helpers.SetRecyclerViewAdapter.NUMBER_OF_WORDS;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.revisio.Game.GameView;

public class GameActivity extends AppCompatActivity {

    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String setName = bundle.getString(SET_NAME);
        String language = bundle.getString(LANGUAGE);
        String difficulty = bundle.getString(DIFFICULTY);
        int numberOfWords = bundle.getInt(NUMBER_OF_WORDS);
        String variant = bundle.getString(ACTIVITY_FROM);

        gameView = new GameView(this, difficulty, setName, language, numberOfWords, variant);
        setContentView(gameView);
    }

    @Override
    protected void onPause() {
        gameView.pauseGame();
        super.onPause();
    }
}