package com.anuj.RecordTask.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.anuj.RecordTask.CompletedActivity;
import com.anuj.RecordTask.R;
import com.anuj.RecordTask.data.DatabaseHandler;
import com.anuj.RecordTask.model.Task;
import com.anuj.RecordTask.ui.CreateTaskPopup;

import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Task> taskList;
    private AlertDialog dialog;

    public RecyclerViewAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e("Type", "" + viewType);
        View view = LayoutInflater.from(context).inflate(R.layout.task_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskTitle.setText(String.format("Task: %s", task.getTaskTitle()));
        holder.taskDescription.setText(String.format("Description: %s", task.getTaskDescription()));

        // Handling the color of Priority of items
        if(task.getTaskPriority().equals("High")) {
            holder.taskPriority.setTextColor(Color.rgb(255,69,0));
        }

        else if(task.getTaskPriority().equals("Medium")) {
            holder.taskPriority.setTextColor(Color.rgb(221, 60, 0));
        }
        else {
            holder.taskPriority.setTextColor(Color.rgb(255,215,0));
        }
        holder.taskPriority.setText(String.format("Priority: %s", task.getTaskPriority()));
        holder.taskDate.setText(String.format("Due date: %s", task.getDate()));
        /*--------------------------------*/

        // Formatting Time
        if(task.getTime() != null){
            if(Integer.parseInt(task.getTime().split(":")[0]) <= 12){
                holder.taskTime.setText(String.format("Reminding at: %s",
                        String.format("%02d", Integer.parseInt(task.getTime().split(":")[0]))+":"+String.format("%02d", Integer.parseInt(task.getTime().split(":")[1]))+" AM"));
            }
            else {
                holder.taskTime.setText(String.format("Reminding at: %s",
                        String.format("%02d", Integer.parseInt(task.getTime().split(":")[0])%12)+":"+String.format("%02d", Integer.parseInt(task.getTime().split(":")[1]))+" PM"));
            }
        }

        else {
            holder.taskTime.setText(String.format("Reminding at: %s", "Not Set"));
        }
        /*----------------------------------------*/

        // Showing the task status through checkbox status
        if (taskList.get(position).getStatus() == 1) {
            holder.checkBox.setChecked(true);
        }
     }


    @Override
    public int getItemCount() {
        return taskList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView taskTitle;
        private TextView taskDescription;
        private TextView taskDate;
        private TextView taskTime;
        private TextView taskPriority;
        private CheckBox checkBox;
        private Button deleteBtn;
        private Button editBtn;


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
            taskTime = itemView.findViewById(R.id.taskTime);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            editBtn = itemView.findViewById(R.id.editBtn);


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

            showConfirmBox();

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

        private void updateStatus(final Task task) {
            if (checkBox.isChecked())
                task.setStatus(1);
            else
                task.setStatus(0);

            showConfirmBox();

            yesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHandler db = new DatabaseHandler(context);
                    db.updateTask(task);
                    if (task.getStatus() == 1) {
                        Toast.makeText(context, "Added to Completed Tasks", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(context, "Removed from Completed Tasks", Toast.LENGTH_SHORT).show();
                    }

                    taskList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    dialog.dismiss();
                }
            });

            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (task.getStatus() == 1) {
                        checkBox.setChecked(false);
                    }
                    else {
                        checkBox.setChecked(true);
                    }
                }
            });


        }

        private void showConfirmBox() {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View confView = LayoutInflater.from(context).inflate(R.layout.confirmation_popup, null);
            yesBtn = confView.findViewById(R.id.yesBtn);
            noBtn = confView.findViewById(R.id.noBtn);
            builder.setView(confView);
            dialog = builder.create();
            dialog.show();
        }

        private void editTask(final Task task) {
            CreateTaskPopup popUp = new CreateTaskPopup(context);
            popUp.showPopup();
            popUp.populatePopup(task);
            popUp.submitEditedPopup(task);
        }
    }
}