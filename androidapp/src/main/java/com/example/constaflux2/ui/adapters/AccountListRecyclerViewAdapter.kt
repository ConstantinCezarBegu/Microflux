package com.example.constaflux2.ui.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.constaflux2.R
import com.example.constaflux2.data.ServerId
import com.example.constaflux2.data.UserId
import com.example.constaflux2.database.Account
import com.example.constaflux2.databinding.ListAccountBinding
import com.example.constaflux2.databinding.ListAddBinding
import com.example.constaflux2.util.layoutInflater

class AccountListRecyclerViewAdapter(
    private val accountClickCallback: (Account, Context, Int) -> Unit = { _, _, _ -> },
    private val addClickCallBack: (Context, Int) -> Unit = { _, _ -> }
) : ListAdapter<Account, RecyclerView.ViewHolder>(diffItemCallback) {
    companion object {
        private val diffItemCallback = object : DiffUtil.ItemCallback<Account>() {
            override fun areItemsTheSame(
                oldItem: Account,
                newItem: Account
            ): Boolean = oldItem.userId == newItem.userId

            override fun areContentsTheSame(
                oldItem: Account,
                newItem: Account
            ): Boolean = oldItem.serverUrl == newItem.serverUrl
                    && oldItem.userName == newItem.userName
        }
    }

    override fun getItemCount() = super.getItemCount() + 1

    override fun getItemViewType(position: Int) =
        if (position == super.getItemCount()) R.layout.list_add else R.layout.list_account

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.list_add -> AddViewHolder(
                ListAddBinding.inflate(
                    parent.context.layoutInflater,
                    parent,
                    false
                )
            )
            else -> AccountViewHolder(
                ListAccountBinding.inflate(
                    parent.context.layoutInflater,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddViewHolder -> {
            }
            is AccountViewHolder -> holder.item = getItem(position)
        }
    }

    inner class AccountViewHolder(
        private val itemBinding: ListAccountBinding
    ) :
        RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        var item: Account? = null
            set(value) {
                field = value
                value ?: return
                onBindingCreated(value, itemBinding)
            }

        init {
            itemBinding.root.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val item = item ?: return
            accountClickCallback(item, v.context, absoluteAdapterPosition)
        }

        private fun onBindingCreated(item: Account, binding: ListAccountBinding) {
            binding.run {
                username.text = item.userName.name
                userUrl.text = item.serverUrl.url
            }
        }
    }

    inner class AddViewHolder(
        listAddBinding: ListAddBinding
    ) : RecyclerView.ViewHolder(listAddBinding.root), View.OnClickListener {

        init {
            listAddBinding.root.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            addClickCallBack(v.context, absoluteAdapterPosition)
        }
    }


}