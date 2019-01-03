package com.palibre.mysqlsync;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactVH> {
    private ArrayList<Contact> contactList = new ArrayList<Contact>();

    ContactsAdapter(ArrayList<Contact> displayList){
        contactList = displayList;
    }
    @NonNull
    @Override
    public ContactVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.name_item_view, parent, false);
        return new ContactVH(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactVH holder, int position) {
        holder.name.setText(contactList.get(position).getName());
        int syncStatus = contactList.get(position).getSyncStatus();
        if (syncStatus == DbContract.SYNC_STATUS_FAILED) {
            holder.syncStatus.setImageResource(R.drawable.ic_sync_needed);
        }
        else {
            holder.syncStatus.setImageResource(R.drawable.ic_sync_needed);
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ContactVH extends RecyclerView.ViewHolder {
        ImageView syncStatus;
        TextView name;
        public ContactVH(View itemView)
        {
            super(itemView);
            syncStatus = itemView.findViewById(R.id.imgSync);
            name = itemView.findViewById(R.id.txtName);
        }
    }


}
