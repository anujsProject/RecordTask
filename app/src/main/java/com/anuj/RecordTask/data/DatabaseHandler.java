package com.anuj.RecordTask.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.anuj.RecordTask.Util.Util;
import com.anuj.RecordTask.model.Task;

import java.io.UTFDataFormatException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    public DatabaseHandler(Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_COMMAND = "CREATE TABLE "+Util.TASK_TABLE_NAME+" ( "
                +Util.KEY_ID + " INTEGER PRIMARY KEY, "
                +Util.KEY_TITLE + " TEXT,"
                +Util.KEY_DESCRIPTION +" TEXT, "
                +Util.KEY_PRIORITY + " TEXT, "
                +Util.KEY_DATE + " TEXT, "
                +Util.KEY_TIME + " TEXT, "
                +Util.KEY_STATUS + " INTEGER );";

        db.execSQL(CREATE_TABLE_COMMAND);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_TABLE_COMMAND = "DROP TABLE IF EXISTS " +Util.TASK_TABLE_NAME;
        db.execSQL(DROP_TABLE_COMMAND);
        onCreate(db);
    }

    // CRUD Operations

    // ADD TASK
    public void addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Util.KEY_TITLE, task.getTaskTitle());
        values.put(Util.KEY_DESCRIPTION, task.getTaskDescription());
        values.put(Util.KEY_PRIORITY, task.getTaskPriority());
        values.put(Util.KEY_DATE, task.getDate());
        values.put(Util.KEY_TIME, task.getTime());
        values.put(Util.KEY_STATUS, task.getStatus());

        // Inserting task using insert method
        db.insert(Util.TASK_TABLE_NAME, null, values);
        db.close();
    }

    // GET TASK
    public Task getTask(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Util.TASK_TABLE_NAME,
                new String[] {Util.KEY_ID, Util.KEY_TITLE, Util.KEY_DESCRIPTION, Util.KEY_PRIORITY, Util.KEY_DATE, Util.KEY_TIME, Util.KEY_STATUS},
                Util.KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null, null);

        if(cursor != null)
            cursor.moveToFirst();

        Task task = new Task();
        if(cursor != null) {
            task.setTaskId(cursor.getInt(cursor.getColumnIndex(Util.KEY_ID)));
            task.setTaskTitle(cursor.getString(cursor.getColumnIndex(Util.KEY_TITLE)));
            task.setTaskDescription(cursor.getString(cursor.getColumnIndex(Util.KEY_DESCRIPTION)));
            task.setTaskPriority(cursor.getString(cursor.getColumnIndex(Util.KEY_PRIORITY)));
            task.setDate(cursor.getString(cursor.getColumnIndex(Util.KEY_DATE)));
            task.setTime(cursor.getString(cursor.getColumnIndex(Util.KEY_TIME)));
            task.setStatus(cursor.getInt(cursor.getColumnIndex(Util.KEY_STATUS)));
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return task;
    }


    // GET ALL TASKS
    public List<Task> getAllTask(int st) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Task> taskList = new ArrayList<>();
        Cursor cursor = db.query(Util.TASK_TABLE_NAME,
                new String[] {Util.KEY_ID, Util.KEY_TITLE, Util.KEY_DESCRIPTION, Util.KEY_PRIORITY, Util.KEY_DATE, Util.KEY_TIME, Util.KEY_STATUS},
                Util.KEY_STATUS + "=?",
                new String[]{String.valueOf(st)}, null, null, null);

        Log.d("Cursor", " "+cursor);
        if(cursor != null && cursor.moveToFirst())  {
            cursor.moveToFirst();
            do {
                Task task = new Task();
                task.setTaskId(cursor.getInt(cursor.getColumnIndex(Util.KEY_ID)));
                task.setTaskTitle(cursor.getString(cursor.getColumnIndex(Util.KEY_TITLE)));
                task.setTaskDescription(cursor.getString(cursor.getColumnIndex(Util.KEY_DESCRIPTION)));
                task.setTaskPriority(cursor.getString(cursor.getColumnIndex(Util.KEY_PRIORITY)));
                task.setDate(cursor.getString(cursor.getColumnIndex(Util.KEY_DATE)));
                task.setTime(cursor.getString(cursor.getColumnIndex(Util.KEY_TIME)));
                task.setStatus(cursor.getInt(cursor.getColumnIndex(Util.KEY_STATUS)));
                taskList.add(task);
            } while(cursor.moveToNext());
        }

        Log.d("Status",""+taskList);

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return taskList;
    }

    // Delete a task
    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Util.TASK_TABLE_NAME, Util.KEY_ID + "=?", new String[] {String.valueOf(id)});
        db.close();
    }

    // Update a task
    public void updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Util.KEY_TITLE, task.getTaskTitle());
        values.put(Util.KEY_DESCRIPTION, task.getTaskDescription());
        values.put(Util.KEY_PRIORITY, task.getTaskPriority());
        values.put(Util.KEY_DATE, task.getDate());
        values.put(Util.KEY_TIME, task.getTime());
        values.put(Util.KEY_STATUS, task.getStatus());
        db.update(Util.TASK_TABLE_NAME, values,
                Util.KEY_ID + "=?", new String[]{String.valueOf(task.getTaskId())});
        db.close();
    }

    // Delete All Item
    public void deleteAllTask() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+Util.TASK_TABLE_NAME);
        db.close();
    }

    // Get Count of Task Added
    public int getCount(int st) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Util.TASK_TABLE_NAME, new String[] {Util.KEY_ID}, Util.KEY_STATUS + "=?",
                        new String[]{String.valueOf(st)}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}
