package com.example.revisio.Helpers;
import static com.example.revisio.CompetitiveSetActivity.COMPETITIVE;
import static com.example.revisio.Helpers.DataBaseHelper.FR_WORD;
import static com.example.revisio.Helpers.DataBaseHelper.SET_NAME;
import static com.example.revisio.Helpers.DataBaseHelper.SET_IMAGE_URL;
import static com.example.revisio.Helpers.DataBaseHelper.SPN_WORD;
import static com.example.revisio.Game.GameView.ACTIVITY_FROM;
import static com.example.revisio.TrainingSetActivity.TRAIN;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.example.revisio.Game.GameActivity;
import com.example.revisio.SetActivity;
import com.example.revisio.databinding.SetListItemBinding;

import java.util.ArrayList;

public class SetRecyclerViewAdapter extends RecyclerView.Adapter<SetRecyclerViewAdapter.ViewHolder>
        implements Filterable {

    public static final String DIFFICULTY = "difficulty";
    public static final String LANGUAGE = "language";
    public static final String NUMBER_OF_WORDS = "numberOfWords";
    private SetListItemBinding binding;

    private ArrayList<SetModel> setModels = new ArrayList<>();
    private ArrayList<SetModel> setModelsSearchCopy = new ArrayList<>();
    private final Context mContext;
    private String variant;

    public SetRecyclerViewAdapter(Context mContext, String variant) {
        this.mContext = mContext;
        this.variant = variant;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = SetListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(setModels.get(position));
    }

    @Override
    public int getItemCount() {
        return setModels.size();
    }

    public void setSetModels(ArrayList<SetModel> setModels) {
        this.setModels = setModels;
        this.setModelsSearchCopy = setModels;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<SetModel> filtered = new ArrayList<>();
                if (constraint.toString().isEmpty() || constraint.length() == 0){
                    filtered.addAll(setModelsSearchCopy);
                } else {
                    for (SetModel setModel : setModelsSearchCopy) {
                        if (setModel.getName().toLowerCase().trim().contains(constraint.toString().toLowerCase().trim())) {
                            filtered.add(setModel);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values=filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                setModels = (ArrayList<SetModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private SetListItemBinding view;

        public ViewHolder(SetListItemBinding view) {
            super(binding.getRoot());
            this.view = view;
        }

        public void bind(SetModel setModel) {
            view.setListNumberOfWords.setImeOptions(EditorInfo.IME_ACTION_DONE);
            Glide.with(mContext)
                    .asBitmap().
                    load(setModel.getImageUrl()).
                    into(view.setListImg);
            if (variant.equals(TRAIN)) {
                setHighscoreVisibility(View.GONE);
            } else if (variant.equals(COMPETITIVE)) {
                setHighscoreVisibility(View.VISIBLE);
                view.setListHighscore.setText(String.valueOf(setModel.getHighscore()));
            }
            String formattedName = setModel.getName().substring(0,1).toUpperCase() + setModel.getName().substring(1);
            view.setListName.setText(formattedName);
            view.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, SetActivity.class);
                    intent.putExtra(SET_NAME, setModel.getName());
                    intent.putExtra(SET_IMAGE_URL, setModel.getImageUrl());
                    mContext.startActivity(intent);
                }
            });

            if (setModel.isExpanded()) {
                TransitionManager.beginDelayedTransition(view.parent);
                view.expandedSet.setVisibility(View.VISIBLE);
                view.setListArrowDown.setVisibility(View.GONE);
            } else {
                TransitionManager.beginDelayedTransition(view.parent);
                view.expandedSet.setVisibility(View.GONE);
                view.setListArrowDown.setVisibility(View.VISIBLE);
            }

            view.setListStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataBaseHelper dataBaseHelper = new DataBaseHelper(mContext);
                    String number = view.setListNumberOfWords.getText().toString();
                    if (number.trim().equals("")) {
                        Toast.makeText(mContext, "Please, enter the number of words to learn", Toast.LENGTH_LONG).show();
                    } else {

                        int numberOfWords = Integer.parseInt(view.setListNumberOfWords.getText().toString());
                        Pair<Integer, Integer> numberOfTranslations = dataBaseHelper.getSetSize(setModel.getName());
                        int numberOfFrTranslations = numberOfTranslations.first;
                        int numberOfSpnTranslations = numberOfTranslations.second;
                        String language = view.setListLanguageSpinner.getSelectedItem().toString();
                        if (language.equals("French")) {
                            language = FR_WORD;
                        } else if (language.equals("Spanish")) {
                            language = SPN_WORD;
                        }

                        if (numberOfWords <= 3) {
                            Toast.makeText(mContext, "The number of words must be at least 4.", Toast.LENGTH_LONG).show();
                        } else if (language.equals(FR_WORD) && numberOfWords > numberOfFrTranslations) {
                            Toast.makeText(mContext, "This set doesn't have that many French translations.", Toast.LENGTH_LONG).show();
                        } else if (language.equals(SPN_WORD) && numberOfWords > numberOfSpnTranslations) {
                            Toast.makeText(mContext, "This set doesn't have that many Spanish translations.", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(mContext, GameActivity.class);
                            intent.putExtra(SET_NAME, setModel.getName());
                            RadioButton rb = view.parent.findViewById(view.setListDifficultyGroup.getCheckedRadioButtonId());
                            intent.putExtra(DIFFICULTY, rb.getText().toString());
                            intent.putExtra(LANGUAGE, language);
                            intent.putExtra(NUMBER_OF_WORDS, numberOfWords);
                            intent.putExtra(ACTIVITY_FROM, variant);

                            mContext.startActivity(intent);
                        }
                    }
                }
            });

            view.setListArrowDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setModel.setExpanded(true);
                    notifyItemChanged(getAdapterPosition());
                }
            });

            view.setListArrowUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setModel.setExpanded(false);
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }

        private void setHighscoreVisibility(int visibility) {
            view.setListHighscore.setVisibility(visibility);
            view.setListTxtHighscore.setVisibility(visibility);
        }
    }
}
