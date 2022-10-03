package com.example.revisio.Helpers;

import static androidx.databinding.DataBindingUtil.setContentView;
import static com.example.revisio.Helpers.DataBaseHelper.HIGHSCORE;
import static com.example.revisio.Helpers.DataBaseHelper.SET_NAME;
import static com.example.revisio.Game.GameView.ACTIVITY_FROM;
import static com.example.revisio.SetActivity.PURPOSE;
import static com.example.revisio.TrainingSetActivity.TRAIN;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.revisio.CompetitiveSetActivity;
import com.example.revisio.R;
import com.example.revisio.TrainingSetActivity;
import com.example.revisio.databinding.AddSetDialogBinding;
import com.example.revisio.databinding.AddWordDialogBinding;
import com.example.revisio.databinding.DeleteSetDialogBinding;
import com.example.revisio.databinding.DeleteWordDialogBinding;
import com.example.revisio.databinding.EndCompetitiveGameDialogBinding;
import com.example.revisio.databinding.EndTrainingGameDialogBinding;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class CustomDialog extends AppCompatDialogFragment {

    public static final String DELETE_WORD = "Delete word";
    public static final String ADD_WORD = "Add word";
    public static final String DELETE_SET = "Delete set";
    public static final String ADD_SET = "Add set";
    public static final String END_GAME = "End the game";
    private static final String DEFAULT_IMG_URL = "https://cdn.pixabay.com/photo/2018/12/25/15/57/friendship-3894444_960_720.png";

    public String errorMsg;
    private String dialogPurpose;
    private String setName;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        Bundle bundle = getArguments();
        dialogPurpose = bundle.getString(PURPOSE);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
        /*
         * Depending on the purpose that was passed on the creation of the dialog, the layout and behavior of it will be different.
         */
        switch (dialogPurpose) {
            /*
             * If the purpose is to add a word into the set in SetActivity.
             * All the words are converted by toLowerCase() and trim().
             * At least one translation should exist (be not equal to "").
             * English word should never be empty.
             */
            case ADD_WORD: {
                setName = bundle.getString(SET_NAME);
                AddWordDialogBinding binding = AddWordDialogBinding.inflate(LayoutInflater.from(getContext()));
                builder.setView(binding.getRoot())
                        .setTitle("Add words into " + setName.substring(0,1).toUpperCase() + setName.substring(1))
                        .setNegativeButton("Go back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String engWord = binding.editTextEngWord.getText().toString().toLowerCase().trim();
                                String frWord = binding.editTextFrWord.getText().toString().toLowerCase().trim();
                                String spnWord = binding.editTextSpnWord.getText().toString().toLowerCase().trim();
                                if (engWord.equals("")) {
                                    errorMsg = "You cannot have empty English word";
                                    showSnackBar(R.id.setParent, errorMsg);
                                } else {
                                    if (frWord.equals("") && spnWord.equals("")) {
                                        errorMsg = "Please, add at least one translation";
                                        showSnackBar(R.id.setParent, errorMsg);
                                    } else {
                                        if (dataBaseHelper.addWord(setName, engWord, frWord, spnWord)) {
                                            restartActivity();
                                        } else {
                                            errorMsg = "You are trying to add the english word that is already existing!";
                                            showSnackBar(R.id.setParent, errorMsg);;
                                        }
                                    }
                                }
                            }
                        });
                break;
            }
            /*
             * If the purpose is to delete a word from a set in SetActivity.
             * Because toLowerCase() and trim() are casted on all words at the time of adding
             * they are also casted on the deleted word. (So "  TeSt   " will delete word "test")
             */
            case DELETE_WORD: {
                setName = bundle.getString(SET_NAME);
                DeleteWordDialogBinding binding = DeleteWordDialogBinding.inflate(LayoutInflater.from(getContext()));
                builder.setView(binding.getRoot())
                        .setTitle("Delete words from " + setName.substring(0,1).toUpperCase() + setName.substring(1))
                        .setNegativeButton("Go back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (dataBaseHelper.deleteWord(setName, binding.editTxtWordToDelete.getText().toString().toLowerCase().trim())) {
                                    restartActivity();
                                } else {
                                    errorMsg = "There is no such word in this Set, please check again.";
                                    showSnackBar(R.id.setParent, errorMsg);
                                }
                            }
                        });
                break;
            }

            /*
             * If the purpose is to add a new Set into the RecyclerView in TrainingSetActivity.
             */
            case ADD_SET: {
                AddSetDialogBinding binding = AddSetDialogBinding.inflate(LayoutInflater.from(getContext()));
                builder.setView(binding.getRoot())
                        .setTitle("Add new Set of words")
                        .setNegativeButton("Go back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String addedSetName = binding.editTextAddedSetName.getText().toString().trim();
                                String addedSetImageUrl = binding.editTextAddedSetImageUrl.getText().toString().trim();
                                if (addedSetName.equals("")) {
                                    errorMsg = "You cannot add Set with empty name";
                                    showSnackBar(R.id.trainingSetParent, errorMsg);
                                } else {
                                    if (addedSetImageUrl.equals("")) {
                                        addedSetImageUrl = DEFAULT_IMG_URL;
                                    }
                                    if (dataBaseHelper.addSet(addedSetName, addedSetImageUrl)) {
                                        restartActivity();
                                    } else {
                                        errorMsg = "You cannot add Set with the same name as existing one!";
                                        showSnackBar(R.id.trainingSetParent, errorMsg);
                                    }
                                }
                            }
                        });
                break;
            }
            /*
             * If the purpose is to delete the Set from the RecyclerView in TrainingSetActivity.
             */
            case DELETE_SET: {
                DeleteSetDialogBinding binding = DeleteSetDialogBinding.inflate(LayoutInflater.from(getContext()));
                builder.setView(binding.getRoot())
                        .setTitle("Delete any Set of words")
                        .setNegativeButton("Go back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(dataBaseHelper.deleteSet(binding.editTextDeletedSetName.getText().toString())) {
                                    restartActivity();
                                } else {
                                    errorMsg = "There is no set with that name.";
                                    showSnackBar(R.id.trainingSetParent, errorMsg);
                                }
                            }
                        });
                break;
            }
            /*
            For the final screen of the game. Different, depending on from which activity the game was started.
            On go back, returns user to that activity.
             */
            case END_GAME:
                String from = bundle.getString(ACTIVITY_FROM);
                if (from.equals(TRAIN)) {
                    EndTrainingGameDialogBinding binding = EndTrainingGameDialogBinding.inflate(LayoutInflater.from(getContext()));
                    builder.setView(binding.getRoot())
                            .setTitle("The end!")
                            .setNeutralButton("Go back", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getContext(), TrainingSetActivity.class);
                                    startActivity(intent);
                                }
                            });
                } else {
                    setName = bundle.getString(SET_NAME);
                    int score = bundle.getInt(HIGHSCORE);

                    EndCompetitiveGameDialogBinding binding = EndCompetitiveGameDialogBinding.inflate(LayoutInflater.from(getContext()));
                    binding.score.setText(String.valueOf(score));
                    builder.setView(binding.getRoot())
                            .setTitle("The end!")
                            .setNeutralButton("Go back", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getContext(), CompetitiveSetActivity.class);
                                    dataBaseHelper.setHighscore(setName, score);
                                    startActivity(intent);
                                }
                            });
                }
        }
        return builder.create();
    }


    /**
     * Restarts an activity in order to show changes in the database.
     */
    public void restartActivity() {
        getActivity().finish();
        getActivity().overridePendingTransition(0, 0);
        getActivity().startActivity(getActivity().getIntent());
        getActivity().overridePendingTransition(0, 0);
    }

    /**
     * Shows a Snackbar in the given View and with the given message.
     * @param layoutId id of the View.
     * @param errorMsg message, explaining the error.
     */
    public  void showSnackBar(int layoutId, String errorMsg) {
       Snackbar.make(getActivity().findViewById(layoutId), errorMsg, BaseTransientBottomBar.LENGTH_INDEFINITE)
               .setAction("Got it", new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {

                   }
               }).show();
    }
}
