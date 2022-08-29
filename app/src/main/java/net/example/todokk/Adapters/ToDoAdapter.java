package net.example.todokk.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.example.todokk.AddNewTask;
import net.example.todokk.MainActivity;
import net.example.todokk.Model.ToDoModel;
import net.example.todokk.R;
import net.example.todokk.Utils.DatabaseHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoModel> todoList;
    private DatabaseHandler db;
    private MainActivity activity;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd MMM yyyy", Locale.US);
    public SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-M-yyyy", Locale.US);
    Date date = null;
    String outputDateString = null;
    public ToDoAdapter(DatabaseHandler db, MainActivity activity) {
        this.db = db;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        db.openDatabase();
        final  ToDoModel task = todoList.get(position);
//        final ToDoModel item = todoList.get(position);
        holder.title.setText(task.getTaskTitle());
        holder.description.setText(task.getTaskDescription());
        holder.time.setText("Time : "+ task.getTaskTime());
        holder.endDate.setText( "End Date : "+task.getTaskEndDate());

        int status = task.getStatus();
        int prio = task.getTaskPriority();
        String strPrio = "";
        String strStatus = "UPCOMING";

        if(prio == 1){
            holder.priority.setTextColor(Color.RED);
            strPrio = "High";
        }
        else if(prio == 2){
            holder.priority.setTextColor(getContext().getColor( R.color.orange));
            strPrio = "Medium";
        }
        else if(prio == 3){
            holder.priority.setTextColor(getContext().getColor( R.color.colorPrimaryDark));
            strPrio = "Low";
        }


        if(status == 1)
            strStatus = "COMPLETED";
        holder.status.setText(strStatus);
        holder.priority.setText(strPrio);

        try {
            date = inputDateFormat.parse(task.getTaskDate());
            outputDateString = dateFormat.format(date);

            String[] items1 = outputDateString.split(" ");
            String day = items1[0];
            String dd = items1[1];
            String month = items1[2];

            holder.day.setText(day);
            holder.date.setText(dd);
            holder.month.setText(month);


        } catch (Exception e) {
            e.printStackTrace();
        }

//        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    db.updateStatus(task.getId(), 1);
//                } else {
//                    db.updateStatus(task.getId(), 0);
//                }
//            }
//        });
    }

    private boolean toBoolean(int n) {
        return n != 0;
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public Context getContext() {
        return activity;
    }

    public void setTasks(List<ToDoModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        ToDoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTaskTitle());
        bundle.putString("desc", item.getTaskDescription());
        bundle.putString("date", item.getTaskDate());
        bundle.putString("time", item.getTaskTime());
        bundle.putString("endate", item.getTaskEndDate());
        bundle.putInt("prio", item.getTaskPriority());

        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView day;
        TextView date;
        TextView month;
        TextView title;
        TextView description;
        TextView status;
        TextView priority;
        TextView endDate;
        ImageView options;
        TextView time;

        ViewHolder(View view) {
            super(view);
//            task = view.findViewById(R.id.todoCheckBox);
            day = view.findViewById(R.id.day);
            date = view.findViewById(R.id.date);
            month = view.findViewById(R.id.month);
            title = view.findViewById(R.id.title);
            description = view.findViewById(R.id.description);
            status = view.findViewById(R.id.status);
//            task = view.findViewById(R.id.options);
            priority = view.findViewById(R.id.priority);
            time = view.findViewById(R.id.time);
            endDate = view.findViewById(R.id.endDate);

        }
    }
}
