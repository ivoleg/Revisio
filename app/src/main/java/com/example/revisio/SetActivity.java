package com.example.revisio;

import static com.example.revisio.Helpers.CustomDialog.ADD_WORD;
import static com.example.revisio.Helpers.CustomDialog.DELETE_WORD;
import static com.example.revisio.Helpers.DataBaseHelper.ENG_WORD;
import static com.example.revisio.Helpers.DataBaseHelper.FR_WORD;
import static com.example.revisio.Helpers.DataBaseHelper.NOT_GIVEN;
import static com.example.revisio.Helpers.DataBaseHelper.SET_IMAGE_URL;
import static com.example.revisio.Helpers.DataBaseHelper.SET_NAME;
import static com.example.revisio.Helpers.DataBaseHelper.SPN_WORD;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.revisio.Helpers.CustomDialog;
import com.example.revisio.Helpers.DataBaseHelper;
import com.example.revisio.databinding.ActivitySetBinding;

public class SetActivity extends AppCompatActivity {

    private String setName;
    public static final String TAG = "Custom Dialog";
    public static final String PURPOSE = "Dialog Purpose";

    private ActivitySetBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        setName = extras.getString(SET_NAME);

        Glide.with(SetActivity.this)
                .asBitmap()
                .load(extras.getString(SET_IMAGE_URL))
                .into(binding.setImg);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(SetActivity.this);
        Cursor c = dataBaseHelper.getCursor(setName);
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.set_words_list_item,
                c,
                new String[]{ENG_WORD, FR_WORD, SPN_WORD},
                new int[]{R.id.txtEngWord, R.id.txtFrWord, R.id.txtSpnWord},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) {
            @Override
            public void setViewText(TextView v, String text) {
                if (setName.equals("countries and nationalities")) {
                    super.setViewText(v, formattedText(text));
                } else {
                    super.setViewText(v, text);
                }
            }
        };
        simpleCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex == 2) {
                    String frWord = cursor.getString(columnIndex);
                    TextView txtFrWord = (TextView) view;
                    if (frWord.equals(NOT_GIVEN)) {
                        txtFrWord.setText("");
                        return true;
                    } else if (frWord.length() > 16){
                        String frWordFormatted = frWord.substring(0,16) + "\n" + frWord.substring(16);
                        txtFrWord.setText(frWordFormatted);
                        return true;
                    } else {
                        return false;
                    }
                } else if (columnIndex == 3) {
                    String spnWord = cursor.getString(columnIndex);
                    TextView txtSpnWord = (TextView) view;
                    if (spnWord.equals(NOT_GIVEN)) {
                        txtSpnWord.setText("");
                        return true;
                    } else if (spnWord.length() > 16){
                        spnWord = spnWord.substring(0,16) + "\n" + spnWord.substring(16);
                        txtSpnWord.setText(spnWord);
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
        });
        simpleCursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return dataBaseHelper.getFilteredCursor(setName, constraint);
            }
        });

        binding.wordSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                simpleCursorAdapter.getFilter().filter(newText);
                return false;
            }
        });

        binding.setWordsList.setAdapter(simpleCursorAdapter);

        binding.setBtnAdd.setOnClickListener(createDialogOnClick(ADD_WORD));

        binding.setBtnDelete.setOnClickListener(createDialogOnClick(DELETE_WORD));

    }

    /**
     * Creates OnClickListener that creates new CustomDialog, depending on purpose of the dialog.
     * @param purpose "Add word" if it is dialog to add words and "Delete word" if it is dialog to delete words.
     * @return View.OnClickListener in order to pass it to the Add or Delete button.
     */

    public View.OnClickListener createDialogOnClick(String purpose) {
        View.OnClickListener result = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog customDialog = new CustomDialog();
                Bundle bundle = new Bundle();
                bundle.putString(SET_NAME, setName);
                bundle.putString(PURPOSE, purpose);
                customDialog.setArguments(bundle);
                customDialog.show(getSupportFragmentManager(), TAG);
            }
        };
        return result;
    }

    /**
     * Changes first letter of the string to Upper case.
     * @param s the string that needs to be changed.
     * @return s starting with Upper case letter.
     */
    public String formattedText(String s) {
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }
}