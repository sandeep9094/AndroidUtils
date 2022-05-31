
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

/**
 * Created on 22-10-2021.
 *
 * @author Sandeep Kumar (https://github.com/sandeep9094)
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<User> userArrayList = new ArrayList<>();
    private final RecyclerViewAdapter.ItemClickListener listener;

    public RecyclerViewAdapter(ArrayList<User> list, RecyclerViewAdapter.ItemClickListener listener) {
        userArrayList = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_user_type, parent, false);
        return new RecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(userArrayList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }


    interface ItemClickListener {
        void userSelected(User user);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        //Get all ui components
        private final TextView nameTextView = itemView.findViewById(R.id.user_name);
        private final TextView ageTextView = itemView.findViewById(R.id.user_age);
        private final LinearLayout rootLayout = itemView.findViewById(R.id.root_layout);

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        //Update ui components with actual data
        public void bindView(User user, RecyclerViewAdapter.ItemClickListener listener) {

            nameTextView.setText(user.getName());
            ageTextView.setText(user.getAge());

            rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.userSelected(user);
                }
            });
        }
    }

}
