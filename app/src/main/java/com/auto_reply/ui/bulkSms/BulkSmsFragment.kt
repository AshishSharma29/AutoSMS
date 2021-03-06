package com.auto_reply.ui.bulkSms

import android.content.ContentResolver
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.auto_reply.base.BaseFragment
import com.auto_reply.calling.CallReceiver
import com.auto_reply.databinding.FragmentBulkSmsBinding
import com.auto_reply.model.LoginResponseModel
import com.auto_reply.util.Prefs


class BulkSmsFragment : BaseFragment(), IContactCheckListner {
    lateinit var mBinding: FragmentBulkSmsBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentBulkSmsBinding.inflate(inflater, container, false)
        mActivity.showProgress()
        return mBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginResponseModel = Prefs.getObjectFromPref<LoginResponseModel>(
            mActivity,
            LoginResponseModel::class.java.name
        )
        Handler(Looper.getMainLooper()).post(Runnable {
            getAllContacts()
        })
        mBinding.cbSelectAll.setOnCheckedChangeListener { p0, p1 ->
            mBinding.edSearch.setText("")
            val temp: MutableList<ContactVO> = ArrayList()
            for (d in contactVOList) {
                d.isChecked = p1
                temp.add(d)
            }
            contactAdapter.updateCheck(temp)
        }
        mBinding.edSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                filter(s.toString())
            }
        })

        mBinding.btnSend.setOnClickListener {
            if (mBinding.edMessage.text.toString().trim().isEmpty()) {
                mActivity.showToast("Please enter message")
            } else
                for (contact in contactVOList) {
                    if (contact.isChecked)
                        CallReceiver.sendSMS(
                            contact.contactNumber,
                            mBinding.edMessage.text.toString(),
                            mActivity,
                            loginResponseModel.responsePacket.SLOT_ID
                        )
                }
        }
    }

    fun filter(text: String) {
        val temp: MutableList<ContactVO> = ArrayList()
        for (d in contactVOList) {
            if (d.contactName.toLowerCase().contains(text.toLowerCase())) {
                temp.add(d)
            }
        }
        contactAdapter.updateCheck(temp)
    }

    var contactVOList: MutableList<ContactVO> = ArrayList()
    private fun getAllContacts() {
        var contactVO: ContactVO
        val contentResolver: ContentResolver = mActivity.getContentResolver()
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        if (cursor?.getCount()!! > 0) {
            while (cursor.moveToNext()) {
                val hasPhoneNumber: Int =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                        .toInt()
                if (hasPhoneNumber > 0) {
                    val id: String =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val name: String =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    contactVO = ContactVO()
                    contactVO.contactName = name
                    val phoneCursor: Cursor? = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id),
                        null
                    )
                    if (phoneCursor?.moveToNext()!!) {
                        val phoneNumber: String =
                            phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        contactVO.contactNumber = phoneNumber
                    }
                    phoneCursor.close()
                    val emailCursor: Cursor? = contentResolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    while (emailCursor?.moveToNext()!!) {
                        val emailId: String =
                            emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                    }
                    contactVOList.add(contactVO)
                }
            }
            mActivity.hideProgress()
            contactAdapter = AllContactsAdapter(
                contactVOList,
                mActivity, this
            )
            mBinding.rvContacts.setLayoutManager(LinearLayoutManager(mActivity))
            mBinding.rvContacts.setAdapter(contactAdapter)
        }
    }

    lateinit var contactAdapter: AllContactsAdapter
    override fun onCheckChange(position: Int) {
        val contactVO = contactVOList[position]
        contactVO.isChecked = !contactVO.isChecked
        contactVOList.set(position, contactVO)
        contactAdapter.updateCheck(contactVOList)
    }
}