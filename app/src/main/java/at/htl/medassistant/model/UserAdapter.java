package at.htl.medassistant.model;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.htl.medassistant.R;
import at.htl.medassistant.entity.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder>{

    private List<User> users = new ArrayList<>();
    private MedicineDatabaseHelper db;

    public UserAdapter(List<User> users, Context context) {
        this.users = users;
        db = MedicineDatabaseHelper.getInstance(context);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public MyViewHolder(final View view, final UserAdapter userAdapter) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.userName);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_row, parent, false);
        return new MyViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User user = users.get(position);
        long currentUserId = db.findCurrentUserId();

        if (currentUserId == user.getId()) {
            holder.name.setTextColor(Color.BLUE);
        } else {
            holder.name.setTextColor(Color.GRAY);
        }
        holder.name.setText(user.getLastName() + " "+user.getFirstName());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
