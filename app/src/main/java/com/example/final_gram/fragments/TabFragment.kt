package com.example.final_gram.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.example.final_gram.adapters.UserAdapter
import com.example.final_gram.databinding.FragmentTabBinding
import com.example.final_gram.models.Message
import com.example.final_gram.models.Recently
import com.example.final_gram.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.final_gram.R

private const val ARG_PARAM1 = "param1"

class TabFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var token: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            token = it.getString(ARG_PARAM1)

        }
    }

    lateinit var binding: FragmentTabBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    private val TAG = "TabFragment"

    lateinit var userAdapter: UserAdapter
    var list = ArrayList<User>()
    var recently = ArrayList<Recently>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTabBinding.inflate(layoutInflater, container, false)

//        if (categoryID == 1) {
        //chat fragment
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")

        val email = currentUser?.email
        val displayName = currentUser?.displayName
        val phoneNumber = currentUser?.phoneNumber
        val photoUrl = currentUser?.photoUrl
        val uid = currentUser?.uid

        val user = User(email, displayName, phoneNumber, photoUrl.toString(), uid!!, false,token)


        for (recent in recently) {
            if (recent.sms.isNotEmpty()) {
                Log.d(TAG, "onCreateView: ${recent}")
            }
        }
//        updateData(email,displayName,phoneNumber,photoUrl,uid,true)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                val filterList = arrayListOf<User>()
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(User::class.java)
                    if (value != null && uid != value.uid) {
                        list.add(value)
                    }
                    if (value != null && value.uid == uid) {
                        filterList.add(value)
                    }
                }
                if (filterList.isEmpty()) {
                    reference.child(uid).setValue(user)
                }
                setRecentMessage(list)

                userAdapter = UserAdapter(
                    list,
                    firebaseDatabase,
                    uid,
                    object : UserAdapter.OnItemClickListner {
                        override fun onItemClick(user: User) {
                            var bundle = Bundle()
                            bundle.putSerializable("key", user)
                            findNavController().navigate(R.id.profileFragment,bundle)
                        }
                    })
                binding.userRv.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        return binding.root
    }

    private fun setRecentMessage(list: ArrayList<User>) {
        for (i in 0 until list.size) {
            reference.child("${firebaseAuth.currentUser?.uid}/messages/${list[i].uid}")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val messagesss = arrayListOf<Message>()
                        val messages = snapshot.children
                        try {
                            val last = messages.last().getValue(Message::class.java)
                            Log.d(TAG, "onDataChange: ${last!!.message}")

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }
    }

    override fun onResume() {
        super.onResume()
        reference.child("${firebaseAuth.currentUser!!.uid}/online").setValue(true)
    }

    override fun onStop() {
        super.onStop()
        reference.child("${firebaseAuth.currentUser!!.uid}/online").setValue(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        reference.child("${firebaseAuth.currentUser!!.uid}/online").setValue(false)
    }

    companion object {
        @JvmStatic
        fun newInstance(token: String) =
            TabFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, token)

                }
            }
    }
}