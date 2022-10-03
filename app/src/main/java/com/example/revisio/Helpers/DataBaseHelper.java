package com.example.revisio.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Pair;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

/**
 * Class that works with the pre-downloaded database. Database consists of the Main table with columns id, set_name
 * and set_image_url, and another tables corresponds to each row of the Main one. Main table stores all
 * Sets characteristics that are available for the app. Each corresponding table stores words and their translations
 * that are in the set, columns of those tables are id, eng_word, fr_word and spn_word.
 * All the words in all tables are casted to lower case and trimmed of spaces.
e */
public class DataBaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "SetModelDatabase.db";

    public static final String MAIN_TABLE_NAME = "Sets";
    public static final String SET_NAME = "set_name";
    public static final String SET_IMAGE_URL = "set_image_url";
    public static final String HIGHSCORE = "highscore";

    public static final String _ID = "_id";
    public static final String ENG_WORD = "eng_word";
    public static final String FR_WORD = "fr_word";
    public static final String SPN_WORD = "spn_word";
    public static final String NOT_GIVEN = "NOT_GIVEN";

    private static final int DATABASE_VERSION = 1;


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This function gets the Cursor from the database and transforms data that stored in it into an ArrayList
     * of SetModels. This array is later used in SetRecyclerViewAdapter in order to show all sets of words.
     * @return ArrayList of SetModels, each SetModel represents one Set that is stored in Main table.
     */
    public ArrayList<SetModel> getSetsModel() {
        ArrayList<SetModel> result = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String q = "SELECT * FROM " + MAIN_TABLE_NAME;
        Cursor c = db.rawQuery(q, null);

        if (c.moveToFirst()) {
            do {
                int id = c.getInt(0);
                String name = c.getString(1);
                String url = c.getString(2);
                int highscore = c.getInt(3);

                SetModel setModel = new SetModel(id,name,url, highscore);
                result.add(setModel);
            } while (c.moveToNext());
        }else {
            //result is already empty ArrayList<>, so the TrainingSetActivity
            //will just show no Sets.
        }
        c.close();
        db.close();
        return result;
    }

    /**
     * This function tries to add a new table to the database, and if it happened successfully function inserts
     * new row into the Main table. It can be unsuccessful, because for the main table, tables with names "Test"
     * and "test" are different but corresponding tables would be the same.
     * @param name name of the added set. Before any usage it will be casted to lower case and trimmed.
     * @param url image url of the added set.
     * @return true if adding was successful and false otherwise.
     */
    public boolean addSet(String name, String url) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        String usableName = name.toLowerCase().trim();

        try {
            String q = "CREATE TABLE '" + usableName + "' ( '" + _ID + "'	INTEGER, '" +
                    ENG_WORD +  "' TEXT NOT NULL UNIQUE, '" +
                    FR_WORD + "' TEXT, '" +
                    SPN_WORD + "' TEXT, " +
                    " PRIMARY KEY( '" + _ID + "' AUTOINCREMENT))";
            db.execSQL(q);
        } catch (SQLiteException e) {
            return false;
        }

        cv.put(SET_NAME, usableName);
        cv.put(SET_IMAGE_URL, url);
        cv.put(HIGHSCORE, 0);

        return db.insert(MAIN_TABLE_NAME, null, cv) != -1;
    }

    /**
     * This function deletes set from Main table of all sets and if that was successful, deletes table that corresponds
     * to that set. It can be unsuccessful because for the main table, tables with names "Test" and "test" are different
     * but corresponding tables would be the same.
     * @param name name of the deleted set. In order to be usable, it will be casted to lower case and trimmed.
     * @return true if delete was successful and false otherwise.
     */
    public boolean deleteSet(String name) {
        SQLiteDatabase db = getWritableDatabase();
        String usableName = name.toLowerCase().trim();

        boolean isDeleted = (db.delete(MAIN_TABLE_NAME, SET_NAME + " = ?", new String[]{usableName}) == 1);
        if(isDeleted) {
            String q = "DROP TABLE '" + usableName + "'";
            db.execSQL(q);
        }

        return isDeleted;
    }

    /**
     * This function adds a given word into a given Set
     * @param setName name of the set.
     * @param engWord word that is added.
     * @param frWord french translation of the word. If empty, NOT_GIVEN is inserted.
     * @param spnWord spanish translation of the word. If empty, NOT_GIVEN is inserted.
     * @return true if insert was successful and false otherwise. False can occur because end_word is UNIQUE in database schema.
     */
    public boolean addWord(String setName, String engWord, String frWord, String spnWord) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(ENG_WORD, engWord);
        if (frWord.equals("")) {
            cv.put(FR_WORD, NOT_GIVEN);
        } else {
            cv.put(FR_WORD, frWord);
        }
        if (spnWord.equals("")) {
            cv.put(SPN_WORD, NOT_GIVEN);
        } else {
            cv.put(SPN_WORD, spnWord);
        }
        try {
            return db.insert("'" + setName + "'", null, cv) != -1;
        } catch (SQLiteConstraintException e) {
            return false;
        }
    }

    /**
     * This function deletes a given word from a given Set.
     * @param setName name of the set from which the word is deleted.
     * @param word word that is going to be deleted.
     * @return due to the known schema of the database db.delete will return either 1 or 0, so this will return true in the
     * first case and false in second.
     */
    public boolean deleteWord(String setName, String word) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("'" + setName + "'", "eng_word = ?", new String[] {word}) == 1;
    }

    /**
     * This function gets the cursor for the ListView in SetActivity.
     * @param setName setName shows on which set's activity this function is called.
     * @return Cursor with all data from the table corresponding to the set.
     */
    public Cursor getCursor(String setName) {
        SQLiteDatabase db = getReadableDatabase();
        String q = "SELECT * FROM '" + setName +"'";
        Cursor c = db.rawQuery(q, null);
        c.moveToFirst();
        return c;
    }

    /**
     * This function gets the cursor for the ListView in SetActivity that is filtered.
     * @param setName setName shows on which set's activity this function is called.
     * @param constraint the function is showing rows from the setName table in which the
     *                   eng_word column have words with constraint pattern in them.
     * @return Cursor with filtered data from the table corresponding to the set.
     */
    public Cursor getFilteredCursor(String setName, CharSequence constraint) {
        SQLiteDatabase db = getReadableDatabase();
        String q = "SELECT * FROM '" + setName + "' WHERE " + ENG_WORD + " LIKE '%" + constraint + "%'";
        Cursor c = db.rawQuery(q, null);
        c.moveToFirst();
        return c;
    }

    public Pair<ArrayList<String>, ArrayList<String>> getRandomWords(String setName, String language, int numberOfWords) {
        SQLiteDatabase db = getReadableDatabase();
        String q = "SELECT " + ENG_WORD + ", " + language + " FROM '" + setName + "' WHERE " + language + " != '" + NOT_GIVEN + "' ORDER BY RANDOM() LIMIT " + numberOfWords;
        Cursor c = db.rawQuery(q, null);
        ArrayList<String> engWords = new ArrayList<>();
        ArrayList<String> translations = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                engWords.add(c.getString(0));
                translations.add(c.getString(1));
            } while (c.moveToNext());
        }
        c.close();
        Pair<ArrayList<String>, ArrayList<String>> result = new Pair<>(engWords, translations);
        return result;
    }

    public Pair<Integer, Integer> getSetSize(String setName) {
        SQLiteDatabase db = getReadableDatabase();
        String qFr = "SELECT COUNT() FROM '" + setName + "' WHERE " + FR_WORD + " != '" + NOT_GIVEN + "'";
        String qSpn = "SELECT COUNT() FROM '" + setName + "' WHERE " + SPN_WORD + " != '" + NOT_GIVEN + "'";

        Cursor cFr = db.rawQuery(qFr, null);
        cFr.moveToFirst();
        int sizeFr = cFr.getInt(0);

        Cursor cSpn = db.rawQuery(qSpn, null);
        cSpn.moveToFirst();
        int sizeSpn = cSpn.getInt(0);

        return new Pair<>(sizeFr, sizeSpn);
    }

    public void setHighscore(String setName, int score) {
        SQLiteDatabase db = getWritableDatabase();
        String q = "SELECT " + HIGHSCORE + " FROM '" + MAIN_TABLE_NAME + "' WHERE " + SET_NAME + " = '" + setName + "'";
        Cursor c = db.rawQuery(q, null);
        c.moveToFirst();

        int currentHighscore = c.getInt(0);
        if (score > currentHighscore) {
            ContentValues cv = new ContentValues();
            cv.put(HIGHSCORE, score);
            db.update(MAIN_TABLE_NAME, cv, SET_NAME + " = ?", new String[]{setName});
        }
    }
}

