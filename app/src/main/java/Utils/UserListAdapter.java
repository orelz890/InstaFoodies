package Utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.instafoodies.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleBiFunction;

import de.hdodenhof.circleimageview.CircleImageView;
import models.User;
import models.UserAccountSettings;
import models.UserSettings;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListAdapter extends ArrayAdapter<UserSettings> {

    private static final String TAG = "UserListAdapter";
    private LayoutInflater mInflater;
    private List<UserSettings> mUsers = null;
    private int layoutResource;
    private Context mContext;
    private FirebaseFirestore db;


    public UserListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<UserSettings> objects) {
        super(context, resource, objects);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mUsers = objects;
        db = FirebaseFirestore.getInstance();
    }

    private static class ViewHolder {
        TextView username, email;
        CircleImageView profileImage;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            holder.username = (TextView) convertView.findViewById(R.id.search_username);
            holder.email = (TextView) convertView.findViewById(R.id.emailInSearch);
            holder.profileImage = (CircleImageView) convertView.findViewById(R.id.profile_imageInSearch);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        UserSettings userSettings = getItem(position);
        if (userSettings != null) {
            holder.username.setText(userSettings.getUser().getUsername());
            holder.email.setText(userSettings.getUser().getEmail());
            Glide.with(mContext)
                    .load(userSettings.getSettings().getProfile_photo())
                    .placeholder(R.drawable.ic_android)
                    .error(R.drawable.ic_android)
                    .fitCenter()
                    .into(holder.profileImage);
        }

        return convertView;
    }

}