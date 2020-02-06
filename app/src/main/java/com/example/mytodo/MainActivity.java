package com.example.mytodo;

import android.app.DatePickerDialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {


    private Button saveButton, calentime_button;
    private FloatingActionButton floatingActionButton;
    private EditText heading, details;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private final ArrayList<String> head = new ArrayList<>();
    private final ArrayList<String> dsc = new ArrayList<>();
    private final ArrayList<String> datesofeachTask = new ArrayList<>();
    private TaskAdapter taskAdapter = new TaskAdapter(head, dsc, datesofeachTask);
    private DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RecyclerView taskRecyclerView = findViewById(R.id.taskListRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        final int position = viewHolder.getAdapterPosition();
                        final String deletedHeading = head.get(position);
                        final String deletedDetail = dsc.get(position);
                        switch (direction) {
                            case ItemTouchHelper.RIGHT:
                                head.remove(viewHolder.getAdapterPosition());
                                dsc.remove(viewHolder.getAdapterPosition());
                                DataBaseHelper.delete(dataBaseHelper.getWritableDatabase(), deletedHeading);
                                taskAdapter.notifyDataSetChanged();
                                Snackbar.make(taskRecyclerView, "Click Undo To Get It Back!", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        head.add(position, deletedHeading);
                                        dsc.add(position, deletedDetail);
                                        taskAdapter.notifyDataSetChanged();
                                    }
                                }).show();
                        }
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                            float dX, float dY, int actionState, boolean isCurrentlyActive) {


                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                };
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(taskRecyclerView);
        loadAtFirstTheTask(taskRecyclerView);
        floatingActionButton = findViewById(R.id.FloatingButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this, R.style.SheetDialog);
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_view);
                bottomSheetDialog.setCanceledOnTouchOutside(true);
                calentime_button = bottomSheetDialog.findViewById(R.id.calender_button);
                calender();
                bottomSheetDialog.show();
                saveButton = bottomSheetDialog.findViewById(R.id.save_button);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadTheTasks(bottomSheetDialog, taskRecyclerView);
                    }
                });
            }
        });
    }

    private void loadAtFirstTheTask(RecyclerView taskRecyclerView) {
        long count = DataBaseHelper.getProfilesCount(dataBaseHelper.getReadableDatabase(), "TaskToBeDone");
        int i = 0;
        head.clear();
        dsc.clear();
        while (count != 0) {
            head.add(i, DataBaseHelper.getData(dataBaseHelper.getReadableDatabase(), i).get(0));
            dsc.add(i, DataBaseHelper.getData(dataBaseHelper.getReadableDatabase(), i).get(1));
            datesofeachTask.add(i, DataBaseHelper.getData(dataBaseHelper.getReadableDatabase(), i++).get(2));
            count--;
        }
        taskAdapter.setHeading(head);
        taskAdapter.setDetails(dsc);
        taskAdapter.setDateofEachTask(datesofeachTask);
        taskRecyclerView.setAdapter(taskAdapter);
    }

    private void calender() {
        calentime_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DATE);
                calentime_button.setVisibility(View.GONE);
                DatePickerDialog dialog = new DatePickerDialog(
                        MainActivity.this,
                        R.style.my_dialog_theme, mDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                date = (day + "/" + (month + 1) + "/" + year);
                calentime_button.setText(date);
                calentime_button.setVisibility(View.VISIBLE);
            }
        };

    }

    private void loadTheTasks(BottomSheetDialog bottomSheetDialog, RecyclerView taskRecyclerView) {
        long count = DataBaseHelper.getProfilesCount(dataBaseHelper.getReadableDatabase(), "TaskToBeDone");
        int i = 0;
        head.clear();
        dsc.clear();
        heading = bottomSheetDialog.findViewById(R.id.Task_Heading_Bottom_Sheet);
        details = bottomSheetDialog.findViewById(R.id.Task_Details_Bottom_Sheet);
        if (heading.getText().toString().equals("") == false && details.getText().toString().equals("") == false) {
            DataBaseHelper.writeData(dataBaseHelper.getWritableDatabase(), heading.getText().toString(), details.getText().toString(), date);
            while (count != -1) {
                head.add(i, DataBaseHelper.getData(dataBaseHelper.getReadableDatabase(), i).get(0));
                dsc.add(i, DataBaseHelper.getData(dataBaseHelper.getReadableDatabase(), i).get(1));
                datesofeachTask.add(i, DataBaseHelper.getData(dataBaseHelper.getReadableDatabase(), i++).get(2));
                count--;
            }
            taskAdapter.setHeading(head);
            taskAdapter.setDetails(dsc);
            taskAdapter.setDateofEachTask(datesofeachTask);
            taskRecyclerView.setAdapter(taskAdapter);
            bottomSheetDialog.dismiss();
        } else {
            Drawable customErrorDrawable = getResources().getDrawable(R.drawable.ic_error_black_24dp);
            customErrorDrawable.setBounds(0, 0, customErrorDrawable.getIntrinsicWidth(), customErrorDrawable.getIntrinsicHeight());
            if (heading.getText().toString().equals("") && details.getText().toString().equals("")) {
                heading.setError("Enter The Heading For Your Task", customErrorDrawable);
                details.setError("Enter The Details For Your Task", customErrorDrawable);
            } else if (heading.getText().toString().equals(""))
                heading.setError("Enter The Heading For Your Task", customErrorDrawable);
            else
                details.setError("Enter The Details For Your Task", customErrorDrawable);

        }
    }
}
