package com.example.health_bracer;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> IllnessNames = null;
    private ArrayList<String> IllnessDescriptions = null;
    private ArrayList<String> IllnessSupports = null;

    TextView IllnessName;
    TextView IllnessDescription;
    TextView IllnessSupport;

    View ExtraView;

    private Context context;

    public RecyclerViewAdapter(ArrayList<String> IllnessNames, ArrayList<String> IllnessDescriptions, ArrayList<String> IllnessSupports,
                               View Extra, Context context) {
        this.IllnessNames = IllnessNames;
        this.IllnessDescriptions = IllnessDescriptions;
        this.IllnessSupports = IllnessSupports;
        this.context = context;
        this.ExtraView = Extra;
        this.IllnessName = ExtraView.findViewById(R.id.illnessName);
        this.IllnessDescription = ExtraView.findViewById(R.id.illnessDescription);
        this.IllnessSupport = ExtraView.findViewById(R.id.illnessSupport);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        Log.d(TAG, "onBindViewHolder: called.");

        holder.illnessName.setText(IllnessNames.get(position));

        holder.illnessName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, IllnessExtra.class);
                intent.putExtra("name", IllnessNames.get(position));
                intent.putExtra("desc", IllnessDescriptions.get(position));
                intent.putExtra("supp", IllnessSupports.get(position));
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.IllnessNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView illnessName;
        ConstraintLayout magnifyLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.illnessName = itemView.findViewById(R.id.illnessText);
            this.magnifyLayout = itemView.findViewById(R.id.magnify_layout);
        }
    }
}
