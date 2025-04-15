
# BaseListAdapter for RecyclerView in Kotlin Android

A reusable generic `BaseListAdapter` to simplify and standardize RecyclerView adapter implementations using **ViewBinding** and **DiffUtil**.



## Features

- ✅ Generic and reusable for any data type.
- ✅ Built-in ViewBinding support.
- ✅ Click listener support.
- ✅ DiffUtil integration for efficient list updates.



## 🧑‍💻 BaseListAdapter Implementation

```kotlin
abstract class BaseListAdapter<T : Any, VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
    private val itemClickListener: ((T) -> Unit)? = null,
    inline val bind: (item: T, binding: VB, position: Int) -> Unit,
) : ListAdapter<T, BaseListAdapter.BaseViewHolder<VB>>(BaseItemCallback<T>()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> {
        val binding = bindingInflater(LayoutInflater.from(parent.context), parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {
        val item = getItem(position)
        bind(item, holder.binding, position)
        holder.binding.root.setOnClickListener {
            itemClickListener?.invoke(item)
        }
    }

    class BaseViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)
}

class BaseItemCallback<T : Any> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.toString() == newItem.toString()
    override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}
```

# UserItemAdapter with BaseListAdapter

This example demonstrates how to create and use a `UserItemAdapter` in a Kotlin Android project by extending a generic `BaseListAdapter` that supports ViewBinding and item click handling.



##  Data Model

### `User.kt`

```kotlin
package com.example.recyclerviewbestpractice.api.model

data class User(val name: String)
```




##  UserItemAdapter

### `UserItemAdapter.kt`

```kotlin
package com.example.recyclerviewbestpractice.adapter

import com.example.recyclerviewbestpractice.databinding.AdapterUserItemBinding
import com.example.recyclerviewbestpractice.api.model.User

class UserItemAdapter(
    itemClickListener: (User) -> Unit
) : BaseListAdapter<User, AdapterUserItemBinding>(
    bindingInflater = AdapterUserItemBinding::inflate,
    itemClickListener = itemClickListener,
    bind = { user, binding, _ ->
        binding.textView.text = user.name
    }
)
```





##  Layout File

### `adapter_user_item.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:id="@+id/textView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="TextView"
        android:textColor="@android:color/black"
        android:padding="16dp"
        android:textStyle="bold"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```





##  How to use in Activity or Fragment


```kotlin
private lateinit var adapter: UserItemAdapter

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    adapter = UserItemAdapter { user ->
        Toast.makeText(requireContext(), "Clicked: ${user.name}", Toast.LENGTH_SHORT).show()
    }

    recyclerView.adapter = adapter
    recyclerView.layoutManager = LinearLayoutManager(requireContext())

    val userList = listOf(
        User("Alice"),
        User("Bob"),
        User("Charlie")
    )

    adapter.submitList(userList)
}

```




