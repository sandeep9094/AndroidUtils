
# 📚 Kotlin Flows

---

## 1. **What is Kotlin Flow?**
- **Flow** is a **cold asynchronous stream** that can emit multiple values sequentially.
- Built on top of **coroutines**.
- It **emits values** over time, **does not start** until collected.

> "Cold" means that the code inside the flow builder doesn't run until the flow is collected.


## 2. **Core Building Blocks**

| Function | Description | Example |
|:---|:---|:---|
| `flow {}` | Builder function to create a Flow | `flow { emit(1) }` |
| `collect {}` | Terminal operation to get values | `myFlow.collect { value -> println(value) }` |
| `map {}` | Transform emitted values | `flow.map { it * 2 }` |
| `filter {}` | Filter emitted values | `flow.filter { it % 2 == 0 }` |
| `catch {}` | Handle exceptions in Flow | `flow.catch { emit(-1) }` |
| `onEach {}` | Perform side effects | `flow.onEach { println(it) }` |
| `debounce()` | Wait before emitting after silence | Ideal for search inputs |
| `combine()` | Combine multiple Flows | |
| `flatMapLatest()` | Switch to a new flow on each new emission | |


## 3. **Lifecycle of Flow**

1. **Create a flow** (using `flow {}`).
2. **Intermediate operations** (e.g., `map`, `filter`, `catch`).
3. **Terminal operator** (`collect` or `launchIn`).



## 4. **Real-World Usage Examples**

### 4.1 Basic Flow Example

```kotlin
val simpleFlow = flow {
    emit(1)
    emit(2)
    emit(3)
}

// Collecting
lifecycleScope.launch {
    simpleFlow.collect { value ->
        Log.d("FlowExample", "Received: $value")
    }
}
```



### 4.2 Using Flows for Search Debouncing (UI Search)

```kotlin
val queryFlow = MutableStateFlow("")

lifecycleScope.launch {
    queryFlow
        .debounce(300)
        .filter { it.isNotBlank() }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            searchApi.search(query)
        }
        .catch { e -> emit(emptyList()) }
        .collect { result ->
            updateUI(result)
        }
}
```



### 4.3 Combine Two Flows

```kotlin
val flowA = flowOf(1, 2, 3)
val flowB = flowOf("A", "B", "C")

val combinedFlow = flowA.combine(flowB) { a, b ->
    "$a -> $b"
}

lifecycleScope.launch {
    combinedFlow.collect {
        Log.d("Combined", it)
    }
}
```



### 4.4 Room Database + Flow

```kotlin
@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>
}

// Collecting in ViewModel
viewModelScope.launch {
    userDao.getAllUsers().collect { users ->
        _userState.value = users
    }
}
```



### 4.5 Retrofit API + Flow

```kotlin
interface ApiService {
    @GET("users")
    fun getUsers(): Flow<List<User>>
}
```
(Note: Retrofit doesn't support Flow out-of-the-box. You need to wrap suspend functions using `flow {}` manually.)



## 5. **Important Operators for Senior Engineers**

| Operator | Use |
|:---|:---|
| `buffer()` | Control backpressure by adding buffer between upstream and downstream |
| `conflate()` | Skip intermediate values when collector is slow |
| `flowOn(dispatcher)` | Change dispatcher of upstream operations |
| `retry()` | Retry upstream on error |
| `zip()` | Combine two flows one-by-one (pairs) |
| `stateIn(scope)` | Convert to StateFlow for hot streams |
| `shareIn(scope)` | Share a flow across multiple collectors |

---

## 6. **Questions and Answers (Interview Level)**


### **Q1: What is the difference between Flow, LiveData, and StateFlow?**

| | Flow | LiveData | StateFlow |
|:---|:---|:---|:---|
| Nature | Cold Stream | Hot Stream | Hot Stream |
| Lifecycle Aware? | No | Yes | No |
| Cancellation | Coroutine-scope based | Lifecycle based | Coroutine-scope based |
| Use Case | Background work, chains | UI data binding | Represent UI state |


### **Q2: Explain backpressure handling in Kotlin Flow.**

- **Backpressure** is when producer emits faster than consumer can consume.
- Kotlin Flow handles it via:
  - `buffer()`
  - `conflate()`
  - `collectLatest()`



### **Q3: What does `flatMapLatest` do?**

- Cancels the previous flow when a new emission comes.
- Useful for Search: when user types a new query, cancel old search.

Example:

```kotlin
searchFlow
    .flatMapLatest { query -> searchApi.search(query) }
```


### **Q4: How does `flowOn` work?**

- `flowOn(dispatcher)` changes the context of **upstream** flow operations.
- Downstream (collector) still operates on original coroutine context.

Example:

```kotlin
flow {
    emit(loadData())
}.flowOn(Dispatchers.IO)
```



### **Q5: What is the difference between `shareIn` and `stateIn`?**

| | shareIn | stateIn |
|:---|:---|:---|
| Behavior | Shares a flow to multiple collectors | Shares + Retains last emitted value |
| Memory | May not hold last value | Always holds last value |
| Use Case | Events, streams | UI State management |

---

## 7. **Best Practices**

✅ Always handle errors using `.catch {}`.

✅ Use `flowOn` to avoid blocking main thread.

✅ Use `stateIn` for ViewModel's state representation.

✅ Use `flatMapLatest` for real-time changing inputs.

✅ Use `shareIn` for broadcast-like behavior.

✅ Remember cancellation! Always tie flows to `CoroutineScope` (e.g., `viewModelScope`, `lifecycleScope`).

---

# 📌 Quick Cheatsheet

```kotlin
flowOf(1,2,3)                // Create flow from values
flow { emit(1) }             // Builder
.map { it * 2 }              // Transform
.filter { it > 2 }           // Filter
.catch { }                  // Error handling
.collect { }                 // Terminal operation
.flowOn(Dispatchers.IO)      // Dispatcher change
.buffer()                    // Buffer values
.conflate()                  // Skip values if slow
.flatMapLatest { }           // Cancel previous on new
.combine(other) { a,b -> }   // Merge two flows
.zip(other) { a,b -> }       // Pair two flows
.stateIn(viewModelScope)     // Hot & retain state
.shareIn(viewModelScope)     // Broadcast
```

---

# ✨ Final Real-World Architecture Example

**Repository Layer:**

```kotlin
class UserRepository(private val api: ApiService) {

    fun fetchUsers(): Flow<List<User>> = flow {
        emit(api.getUsers())
    }.catch { e ->
        emit(emptyList())
    }.flowOn(Dispatchers.IO)
}
```

**ViewModel Layer:**

```kotlin
class UserViewModel(private val repo: UserRepository): ViewModel() {
    val usersFlow = repo.fetchUsers()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
```

**UI Layer:**

```kotlin
lifecycleScope.launch {
    viewModel.usersFlow.collect { users ->
        adapter.submitList(users)
    }
}
```

---
Would you like me to continue and build those for you too? 🚀
