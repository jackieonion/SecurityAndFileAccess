package com.example.security_and_file_access;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;

public class RegisterAdapter extends RecyclerView.Adapter<RegisterAdapter.ViewHolder> {
    private List<RegisterModel> registerMOdelsList;

    public RegisterAdapter(List<RegisterModel> userModelList) {
        this.registerMOdelsList = userModelList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.register_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String id = registerMOdelsList.get(position).id;
        String password = registerMOdelsList.get(position).password;
        String encodedPassword = registerMOdelsList.get(position).encodedPassword;
        String date = registerMOdelsList.get(position).date;
        holder.password.setText(password);
        holder.id.setText(id);
        holder.date.setText(date);
        holder.encodedPassword.setText(encodedPassword);
    }

    @Override
    public int getItemCount() {
        return registerMOdelsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView password;
        private TextView id;
        private TextView date;
        private TextView encodedPassword;

        public ViewHolder(View v) {
            super(v);
            id = v.findViewById(R.id.textView4);
            password = v.findViewById(R.id.textView5);
            encodedPassword = v.findViewById(R.id.textView6);
            date = v.findViewById(R.id.textView7);
        }
    }
}
