package com.example.revisio;

import static com.example.revisio.Helpers.CustomDialog.ADD_SET;
import static com.example.revisio.Helpers.CustomDialog.DELETE_SET;
import static com.example.revisio.SetActivity.PURPOSE;
import static com.example.revisio.SetActivity.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.example.revisio.Helpers.CustomDialog;
import com.example.revisio.Helpers.DataBaseHelper;
import com.example.revisio.Helpers.SetModel;
import com.example.revisio.Helpers.SetRecyclerViewAdapter;
import com.example.revisio.databinding.ActivityCompetitiveSetBinding;

import java.util.ArrayList;

public class CompetitiveSetActivity extends AppCompatActivity {

    public static final String COMPETITIVE = "Competitive mode";
    private SetRecyclerViewAdapter adapter;
    private DataBaseHelper db;
    private ActivityCompetitiveSetBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompetitiveSetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new DataBaseHelper(CompetitiveSetActivity.this);
        ArrayList<SetModel> setModels = db.getSetsModel();

        adapter = new SetRecyclerViewAdapter(this, COMPETITIVE);

        binding.competitiveSetsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.competitiveSetsRecyclerView.setAdapter(adapter);
        adapter.setSetModels(setModels);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sets_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search here for the Set");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addSet:
                createDialogOnOptionsItemSelected(ADD_SET);
                return true;
            case R.id.deleteSet:
                createDialogOnOptionsItemSelected(DELETE_SET);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createDialogOnOptionsItemSelected(String purpose) {
        CustomDialog customDialog = new CustomDialog();
        Bundle bundle = new Bundle();
        bundle.putString(PURPOSE, purpose);
        customDialog.setArguments(bundle);
        customDialog.show(getSupportFragmentManager(), TAG);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CompetitiveSetActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}