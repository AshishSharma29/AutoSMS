package com.auto_reply.ui.bulkSms;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.auto_reply.R;
import com.auto_reply.databinding.SingleContactViewBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AllContactsAdapter extends RecyclerView.Adapter<AllContactsAdapter.ContactViewHolder> {

    private static List<ContactVO> contactVOList;
    private Context mContext;
    private IContactCheckListner iContactCheckListner;

    public AllContactsAdapter(List<ContactVO> contactVOList, Context mContext, IContactCheckListner iContactCheckListner) {
        this.contactVOList = contactVOList;
        this.mContext = mContext;
        this.iContactCheckListner = iContactCheckListner;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_contact_view, null);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        holder.mBinding.setContactVO(contactVOList.get(position));
        holder.mBinding.cbChecked.setOnCheckedChangeListener(null);
        holder.mBinding.cbChecked.setChecked(contactVOList.get(position).isChecked());
        holder.mBinding.cbChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                iContactCheckListner.onCheckChange(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactVOList.size();
    }

    public void updateCheck(@NotNull List<ContactVO> contactVOList) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                AllContactsAdapter.contactVOList = contactVOList;
                notifyDataSetChanged();
            }
        });

    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        SingleContactViewBinding mBinding;

        public ContactViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }
    }
}