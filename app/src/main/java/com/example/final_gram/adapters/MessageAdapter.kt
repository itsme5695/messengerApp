package com.example.final_gram.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.final_gram.databinding.ItemFromBinding
import com.example.final_gram.databinding.ItemToBinding
import com.example.final_gram.models.Message

class MessageAdapter (var list: List<Message>, var uid: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    inner class FromVh(var itemFromBinding: ItemFromBinding) :
        RecyclerView.ViewHolder(itemFromBinding.root) {

        fun onBind(message: Message) {
            itemFromBinding.messageTv.text = message.message

        }
    }

    inner class ToVh(var itemToBinding: ItemToBinding) :
        RecyclerView.ViewHolder(itemToBinding.root) {

        fun onBind(message: Message) {
            itemToBinding.messageTv.text = message.message

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            return FromVh(ItemFromBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }else{
            return ToVh(ItemToBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 1) {
            val fromVh = holder as FromVh
            fromVh.onBind(list[position])
        }else{
            val toVh = holder as ToVh
            toVh.onBind(list[position])
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (list[position].fromUid == uid){
            return 1
        } else {
            return 2
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}