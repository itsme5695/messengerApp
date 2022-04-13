package com.example.final_gram

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.final_gram.adapters.ChatAdapter
import com.example.final_gram.databinding.FragmentChatBinding
import com.example.final_gram.models.Group
import com.example.final_gram.models.Message
import com.example.final_gram.models.User
import com.example.final_gram.retrofit.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private val TAG = "ChatFragment"
    lateinit var binding: FragmentChatBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var chatAdapter: ChatAdapter
    lateinit var apiService: ApiService
    lateinit var referenceUsers: DatabaseReference
    lateinit var userDataList: ArrayList<User>
    lateinit var tokens: ArrayList<User>
    lateinit var userList: ArrayList<User>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(layoutInflater, container, false)
        val groupcha = arguments?.getSerializable("chat") as Group
        apiService =
            ApiClient.getRetrofit("https://fcm.googleapis.com/").create(ApiService::class.java)


        binding.namee.text = groupcha.gr_name
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }


        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("groups")
        referenceUsers = firebaseDatabase.getReference("users")

        userList = ArrayList()
        userDataList = ArrayList()
        tokens = ArrayList()
        groupUsers()


        var rasm = firebaseAuth.currentUser?.photoUrl

        binding.sendBtn.setOnClickListener {
            val m = binding.sms.text.toString().trim()
            val simpleDateFormat = SimpleDateFormat("HH.mm")
            val date = simpleDateFormat.format(Date())
            val message = Message(
                m,
                date,
                firebaseAuth.currentUser?.uid,
                firebaseAuth.currentUser?.displayName,
                rasm.toString()
            )

            if (m.isNotEmpty()) {
                for (i in 0 until groupcha.users!!.size) {
                    val key = reference.push().key
                    reference.child("${groupcha.gr_name!!}/users/$i/messages/$key")
                        .setValue(message)
                }
            }

            binding.sms.setText("")
            sendMessage(m, userList, tokens)

        }


        for (i in 0 until groupcha.users!!.size) {

//            if (firebaseAuth.currentUser?.uid == groupcha.users!![i].uid) {
            reference.child("${groupcha.gr_name}/users/$i/messages")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list = ArrayList<Message>()
                        val children = snapshot.children
                        for (child in children) {
                            val value = child.getValue(Message::class.java)
                            if (value != null) {
                                list.add(value)
                            }
                        }

                        chatAdapter = ChatAdapter(list, groupcha.users!![i].uid!!)
                        binding.chatmessageRv.adapter = chatAdapter
                        binding.chatmessageRv.scrollToPosition(list.size - 1)

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }


/*        binding.sendBtn.setOnClickListener {
            val m = binding.sms.text.toString()
            key = reference.push().key
            val chat = Chat(m,firebaseAuth.currentUser!!.uid,groupcha.gr_name)


            reference.child("${groupcha.gr_name}/$key")
                .setValue(chat)

//            reference.child("${usercha.uid}/messages/${firebaseAuth.currentUser!!.uid}/$key")
//                .setValue(message)
            binding.sms.setText("")

        }


        reference.child("${groupcha.gr_name}/$key")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(Chat::class.java)
                    Log.d(TAG, "onDataChange: ${value}")
                    if (value!=null){
                        list.add(value)
                    }
                }

                chatAdapter = ChatAdapter(list)
                binding.chatmessageRv.adapter = chatAdapter
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


        reference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(Chat::class.java)
                    if (value!=null){
                        list.add(value)
                    }
                }

                chatAdapter = ChatAdapter(list)
                binding.chatmessageRv.adapter = chatAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })*/

        return binding.root
    }

    private fun sendMessage(m: String, list: ArrayList<User>, tokens: java.util.ArrayList<User>) {
        val groupcha = arguments?.getSerializable("chat") as Group
        Log.d(TAG, "sendMessage: ${list[0].token}")
        for (i in 0 until list.size) {
            Log.d(TAG, "sendMessage: ${list[i].token}")
            apiService.sendNotification(
                Sender(
                    Data(
                        firebaseAuth.currentUser!!.uid,
                        R.drawable.ic_launcher_foreground,
                        m,
                        "New Message",
                        "Hello"
                    ),
                    "${list[i].token}"

                )
            )
                .enqueue(object : Callback<MyResponce> {
                    override fun onResponse(
                        call: Call<MyResponce>,
                        response: Response<MyResponce>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(binding.root.context, "Success", Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<MyResponce>, t: Throwable) {

                    }
                })
        }
    }

    private fun groupUsers() {
        referenceUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val group = arguments?.getSerializable("chat") as Group
                val currentUser = firebaseAuth.currentUser
                val uid = currentUser?.uid
                userList.clear()
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(User::class.java)
                    if (value != null && uid != value.uid) {
                        userList.add(value)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}