package com.anuj.RecordTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anuj.RecordTask.adapter.RecyclerViewAdapter;
import com.anuj.RecordTask.data.DatabaseHandler;
import com.anuj.RecordTask.model.Task;
import com.anuj.RecordTask.ui.CreateTaskPopup;
import com.anuj.RecordTask.ui.RemindTask;
import com.anuj.RecordTask.ui.TaskNav;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<Task> taskList;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                CreateTaskPopup popUp = new CreateTaskPopup(ListActivity.this);
                popUp.showPopup();
                popUp.submitPopup();
            }
        });

        DatabaseHandler db = new DatabaseHandler(ListActivity.this);
        taskList = db.getAllTask(0);
        recyclerView = findViewById(R.id.recyclerView);

        if(taskList.size() != 0) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(ListActivity.this));

            // Attaching the Adapter
            recyclerViewAdapter = new RecyclerViewAdapter(ListActivity.this, taskList);
            recyclerView.setAdapter(recyclerViewAdapter);
        }
        else {
            LinearLayout linearLayout =  findViewById(R.id.listActivityLayout);
            linearLayout.removeView(recyclerView);
            TextView txt1 = new TextView(ListActivity.this);
            txt1.setText(R.string.not_added_task_msg);
            txt1.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            txt1.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            txt1.setTextSize(18);
            linearLayout.setBackgroundColor(Color.TRANSPARENT);
            linearLayout.addView(txt1);

        }





        // Handle the Navigation things
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        TaskNav.makeNav(this, drawerLayout, navigationView);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(taskList.size() > 0)
            finishAffinity();
    }
    /*--------------------------*/
}
