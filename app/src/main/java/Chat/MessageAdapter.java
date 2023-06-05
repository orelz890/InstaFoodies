package Chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instafoodies.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import Home.HomeActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import models.User;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private DocumentReference usersDoc;


    public MessageAdapter(List<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;

    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView senderMessageText, receiverMessageText;
        public CircleImageView receiverProfileImage;
        public ImageView messageSenderPicture, messageReceiverPicture;
        public View view;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            senderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
        }
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_messages_layout, viewGroup, false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, final int position) {
        String messageSenderId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Messages messages = userMessagesList.get(position);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();


        usersDoc = FirebaseFirestore.getInstance().collection("users_account_settings").document(fromUserID);

        usersDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Error occurred while retrieving the document
                    return;
                }

                if (value != null && value.exists()) {
                    // Document exists, retrieve the value
                    String receiverImage = value.getString("profile_photo");
                    if (receiverImage != null && !receiverImage.isEmpty()){
                        Picasso.get().load(receiverImage).placeholder(R.drawable.profile_image).into(messageViewHolder.receiverProfileImage);
                    }
                    else{
                        messageViewHolder.receiverProfileImage.setImageResource(R.drawable.profile_image);
                    }


                } else {
                    // Document doesn't exist
                    messageViewHolder.receiverProfileImage.setImageResource(R.drawable.profile_image);
                }

            }
        });

        messageViewHolder.receiverMessageText.setVisibility(View.GONE);
        messageViewHolder.receiverProfileImage.setVisibility(View.GONE);
        messageViewHolder.senderMessageText.setVisibility(View.GONE);
        messageViewHolder.messageSenderPicture.setVisibility(View.GONE);
        messageViewHolder.messageReceiverPicture.setVisibility(View.GONE);


        switch (fromMessageType) {
            case "text":
                if (fromUserID.equals(messageSenderId)) {
                    messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);

                    messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                    messageViewHolder.senderMessageText.setTextColor(Color.BLACK);
                    messageViewHolder.senderMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());
                } else {
                    messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                    messageViewHolder.receiverMessageText.setVisibility(View.VISIBLE);

                    messageViewHolder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                    messageViewHolder.receiverMessageText.setTextColor(Color.BLACK);
                    messageViewHolder.receiverMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());
                }
                break;
            case "image":
                if (fromUserID.equals(messageSenderId)) {
                    messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                    Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageSenderPicture);

                } else {
                    messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                    messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
                    Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageReceiverPicture);
                }
                break;
            case "pdf":
            case "docx":
                Context context = messageViewHolder.view.getContext();
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.file);
                if (fromUserID.equals(messageSenderId)) {
                    messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                    messageViewHolder.messageSenderPicture.setBackground(drawable);

                    messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(messageViewHolder.getAdapterPosition()).getMessage()));
                            messageViewHolder.itemView.getContext().startActivity(intent);
                        }
                    });
                } else {
                    messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                    messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
                    messageViewHolder.messageReceiverPicture.setBackground(drawable);
                }
                break;
        }
        if (fromUserID.equals(messageSenderId)) {
            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userMessagesList.get(messageViewHolder.getAdapterPosition()).getType().equals("pdf") || userMessagesList.get(messageViewHolder.getAdapterPosition()).getType().equals("docx")) {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Delete For me",
                                        "Download and View this Document",
                                        "Cancel",
                                        "Delete For EveryOne"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                if (position == 0) {
                                   deleteSentMessage(messageViewHolder.getAdapterPosition(),messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), HomeActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);

                                } else if (position == 1) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(messageViewHolder.getAdapterPosition()).getMessage()));
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                } else if (position == 3) {
                                     deleteMessageForEveryOne(messageViewHolder.getAdapterPosition(),messageViewHolder);
                                }
                            }
                        });
                        builder.show();
                    } else if (userMessagesList.get(messageViewHolder.getAdapterPosition()).getType().equals("text")) {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Delete For me",
                                        "Cancel",
                                        "Delete For EveryOne"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                if (position == 0) {
                                    deleteSentMessage(messageViewHolder.getAdapterPosition(),messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(),HomeActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);

                                }else if (position == 2) {
                                    deleteMessageForEveryOne(messageViewHolder.getAdapterPosition(),messageViewHolder);
                                }
                            }
                        });
                        builder.show();
                    } else if (userMessagesList.get(messageViewHolder.getAdapterPosition()).getType().equals("image")) {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Delete For me",
                                        "View This Image",
                                        "Cancel",
                                        "Delete For EveryOne"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                if (position == 0) {
                                  deleteSentMessage(messageViewHolder.getAdapterPosition(),messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(),HomeActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);

                                } else if (position == 1) {
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(),ImageViewerActivity.class);
                                    intent.putExtra("url",userMessagesList.get(messageViewHolder.getAdapterPosition()).getMessage());
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                } else if (position == 3) {
                                    deleteMessageForEveryOne(messageViewHolder.getAdapterPosition(),messageViewHolder);
                                }
                            }
                        });
                        builder.show();
                    }
                }
            });
        } else {
            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userMessagesList.get(messageViewHolder.getAdapterPosition()).getType().equals("pdf") || userMessagesList.get(messageViewHolder.getAdapterPosition()).getType().equals("docx")) {
                        CharSequence[] options = new CharSequence[]
                                {
                                        "Delete For me",
                                        "Download and View this Document",
                                        "Cancel"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                if (position == 0) {
                                   deleteReceiverMessage(messageViewHolder.getAdapterPosition(),messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(),HomeActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);

                                } else if (position == 1) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(messageViewHolder.getAdapterPosition()).getMessage()));
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    } else if (userMessagesList.get(messageViewHolder.getAdapterPosition()).getType().equals("text")) {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Delete For me",
                                        "Cancel"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                if (position == 0) {
                                    deleteReceiverMessage(messageViewHolder.getAdapterPosition(),messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(),HomeActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    } else if (userMessagesList.get(messageViewHolder.getAdapterPosition()).getType().equals("image")) {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Delete For me",
                                        "View This Image",
                                        "Cancel"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Delete Message");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int position) {
                                if (position == 0) {
                                    deleteReceiverMessage(messageViewHolder.getAdapterPosition(),messageViewHolder);
                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(),HomeActivity.class);
                                    messageViewHolder.itemView.getContext().startActivity(intent);

                                } else if (position == 1) {
                                   Intent intent = new Intent(messageViewHolder.itemView.getContext(),ImageViewerActivity.class);
                                    intent.putExtra("url",userMessagesList.get(messageViewHolder.getAdapterPosition()).getMessage());
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                            }
                        });
                        builder.show();
                    }
                }
            });

        }
    }


    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    private void deleteSentMessage(final int position, final MessageViewHolder holder)
    {
      DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
      rootRef.child("Messages")
              .child(userMessagesList.get(position).getFrom())
              .child(userMessagesList.get(position).getTo())
              .child(userMessagesList.get(position).getMessageID())
              .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task)
          {
               if(task.isSuccessful())
               {
                   Toast.makeText(holder.itemView.getContext(),"Deleted Successfully.",Toast.LENGTH_SHORT).show();
               }
               else
               {
                   Toast.makeText(holder.itemView.getContext(),"Error Occurred.",Toast.LENGTH_SHORT).show();
               }
          }
      });

    }

    private void deleteReceiverMessage(final int position, final MessageViewHolder holder)
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(holder.itemView.getContext(),"Deleted Successfully.",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(holder.itemView.getContext(),"Error Occurred.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void deleteMessageForEveryOne(final int position, final MessageViewHolder holder)
    {
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    rootRef.child("Messages")
                            .child(userMessagesList.get(position).getFrom())
                            .child(userMessagesList.get(position).getTo())
                            .child(userMessagesList.get(position).getMessageID())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                           if(task.isSuccessful())
                           {
                               Toast.makeText(holder.itemView.getContext(),"Error Occurred.",Toast.LENGTH_SHORT).show();
                           }
                        }
                    });
                }
                else
                {
                    Toast.makeText(holder.itemView.getContext(),"Error Occurred.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
