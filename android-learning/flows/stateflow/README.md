
# 📚 Kotlin `StateFlow` Study Material

---

## 📖 What is `StateFlow`?

- `StateFlow` is a **state-holder** observable flow that **always has a current value**.
- It is **hot** — meaning it remains active and emits the latest value to new collectors.
- Designed to **replace LiveData** in modern Android development (ViewModels).

---

## 🛠 Key Characteristics

| Feature | StateFlow |
|:---|:---|
| Is hot or cold? | Hot (always active) |
| Has initial value? | Yes (must) |
| Can emit same value twice? | No (skips if same value) |
| Behavior with multiple collectors? | All get the latest value |
| Lifecycle awareness? | No (you must cancel manually or use `flowWithLifecycle`) |

---

## 🧠 Basic Example

```kotlin
class MyViewModel : ViewModel() {
    private val _state = MutableStateFlow(0)
    val state: StateFlow<Int> = _state

    fun incrementCounter() {
        _state.value += 1
    }
}

// Collect in UI (e.g., Fragment)
lifecycleScope.launch {
    viewModel.state.collect { value ->
        textView.text = "Counter: $value"
    }
}
```

---

## 🔥 Important Operations

- `value`: Read/write current state.
- `update {}`: Safely update value.
- `stateIn(scope, started, initialValue)`: Convert any flow into StateFlow.

```kotlin
val myStateFlow = someFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initialValue = 0)
```

---

## ✅ When to Use StateFlow?

- UI State Management (e.g., showing list, loading, error).
- Replacing LiveData inside ViewModel.
- Observing form field validations.

---

## ⚠️ Common Mistakes

- Forgetting to cancel collectors manually (use lifecycle-aware collection!).
- Using heavy logic inside `collect {}` instead of `map/filter`.

---

# 📚 Kotlin `SharedFlow` Study Material

---

## 📖 What is `SharedFlow`?

- `SharedFlow` is a **broadcast** flow for **one-time events**.
- It **does not hold a state** — unlike `StateFlow`.
- It **multicasts** emissions to all active collectors.

---

## 🛠 Key Characteristics

| Feature | SharedFlow |
|:---|:---|
| Is hot or cold? | Hot |
| Has initial value? | No |
| Replays old values? | Optional (depends on replay cache) |
| Use case | One-time events (Navigation, Toast, Snackbar) |
| Lifecycle awareness? | No |

---

## 🧠 Basic Example

```kotlin
class MyViewModel : ViewModel() {
    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun sendToastMessage() {
        viewModelScope.launch {
            _eventFlow.emit("Hello from ViewModel!")
        }
    }
}

// Collect in UI (Fragment)
lifecycleScope.launch {
    viewModel.eventFlow.collect { message ->
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
```

---

## 🔥 Important Configurations

You can configure a `SharedFlow` with:

- `replay`: How many previous values should be replayed to new subscribers.
- `extraBufferCapacity`: Buffer size for emissions.
- `onBufferOverflow`: What to do if buffer is full.

Example:

```kotlin
val _sharedFlow = MutableSharedFlow<String>(
    replay = 1,
    extraBufferCapacity = 2,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)
```

---

## ✅ When to Use SharedFlow?

- One-time UI events (toast, navigation, snackbar).
- Handling form submission result.
- Broadcasting updates to multiple collectors.

---

## ⚠️ Common Mistakes

- Using SharedFlow for **UI state** instead of **one-time events** → **Use StateFlow for state**!
- Setting wrong `replay` count and causing unexpected behavior.

---

# 🆚 StateFlow vs SharedFlow (Quick Comparison)

| Feature | StateFlow | SharedFlow |
|:---|:---|:---|
| Holds state? | Yes | No |
| Replays last value automatically? | Yes (always) | Configurable |
| Use case | UI state | UI events |
| Requires initial value? | Yes | No |
| Ideal for | Loading indicators, UI screens | Toasts, Dialogs, Navigation |

---

# 🚀 Real-world Usage Examples

## 1. `StateFlow` in ViewModel for UI State

```kotlin
data class UiState(
    val isLoading: Boolean = false,
    val data: List<Item> = emptyList(),
    val error: String? = null
)

private val _uiState = MutableStateFlow(UiState())
val uiState: StateFlow<UiState> = _uiState
```

## 2. `SharedFlow` in ViewModel for Toast Message

```kotlin
private val _toastEvent = MutableSharedFlow<String>()
val toastEvent = _toastEvent.asSharedFlow()

fun showToast() {
    viewModelScope.launch {
        _toastEvent.emit("Saved Successfully!")
    }
}
```
---

# 🎯 Kotlin `StateFlow` and `SharedFlow` Interview Questions & Answers

---

## 📚 Basic-Level Questions

---

### Q1: What is `StateFlow`?

**Answer:**  
`StateFlow` is a `Flow` that **always holds a current value** and emits updates to its collectors. It is a **hot** flow and is commonly used to represent a UI state.

---

### Q2: What is `SharedFlow`?

**Answer:**  
`SharedFlow` is a `Flow` designed to **broadcast events** to multiple collectors without holding any state. It is also a **hot** flow and is useful for handling **one-time events** like navigation, messages, or actions.

---

### Q3: How is `StateFlow` different from `SharedFlow`?

| Feature | StateFlow | SharedFlow |
|:---|:---|:---|
| Holds state? | Yes | No |
| Requires initial value? | Yes | No |
| Best for | State Management | Events (navigation, toasts) |
| Replay Behavior | Always replays latest | Configurable replay |

---

### Q4: Can we use `StateFlow` instead of `LiveData`?

**Answer:**  
Yes, `StateFlow` is often used as a **replacement for LiveData** because it naturally fits into Kotlin coroutines and flows without lifecycle overhead.

---

### Q5: Does `StateFlow` automatically cancel when the screen is destroyed?

**Answer:**  
No, `StateFlow` is not lifecycle-aware.  
You must collect it inside a lifecycle-aware coroutine (e.g., `repeatOnLifecycle` or `flowWithLifecycle`).

Example:

```kotlin
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.uiState.collect { /* ... */ }
    }
}
```

---

## 📚 Senior-Level Questions

---

### Q6: Explain `replay`, `extraBufferCapacity`, and `BufferOverflow` in `SharedFlow`.

**Answer:**

- `replay`: Number of past emissions that new subscribers will immediately receive.
- `extraBufferCapacity`: Number of items emitted without suspension (additional buffer).
- `BufferOverflow`:
  - `SUSPEND`: suspend emitter when buffer is full
  - `DROP_OLDEST`: drop oldest value
  - `DROP_LATEST`: drop latest emission

---

### Q7: What happens if two collectors subscribe to a `SharedFlow`?

**Answer:**  
Both collectors will **independently receive** all emitted values (if buffer and replay allow).  
It's a **multicast**.

---

### Q8: How to convert a cold `Flow` into `StateFlow`?

**Answer:**  
Use the `stateIn()` operator:

```kotlin
val stateFlow = myFlow.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = MyInitialState()
)
```

---

### Q9: What is `SharingStarted` in `StateFlow`?

**Answer:**  
`SharingStarted` controls when the upstream flow starts/stops:

- `WhileSubscribed(timeoutMillis)`: Starts when there are active collectors and stops after timeout.
- `Eagerly`: Starts immediately without waiting.
- `Lazily`: Starts when first collected.

---

### Q10: Can we emit values into a `StateFlow` or `SharedFlow` from multiple coroutines?

**Answer:**  
Yes, but you must ensure thread safety manually if multiple coroutines modify the flow.  
Typically, you encapsulate `MutableStateFlow` or `MutableSharedFlow` inside a `ViewModel`.

---

# 2. ⚡ Cheat Sheet: Flow vs StateFlow vs SharedFlow

---

| Feature | Flow | StateFlow | SharedFlow |
|:---|:---|:---|:---|
| Type | Cold | Hot | Hot |
| Holds latest value? | No | Yes | No |
| Requires initial value? | No | Yes | No |
| Suitable for | Streams | UI State | One-time events |
| Collects from | Start | Latest value | All events |
| Lifecycle-aware? | No | No | No |
| Multicast? | No | Yes | Yes |
| Example Usage | API responses, DB streams | UI state (loading, error) | Navigation, toasts |

---
