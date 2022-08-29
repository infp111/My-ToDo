package net.example.todokk.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import net.example.todokk.Model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DBName = "toDoTask.db";

    private static final String DATABASE_NAME = Environment.getExternalStorageDirectory().toString() +DBName;
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String colTask = "Task";
    private static final String colDescription = "Description";
    private static final String colDate = "Date";
    private static final String colTime = "Time";
    private static final String colEndDate = "EndDate";

    private static final String colPriority = "Priority";

    private static final String STATUS = "status";
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            colTask + " TEXT, " +
            colDescription + " TEXT, " +
            colDate + " TEXT, " +
            colTime + " TEXT, "  +
            colEndDate + " TEXT, "  +
            colPriority + " INTEGER, "
            + STATUS + " INTEGER)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, DBName, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        // Create tables again
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertTask(ToDoModel task){
        ContentValues cv = new ContentValues();
        cv.put(colTask, task.getTaskTitle());
        cv.put(colDescription, task.getTaskDescription());
        cv.put(colDate, task.getTaskDate());
        cv.put(colTime, task.getTaskTime());
        cv.put(colPriority, task.getTaskPriority());
        cv.put(colEndDate, task.getTaskEndDate());
        cv.put(STATUS, 0);
        db.insert(TODO_TABLE, null, cv);
    }

    public List<ToDoModel> getAllTasks(String sortType){
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try{
            cur =sortTaskBy(sortType);
//            cur = db.rawQuery("SELECT *FROM todo ORDER by Priority DESC", null);

            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        ToDoModel task = new ToDoModel();
                        task.setId(cur.getInt(cur.getColumnIndex(ID)));
                        task.setTaskTitle(cur.getString(cur.getColumnIndex(colTask)));
                        task.setTaskDescription(cur.getString(cur.getColumnIndex(colDescription)));
                        task.setTaskDate(cur.getString(cur.getColumnIndex(colDate)));
                        task.setTaskTime(cur.getString(cur.getColumnIndex(colTime)));
                        task.setTaskPriority(cur.getInt(cur.getColumnIndex(colPriority)));
                        task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                        task.setTaskEndDate(cur.getString(cur.getColumnIndex(colEndDate)));

                        taskList.add(task);
                    }
                    while(cur.moveToNext());
                }
            }
        }
        finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return taskList;
    }

    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateTask(int id, ToDoModel task) {
        ContentValues cv = new ContentValues();
        cv.put(colTask, task.getTaskTitle());
        cv.put(colDescription, task.getTaskDescription());
        cv.put(colDate, task.getTaskDate());
        cv.put(colTime, task.getTaskTime());
        cv.put(colPriority, task.getTaskPriority());
        cv.put(colEndDate, task.getTaskEndDate());

        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void deleteTask(int id){
        db.delete(TODO_TABLE, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public Cursor sortTaskBy(String str){
        Cursor cur = null;

        if(str.equals("Name"))
            cur = db.query(TODO_TABLE, null, null, null, null, null, colTask , null);
        else  if(str.equals("Date"))
            cur = db.query(TODO_TABLE, null, null, null, null, null, colDate, null);
        else  if(str.equals("Priority"))
            cur = db.query(TODO_TABLE, null, null, null, null, null, colPriority, null);
        else
            cur = db.query(TODO_TABLE, null, null, null, null, null, null, null);

        return  cur;
    }
}
