package net.example.todokk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.example.todokk.Model.ToDoModel;
import net.example.todokk.Utils.DatabaseHandler;

import java.util.Calendar;
import java.util.Objects;

import static android.content.Context.ALARM_SERVICE;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";
    EditText addTaskTitle;
    EditText addTaskDescription;
    EditText taskDate ,taskEndDate;
    EditText taskTime;
    RadioGroup rgPriority;
    RadioButton rbHigh, rbMid, rbLow;
    int taskPriority = 4;
    Button btnAddTask;
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    int mYear, mMonth, mDay;
    int mHour, mMinute;
    Bundle bundle = null;
    private DatabaseHandler db;
    private ToDoModel task;

    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addTaskTitle = Objects.requireNonNull(getView()).findViewById(R.id.addTaskTitle);
        addTaskDescription = Objects.requireNonNull(getView()).findViewById(R.id.addTaskDescription);
        taskDate = getView().findViewById(R.id.taskDate);
        taskEndDate = getView().findViewById(R.id.taskEndDate);
        taskTime = getView().findViewById(R.id.taskTime);
        rgPriority = Objects.requireNonNull(getView()).findViewById(R.id.rgPriority);
        btnAddTask = getView().findViewById(R.id.addTask);
        rbHigh = getView().findViewById(R.id.rbPrioHigh);
        rbMid = getView().findViewById(R.id.rbPrioMid);
        rbLow = getView().findViewById(R.id.rbPrioLow);

        boolean isUpdate = false;
        bundle = getArguments();

        // After Swiping the item for edit, will get the item id in the bundle
        if (bundle != null) {
            isUpdate = true;
            EditTodoTasks(bundle);
            String task = bundle.getString("task"); // extracting bundle

            assert task != null;
            if (task.length() > 0)
                btnAddTask.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorPrimaryDark));
            btnAddTask.setText("Update Task");
        }else
        {
            btnAddTask.setText("Add Task");

        }


        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        selectPriority();

        setTaskTImeDialog();


        // On title Change
        addTaskTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    btnAddTask.setEnabled(false);
                    btnAddTask.setTextColor(Color.GRAY);
                } else {
                    btnAddTask.setEnabled(true);
                    btnAddTask.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorPrimaryDark));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        final boolean finalIsUpdate = isUpdate;

        // Om Button CLick
        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task = new ToDoModel();
                setTaskData();

                if (finalIsUpdate) {
                    db.updateTask(bundle.getInt("id"), task);
                    dismiss();

                } else {
                    if (validateFields()) {
                        task = new ToDoModel();
                        setTaskData();
                        db.insertTask(task);
                        dismiss();

                    } else {
                        Toast.makeText(getActivity(), "No Data", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void setTaskData() {
        task.setTaskTitle(addTaskTitle.getText().toString());
        task.setTaskDescription(addTaskDescription.getText().toString());
        task.setTaskDate(taskDate.getText().toString());
        task.setTaskTime(taskTime.getText().toString());
        task.setTaskEndDate(taskEndDate.getText().toString());
        task.setTaskPriority(taskPriority);
        task.setStatus(0);
    }


    private void setTaskTImeDialog() {

        taskDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                    datePickerDialog = new DatePickerDialog(AddNewTask.this.getActivity(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view1, int year, int monthOfYear, int dayOfMonth) {
                                    taskDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                    datePickerDialog.dismiss();
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.show();
                }
                return true;
            }
        });

        taskEndDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                    datePickerDialog = new DatePickerDialog(AddNewTask.this.getActivity(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view1, int year, int monthOfYear, int dayOfMonth) {
                                    taskEndDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                    datePickerDialog.dismiss();
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.show();
                }
                return true;
            }
        });

        taskTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    // Get Current Time
                    final Calendar c = Calendar.getInstance();
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mMinute = c.get(Calendar.MINUTE);

                    // Launch Time Picker Dialog
                    timePickerDialog = new TimePickerDialog(AddNewTask.this.getActivity(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view12, int hourOfDay, int minute) {
                                    taskTime.setText(hourOfDay + ":" + minute);
                                    timePickerDialog.dismiss();
                                }
                            }, mHour, mMinute, false);
                    timePickerDialog.show();
                }
                return true;
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        Activity activity = getActivity();
        if (activity instanceof DialogCloseListener)
            ((DialogCloseListener) activity).handleDialogClose(dialog);
    }

    private void selectPriority() {

        // This overrides the radiogroup onCheckListener
        rgPriority.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                String btnValue = checkedRadioButton.getText().toString();
                if (isChecked) {
                    if (btnValue.equals("High"))
                        taskPriority = 1;
                    else if (btnValue.equals("Medium"))
                        taskPriority = 2;
                    else if (btnValue.equals("Low"))
                        taskPriority = 3;

                    // Changes the textview's text to "Checked: example radiobutton text"
//                    Toast.makeText(getActivity(), checkedRadioButton.getText(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public boolean validateFields() {
        if (addTaskTitle.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(getActivity(), "Please enter a valid title", Toast.LENGTH_SHORT).show();
            return false;
        } else if (addTaskDescription.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(getActivity(), "Please enter a valid description", Toast.LENGTH_SHORT).show();
            return false;
        } else if (taskDate.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(getActivity(), "Please enter date", Toast.LENGTH_SHORT).show();
            return false;
        } else if (taskTime.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(getActivity(), "Please enter time", Toast.LENGTH_SHORT).show();
            return false;
        } else if (rgPriority.getCheckedRadioButtonId() == 0) {
            Toast.makeText(getActivity(), "Please enter an event", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    private void EditTodoTasks(Bundle bundle) {

        TextView tvHead = getView().findViewById(R.id.tvHeading);
        TextView tvHeadInfo = getView().findViewById(R.id.tvHeadInfo);
        tvHead.setText("Edit Task");
        tvHeadInfo.setText("Update the todo details ");


        String task = bundle.getString("task"); // extracting bundle
        addTaskTitle.setText(task);
        addTaskDescription.setText(bundle.getString("desc"));
        taskDate.setText(bundle.getString("date"));
        taskTime.setText(bundle.getString("time"));
        taskEndDate.setText(bundle.getString("endate"));
//            taskTime.setText(bundle.getString("task"));
        int prio = bundle.getInt("prio");

//        rgPriority.clearCheck();
        switch (prio) {
            case 1:
                rbHigh.setChecked(true);
                break;
            case 2:
                rbMid.setChecked(true);
                break;
            case 3:
                rbLow.setChecked(true);
                break;

        }

    }


}
