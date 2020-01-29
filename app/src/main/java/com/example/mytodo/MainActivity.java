package com.example.mytodo;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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


public class MainActivity extends AppCompatActivity {


    private Button  saveButton;
    private FloatingActionButton floatingActionButton;
    private EditText heading, details;
    private final ArrayList<String> head = new ArrayList<>();
    private final ArrayList<String> dsc = new ArrayList<>();
    private TaskAdapter taskAdapter = new TaskAdapter(head, dsc);
    private DataBaseHelper dataBaseHelper = new DataBaseHelper(this);

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
       floatingActionButton = findViewById(R.id.FloatingButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this, R.style.SheetDialog);
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_view);
                bottomSheetDialog.setCanceledOnTouchOutside(true);
                bottomSheetDialog.show();
                saveButton = bottomSheetDialog.findViewById(R.id.save_button);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long count = DataBaseHelper.getProfilesCount(dataBaseHelper.getReadableDatabase(), "TaskToBeDone");
                        int i = 0;
                        heading = bottomSheetDialog.findViewById(R.id.Task_Heading_Bottom_Sheet);
                        details = bottomSheetDialog.findViewById(R.id.Task_Details_Bottom_Sheet);
                        DataBaseHelper.writeData(dataBaseHelper.getWritableDatabase(), heading.getText().toString(), details.getText().toString());
                        while (count != -1) {
                            head.add(i, DataBaseHelper.getData(dataBaseHelper.getReadableDatabase(), i).get(0));
                            dsc.add(i, DataBaseHelper.getData(dataBaseHelper.getReadableDatabase(), i++).get(1));
                            count--;
                        }
                        taskAdapter.setHeading(head);
                        taskAdapter.setDetails(dsc);
                        taskRecyclerView.setAdapter(taskAdapter);
                        bottomSheetDialog.dismiss();
                    }
                });
            }
        });
    }
}
