package com.anuj.RecordTask.adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.anuj.RecordTask.CompletedActivity;
import com.anuj.RecordTask.ListActivity;
import com.anuj.RecordTask.MainActivity;
import com.anuj.RecordTask.R;
import com.anuj.RecordTask.data.DatabaseHandler;
import com.anuj.RecordTask.model.Task;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Task> taskList;

    public RecyclerViewAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e("Type", ""+viewType);
        View view = LayoutInflater.from(context).inflate(R.layout.task_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskTitle.setText(String.format("Task: %s", task.getTaskTitle()));
        holder.taskDescription.setText(String.format("Description: %s", task.getTaskDescription()));
        holder.taskPriority.setText(String.format("Priority: %s", task.getTaskPriority()));
        holder.taskDate.setText(String.format("Due date: %s", task.getDate()));
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        private TextView taskTitle;
        private TextView taskDescription;
        private TextView taskDate;
        private TextView taskPriority;
        private CheckBox checkBox;
        private Button deleteBtn;
        private Button editBtn;

        //Variables for popup
        private AlertDialog.Builder builder;
        private AlertDialog dialog;
        private LayoutInflater inflater;
        private EditText taskTitleBox;
        private EditText taskDescriptionBox;
        private Spinner taskPriorityBox;
        private Button dateBtn;
        private Button updateBtn;
        private Calendar calendar;
        private String dateStr;
        private String taskPriorityStr;
        private DatePickerDialog datePickerDialog;
        private ArrayAdapter<String> spinnerAdapter;

        // Variable for confirmation popup
        private Button yesBtn;
        private Button noBtn;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            taskPriority = itemView.findViewById(R.id.taskPriority);
            taskDate = itemView.findViewById(R.id.taskDate);
            checkBox = itemView.findViewById(R.id.taskTick);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            editBtn = itemView.findViewById(R.id.editBtn);

            if(taskList.get(0).getStatus() == 1){
                checkBox.setChecked(true);
            }

            // Invoking On click method
            checkBox.setOnClickListener(this);
            deleteBtn.setOnClickListener(this);
            editBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Task task = taskList.get(position);
            switch (v.getId()) {
                case R.id.taskTick:
                    if(checkBox.isChecked())
                        task.setStatus(1);
                    else
                        task.setStatus(0);
                    updateStatus(task);
                    break;

                case R.id.deleteBtn:
                    deleteTask(task.getTaskId());
                    break;

                case R.id.editBtn:
                    editTask(task);
                    break;

            }
        }

        private void deleteTask(final int id) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View confView = LayoutInflater.from(context).inflate(R.layout.confirmation_popup, null);
            yesBtn = confView.findViewById(R.id.yesBtn);
            noBtn = confView.findViewById(R.id.noBtn);
            builder.setView(confView);
            final AlertDialog dialog = builder.create();
            dialog.show();

            yesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHandler db = new DatabaseHandler(context);
                    db.deleteTask(id);
                    taskList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    dialog.dismiss();
                }
            });

            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            // Check whether the ListActivity is empty or not
//            if(db.getCount(0) == 0) {
//                // We will think what to do in this scenerio
//            }
        }

        private void updateStatus(Task task) {
            DatabaseHandler db = new DatabaseHandler(context);
            db.updateTask(task);
            if(task.getStatus() == 1) {
                Toast.makeText(context, "Added to Completed Tasks", Toast.LENGTH_SHORT).show();
                // Check whether the ListActivity is empty or not

//                if(db.getCount(0) == 0) {
//                   // We will think what to do in this scenerio
//                }
            }
            else {
                Toast.makeText(context, "Removed from Completed Tasks", Toast.LENGTH_SHORT).show();
            }

            taskList.remove(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());
        }

        private void editTask(final Task task) {
            final int st = task.getStatus();
            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.inputpopup, null);

            // Getting the object of input datas
            taskTitleBox = view.findViewById(R.id.taskTitle);
            taskDescriptionBox = view.findViewById(R.id.taskDescription);
            taskPriorityBox = view.findViewById(R.id.taskPriority);
            updateBtn = view.findViewById(R.id.addBtn);
            updateBtn.setText(R.string.update_text);
            dateBtn = view.findViewById(R.id.taskDate);


            // Populating the View with Task details
            taskTitleBox.setText(task.getTaskTitle());
            taskDescriptionBox.setText(task.getTaskDescription());
            dateBtn.setText(task.getDate());

            // Spinner Code
            // Creating Spinner
            final List<String> prioritiesList = new ArrayList<>();
            prioritiesList.add("Select Priority");
            prioritiesList.add("High");
            prioritiesList.add("Medium");
            prioritiesList.add("Low");
            spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, prioritiesList) {
                @Override
                public boolean isEnabled(int position) {
                    return position != 0;
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
            taskPriorityBox.setAdapter(spinnerAdapter);
            taskPriorityBox.setSelection(spinnerAdapter.getPosition(task.getTaskPriority()));
            taskPriorityBox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if(position > 0) {
                        taskPriorityStr = parent.getItemAtPosition(position).toString();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent){
                }
            });



            /*-------------------------------------_*/


            builder.setView(view);
            dialog = builder.create();
            dialog.show();
            dateStr = null;

            // Handling the Click of the Date Button
            dateBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);
                    datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            dateStr = dayOfMonth + "/" + (month+1) + "/" + year;
                            dateBtn.setText(dateStr);

                        }
                    }, day, month, year);

                    datePickerDialog.getDatePicker().setMinDate(java.lang.System.currentTimeMillis());
                    datePickerDialog.show();

                }
            });


            // Handling the Add Button
            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHandler db = new DatabaseHandler(context);
                    task.setTaskTitle(taskTitleBox.getText().toString().trim());
                    task.setTaskDescription(taskDescriptionBox.getText().toString().trim());
                    task.setDate(dateStr);
                    task.setStatus(st);

                    if(!task.getTaskTitle().isEmpty()
                            && task.getDate() != null) {
                        db.updateTask(task);
                        Snackbar.make(v, "Task Updated", Snackbar.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                if(context instanceof CompletedActivity) {
                                    context.startActivity(new Intent(context, CompletedActivity.class));
                                }
                                else {
                                    context.startActivity(new Intent(context, ListActivity.class));
                                }
                            }
                        }, 700);
                    }

                    else {
                        Snackbar.make(v, "Mondatory Fields are Empty", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });

        }


    }
}

/*------------------------------
## Refactor it for multiple uniqly styled row


public class RecyclerViewAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Task> taskList;

    public RecyclerViewAdapter(Context context) {
        this.context = context;
        this.taskList = null;
    }

    public RecyclerViewAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @Override
    public int getItemViewType(int position) {
        if (taskList.size() != 0)
            return 0;
        else
            return 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e("Type", "" + viewType);
        if (viewType == 0) {
            View view = LayoutInflater.from(context).inflate(R.layout.task_row, parent, false);
            return new taskViewHolder(view);
        } else if (viewType == 1) {
            View view = LayoutInflater.from(context).inflate(R.layout.completed_btn, parent, false);
            return new completedBtnViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

//        if(holder instanceof taskViewHolder) {
//            Task task = taskList.get(position);
//            ((taskViewHolder)holder).taskTitle.setText(String.format("Task: %s", task.getTaskTitle()));
//            ((taskViewHolder)holder).taskDescription.setText(String.format("Description: %s", task.getTaskDescription()));
//            ((taskViewHolder)holder).taskDate.setText(String.format("To be done on: %s", task.getDate()));
//        }

    }


    @Override
    public int getItemCount() {
        if (taskList != null)
            return taskList.size();

        return 0;
    }


    public class taskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView taskTitle;
        private TextView taskDescription;
        private TextView taskDate;
        private CheckBox checkBox;
        public Button deleteBtn;
        public Button editBtn;

        public taskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            taskDate = itemView.findViewById(R.id.taskDate);
            checkBox = itemView.findViewById(R.id.taskTick);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            editBtn = itemView.findViewById(R.id.editBtn);

            if (taskList.get(0).getStatus() == 1) {
                checkBox.setChecked(true);
            }

            // Invoking On click method
            checkBox.setOnClickListener(this);
            deleteBtn.setOnClickListener(this);
            editBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Task task = taskList.get(position);
            switch (v.getId()) {
                case R.id.taskTick:
                    if (checkBox.isChecked())
                        task.setStatus(1);
                    else
                        task.setStatus(0);
                    updateStatus(task);
                    Log.d("Status", " " + task);
                    break;

                case R.id.deleteBtn:
                    Log.d("Deleting", " " + task);
                    deleteTask(task.getTaskId());
                    break;

                case R.id.editBtn:
                    break;

            }
        }

        public void deleteTask(int id) {
            Log.d("Deleting", " " + id);
            DatabaseHandler db = new DatabaseHandler(context);
            db.deleteTask(id);
            taskList.remove(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());
        }

        public void updateStatus(Task task) {
            DatabaseHandler db = new DatabaseHandler(context);
            db.updateTask(task);
            if (task.getStatus() == 1)
                Toast.makeText(context, "Added to Completed Tasks", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "Removed from Completed Tasks", Toast.LENGTH_SHORT).show();

            taskList.remove(getAdapterPosition());
            notifyItemChanged(getAdapterPosition());
        }
    }

    public class completedBtnViewHolder extends RecyclerView.ViewHolder {
        private Button completedBtn;

        public completedBtnViewHolder(@NonNull View itemView) {
            super(itemView);
            completedBtn = itemView.findViewById(R.id.completedButton);
        }

    }
}
--------------------------
 */