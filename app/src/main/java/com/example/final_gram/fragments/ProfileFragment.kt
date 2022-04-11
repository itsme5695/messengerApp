package com.example.final_gram.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.final_gram.R
import com.example.final_gram.adapters.MessageAdapter
import com.example.final_gram.databinding.FragmentProfileBinding
import com.example.final_gram.models.*
import com.example.final_gram.retrofit.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
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

    lateinit var binding: FragmentProfileBinding
    lateinit var firebaseAuth: FirebaseAuth

    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var messageAdapter: MessageAdapter
    lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")
        apiService =
            ApiClient.getRetrofit("https://fcm.googleapis.com/").create(ApiService::class.java)


        val usercha = arguments?.getSerializable("key") as User



        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        Picasso.get().load(usercha.photoUrl).into(binding.image1)
        binding.namee.text = usercha.displayName
        if (usercha.online == true) {
            binding.messagee.text = "Online"

        } else {
            binding.messagee.text = "Offline"
            //binding.messagee.setTextColor(R.color.black!!)
            binding.messagee.setTextColor(Color.BLACK)
        }

        binding.sendBtn.setOnClickListener {
            val m = binding.sms.text.toString()
            val simpleDateFormat = SimpleDateFormat("HH:mm")
            val date = simpleDateFormat.format(Date())
            val message =
                Message(m, date, firebaseAuth.currentUser!!.uid, usercha.uid, usercha.photoUrl)

            val key = reference.push().key
            reference.child("${firebaseAuth.currentUser!!.uid}/messages/${usercha.uid}/$key")
                .setValue(message)

            reference.child("${usercha.uid}/messages/${firebaseAuth.currentUser!!.uid}/$key")
                .setValue(message)
            binding.sms.setText("")
            apiService.sendNotification(
                Sender(
                    Data(
                        firebaseAuth.currentUser!!.uid,
                        R.drawable.ic_launcher_foreground,
                        m,
                        "New message", usercha.uid ?: ""
                    ), usercha.token ?:""
                )
            )
                .enqueue(object :Callback<MyResponce>{
                    override fun onResponse(
                        call: Call<MyResponce>,
                        response: Response<MyResponce>
                    ) {
                        if (response.isSuccessful){
                            Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<MyResponce>, t: Throwable) {

                    }

                })
        }

        reference.child("${firebaseAuth.currentUser!!.uid}/messages/${usercha.uid}")
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
                    messageAdapter = MessageAdapter(list, firebaseAuth.currentUser!!.uid)
                    binding.messageRv.adapter = messageAdapter
                    binding.messageRv.scrollToPosition(list.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        return binding.root
    }


}