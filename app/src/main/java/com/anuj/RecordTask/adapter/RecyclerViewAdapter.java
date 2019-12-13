package com.anuj.RecordTask.adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.anuj.RecordTask.CompletedActivity;
import com.anuj.RecordTask.ListActivity;
import com.anuj.RecordTask.MainActivity;
import com.anuj.RecordTask.R;
import com.anuj.RecordTask.data.DatabaseHandler;
import com.anuj.RecordTask.model.Task;
import com.google.android.material.snackbar.Snackbar;

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
        holder.taskDate.setText(String.format("To be done on: %s", task.getDate()));
    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        private TextView taskTitle;
        private TextView taskDescription;
        private TextView taskDate;
        private CheckBox checkBox;
        public Button deleteBtn;
        public Button editBtn;

        //Variables for popup
        public AlertDialog.Builder builder;
        public AlertDialog dialog;
        public LayoutInflater inflater;
        public EditText taskTitleBox;
        public EditText taskDescriptionBox;
        public Button dateBtn;
        public Button updateBtn;
        public Calendar calendar;
        public String dateStr;
        public DatePickerDialog datePickerDialog;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDescription = itemView.findViewById(R.id.taskDescription);
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

        public void deleteTask(int id) {
            Log.d("Deleting", " "+id);
            DatabaseHandler db = new DatabaseHandler(context);
            db.deleteTask(id);
            taskList.remove(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());

            // Check whether the ListActivity is empty or not
            if(db.getCount(0) == 0 || db.getCount(1) == 1) {
                context.startActivity(new Intent(context, MainActivity.class));
            }
        }

        public void updateStatus(Task task) {
            DatabaseHandler db = new DatabaseHandler(context);
            db.updateTask(task);
            if(task.getStatus() == 1) {
                Toast.makeText(context, "Added to Completed Tasks", Toast.LENGTH_SHORT).show();
                // Check whether the ListActivity is empty or not
                if(db.getCount(0) == 0)
                    context.startActivity(new Intent(context, MainActivity.class));
            }
            else {
                Toast.makeText(context, "Removed from Completed Tasks", Toast.LENGTH_SHORT).show();
            }

            taskList.remove(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());
        }

        public void editTask(final Task task) {
            final int st = task.getStatus();
            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.inputpopup, null);

            // Getting the object of input datas
            taskTitleBox = view.findViewById(R.id.taskTitle);
            taskDescriptionBox = view.findViewById(R.id.taskDescription);
            updateBtn = view.findViewById(R.id.addBtn);
            updateBtn.setText("Update");
            dateBtn = view.findViewById(R.id.taskDate);

            // Populating the View with Task details
            taskTitleBox.setText(task.getTaskTitle());
            taskDescriptionBox.setText(task.getTaskDescription());
            dateBtn.setText(task.getDate());

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