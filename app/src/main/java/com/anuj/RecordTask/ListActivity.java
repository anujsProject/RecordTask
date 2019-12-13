package com.anuj.RecordTask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anuj.RecordTask.adapter.RecyclerViewAdapter;
import com.anuj.RecordTask.data.DatabaseHandler;
import com.anuj.RecordTask.model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<Task> taskList;
    private Button completedBtn;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;
    private EditText taskTitle;
    private EditText taskDescription;
    private Button dateBtn;
    private Button addBtn;
    private Calendar calendar;
    private  Calendar prevDate;
    private String dateStr;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                createPopup();
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
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.listActivityLayout);
            linearLayout.removeView(recyclerView);
            TextView txt1 = new TextView(ListActivity.this);
            txt1.setText("You have not added any task Yet!");
            txt1.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            txt1.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            txt1.setTextSize(18);
            linearLayout.setBackgroundColor(Color.TRANSPARENT);
            linearLayout.addView(txt1);

        }

        completedBtn = findViewById(R.id.completedBtn);
        completedBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListActivity.this, CompletedActivity.class));
            }
        }) ;



    }


    private void createPopup() {
        builder = new AlertDialog.Builder(ListActivity.this);
        inflater = LayoutInflater.from(ListActivity.this);
        View view = inflater.inflate(R.layout.inputpopup, null);

        // Getting the object of input datas
        taskTitle = view.findViewById(R.id.taskTitle);
        taskDescription = view.findViewById(R.id.taskDescription);
        addBtn = view.findViewById(R.id.addBtn);
        dateBtn = view.findViewById(R.id.taskDate);

        builder.setView(view);
        dialog = builder.create();
        dialog.show();

        dateStr = null;
        dateBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                datePickerDialog = new DatePickerDialog(ListActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        DateFormat dateFormat = DateFormat.getDateInstance();
                        dateStr = dateFormat.format(new Date(year-1900, month, dayOfMonth));
                        dateBtn.setText(dateStr);

                    }
                }, day, month, year);

                datePickerDialog.getDatePicker().setMinDate(java.lang.System.currentTimeMillis());
                datePickerDialog.show();

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler db = new DatabaseHandler(ListActivity.this);
                Task task = new Task();
                task.setTaskTitle(taskTitle.getText().toString().trim());
                task.setTaskDescription(taskDescription.getText().toString().trim());
                task.setDate(dateStr);
                task.setStatus(0);

                if(!task.getTaskTitle().isEmpty()
                        && task.getDate() != null) {
                    db.addTask(task);
                    Snackbar.make(v, "Task Added", Snackbar.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            startActivity(new Intent(ListActivity.this, ListActivity.class));
                            finish();
                        }
                    }, 800);
                }

                else {
                    Snackbar.make(v, "Mondatory Fields are Empty", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(taskList.size() > 0)
            finishAffinity();
    }
    /*--------------------------*/
}
