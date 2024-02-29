package com.bdtx.mod_data.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bdtx.mod_data.Database.DaoUtils
import com.bdtx.mod_data.Database.Entity.Contact
import com.bdtx.mod_data.Database.Entity.Message

class CommunicationVM : BaseViewModel() {

    private val TAG = "CommunicationVM"
    private val contactList : MutableLiveData<MutableList<Contact>?> = MutableLiveData()  // 联系人列表
    private val messageList : MutableLiveData<MutableList<Message>?> = MutableLiveData()  // 消息列表

    // 获取联系人列表数据
    fun getContact() : LiveData<MutableList<Contact>?> {
        upDateContact()
        return contactList
    }
    fun upDateContact(){
        launchUIWithResult(
            responseBlock = {
                DaoUtils.getContacts()
            },
            successBlock = {
                it?.let { contacts ->
                    Log.e(TAG, "成功查询到: ${contacts.size} contact" )
                    contactList.value = contacts
                }
            }
        )
    }

    fun getMessage(number:String) : LiveData<MutableList<Message>?> {
        upDateMessage(number)
        return messageList
    }

    fun upDateMessage(number:String){
        launchUIWithResult(
            responseBlock = {
                DaoUtils.getMessages(number)
            },
            successBlock = {
                it?.let { messages ->
                    Log.e(TAG, "成功查询到: ${messages.size} messages" )
                    messageList.value = messages
                    // 清除未读消息数量
                    DaoUtils.getInstance().clearContactUnread(number)
                }
            }
        )
    }


}