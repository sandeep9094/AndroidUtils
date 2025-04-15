package com.example.recyclerviewbestpractice.adapter

import com.example.recyclerviewbestpractice.databinding.AdapterUserItemBinding
import com.example.recyclerviewbestpractice.adapter.BaseListAdapter
import com.example.recyclerviewbestpractice.api.model.User

class UserItemAdapter(
    itemClickListener: (User) -> Unit
): BaseListAdapter<User, AdapterUserItemBinding> (
    bindingInflater = AdapterUserItemBinding::inflate,
    itemClickListener = itemClickListener,
    bind = { user, binding, position ->
        user.apply { 
            binding.textView.text = name
        }
    }
)