package com.anuj.RecordTask;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.anuj.RecordTask.data.DatabaseHandler;
import com.anuj.RecordTask.model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;
    private EditText taskTitle;
    private EditText taskDescription;
    private Spinner taskPriority;
    private ArrayAdapter<String> spinnerAdapter;
    private Button dateBtn;
    private Button addBtn;
    private Calendar calendar;
    private String dateStr;
    private DatePickerDialog datePickerDialog;
    private Button completedBtn;
    private Button incompletedBtn;
    private String taskPriorityStr;

    private Boolean prioritySel = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bypassActivity();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);

        // Completed and Incomplete button
        completedBtn = findViewById(R.id.completedTaskBtn);
        incompletedBtn = findViewById(R.id.inCompletedTaskBtn);
        completedBtn.setOnClickListener(this);
        incompletedBtn.setOnClickListener(this);

        /* ---------
        ## Get rid of spinner for now
        ## Would like to implement in next version of the app
        ------------------------------------------------------
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.
                createFromResource(MainActivity.this, R.array.task_priorities, R.layout.inputpopup);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        */

    }

    private void bypassActivity() {
        DatabaseHandler db = new DatabaseHandler(MainActivity.this);
        if(db.getCount(0) != 0) {
            startActivity(new Intent(MainActivity.this, ListActivity.class));
        }
    }

    private void createPopup() {
        builder = new AlertDialog.Builder(MainActivity.this);
        inflater = LayoutInflater.from(MainActivity.this);
        View view = inflater.inflate(R.layout.inputpopup, null);

        // Getting the object of input datas
        taskTitle = view.findViewById(R.id.taskTitle);
        taskDescription = view.findViewById(R.id.taskDescription);

        // Creating Spinner
        final List<String> prioritiesList = new ArrayList<>();
        prioritiesList.add("Select Priority");
        prioritiesList.add("High");
        prioritiesList.add("Medium");
        prioritiesList.add("Low");
        taskPriority = view.findViewById(R.id.taskPriority);
        spinnerAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, prioritiesList) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0; // If Position is 0 , then it will return false otherwise true
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;

                if(position == 0) {
                    textView.setTextColor(Color.GRAY);
                }
                else {
                    textView.setTextColor(Color.BLACK);
                }
                return view;
            }

        };
//        spinnerAdapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.task_priorities, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskPriority.setAdapter(spinnerAdapter);
        taskPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position > 0) {
                    taskPriorityStr = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                prioritySel = false;
            }
        });


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
                 final int day = calendar.get(Calendar.DAY_OF_MONTH);
                 int month = calendar.get(Calendar.MONTH);
                 int year = calendar.get(Calendar.YEAR);
                datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                DatabaseHandler db = new DatabaseHandler(MainActivity.this);
                Task task = new Task();
                task.setTaskTitle(taskTitle.getText().toString().trim());
                task.setTaskDescription(taskDescription.getText().toString().trim());
                task.setTaskPriority(taskPriorityStr);
                task.setDate(dateStr);
                task.setStatus(0);

                if(!task.getTaskTitle().isEmpty()
                        && task.getDate() != null
                        && prioritySel) {
                    db.addTask(task);
                    Snackbar.make(v, "Task Added", Snackbar.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            startActivity(new Intent(MainActivity.this, ListActivity.class));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.completedTaskBtn:
                startActivity(new Intent(MainActivity.this, CompletedActivity.class));
                break;

            case R.id.inCompletedTaskBtn:
                startActivity(new Intent(MainActivity.this, ListActivity.class));
                break;

            case R.id.fab:
                createPopup();
                break;

        }
    }
}
