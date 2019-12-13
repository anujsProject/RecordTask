package com.anuj.RecordTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anuj.RecordTask.adapter.RecyclerViewAdapter;
import com.anuj.RecordTask.data.DatabaseHandler;
import com.anuj.RecordTask.model.Task;

import java.util.List;

public class CompletedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<Task> taskList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed);

        DatabaseHandler db = new DatabaseHandler(CompletedActivity.this);
        taskList = db.getAllTask(1);
        recyclerView = findViewById(R.id.recyclerViewCompleted);
        if(taskList.size() != 0) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(CompletedActivity.this));

            // Attaching Adapter
            recyclerViewAdapter = new RecyclerViewAdapter(CompletedActivity.this, taskList);
            recyclerView.setAdapter(recyclerViewAdapter);
        }
        else {
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearCompleted);
            linearLayout.removeView(recyclerView);
            TextView txt1 = new TextView(CompletedActivity.this);
            txt1.setText("You have not completed any task Yet!");
            txt1.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            txt1.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            txt1.setTextSize(18);
            linearLayout.setBackgroundColor(Color.TRANSPARENT);
            linearLayout.addView(txt1);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DatabaseHandler db = new DatabaseHandler(CompletedActivity.this);
        if(db.getCount(0) != 0){
            startActivity(new Intent(CompletedActivity.this, ListActivity.class));
        }
        else {
            startActivity(new Intent(CompletedActivity.this, MainActivity.class));
        }

    }
}
