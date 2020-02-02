package com.example.mytodo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private ArrayList<String> heading;
    private ArrayList<String> details;

    public void setDateofEachTask(ArrayList<String> dateofEachTask) {
        this.dateofEachTask = dateofEachTask;
    }

    private ArrayList<String> dateofEachTask;


    public void setHeading(ArrayList<String> heading) {
        this.heading = heading;
    }


    public void setDetails(ArrayList<String> details) {
        this.details = details;
    }

    public TaskAdapter(ArrayList<String> heading, ArrayList<String> details, ArrayList<String> dateofEachTask) {
        this.dateofEachTask = dateofEachTask;
        this.heading = heading;
        this.details = details;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_layout, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        String title = heading.get(position);
        String desc = details.get(position);
        String dates = dateofEachTask.get(position);
        holder.headingOfTask.setText(title);
        holder.detailTask.setText(desc);
        holder.dateForTask.setText(dates);
    }

    @Override
    public int getItemCount() {
        return heading.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView headingOfTask;
        TextView detailTask;
        TextView dateForTask;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            headingOfTask = itemView.findViewById(R.id.HeadingOfTask);
            detailTask = itemView.findViewById(R.id.DetailofTask);
            dateForTask = itemView.findViewById(R.id.date_for_task);
        }
    }
}
