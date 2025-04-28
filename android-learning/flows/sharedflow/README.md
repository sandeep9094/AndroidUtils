
# 📚 Kotlin `SharedFlow` Study Material

## 📖 What is SharedFlow?

SharedFlow is a **hot flow** that **broadcasts** emitted values to multiple collectors.  
It **does not hold any state** — it simply emits values to whoever is collecting at that moment (or replays a few if configured).

It is mainly used for **one-time events** like:

- Navigation commands
- Showing Toasts or Snackbars
- Triggering side effects (e.g., refresh)

It behaves like an **event bus** for your app, but in a safe, coroutine-based way.

---

# 🎯 Key Concepts

**1. Hot Flow**  
Collectors immediately receive emissions if active (no need to "start" flow).

**2. Multicast**  
Multiple collectors can listen at the same time.

**3. Replay Buffer**  
You can configure how many old emissions new collectors will instantly get (`replay`).

**4. Buffer Strategy**  
You can define what happens when buffer overflows: suspend, drop oldest, or drop latest.

**5. No Default Value**  
Unlike `StateFlow`, `SharedFlow` doesn't require any initial value.

---

# 📋 SharedFlow Interview Questions & Answers

## Basic-Level

**Q1. What is SharedFlow?**

**Answer:**  
SharedFlow is a coroutine-based API that emits values to multiple collectors without holding any state. It is a hot stream useful for broadcasting one-time events.

**Q2. How is SharedFlow different from StateFlow?**

| Feature | StateFlow | SharedFlow |
|:---|:---|:---|
| Holds Latest Value | Yes | No |
| Needs Initial Value | Yes | No |
| Use-case | State updates | One-time Events |
| Replay | Always 1 (latest) | Configurable |

**Q3. What happens if no one is collecting a SharedFlow?**

**Answer:**  
The emission is stored in the buffer if configured (replay or extra buffer). Otherwise, it may suspend or drop, depending on the `BufferOverflow` setting.

**Q4. How to emit values into SharedFlow?**

**Answer:**  
Use `emit()` inside a coroutine or `tryEmit()` immediately.

Example:

```kotlin
private val _eventFlow = MutableSharedFlow<String>()
val eventFlow: SharedFlow<String> = _eventFlow

viewModelScope.launch {
    _eventFlow.emit("ShowToast")
}
```

## Senior-Level

**Q5. Explain replay, extraBufferCapacity, and BufferOverflow parameters in SharedFlow.**

**Answer:**

- **replay**: Number of previously emitted values to replay for new subscribers.
- **extraBufferCapacity**: Number of values allowed in the buffer beyond replay cache.
- **BufferOverflow**: Strategy when buffer is full (SUSPEND, DROP_OLDEST, DROP_LATEST).

Example:

```kotlin
private val _events = MutableSharedFlow<String>(
    replay = 1,
    extraBufferCapacity = 2,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)
```

**Q6. How to use SharedFlow to implement a one-time event like a navigation command?**

**Answer:**  
Inside ViewModel:

```kotlin
private val _navigationCommands = MutableSharedFlow<NavigationCommand>()
val navigationCommands: SharedFlow<NavigationCommand> = _navigationCommands

fun navigateToDetails() {
    viewModelScope.launch {
        _navigationCommands.emit(NavigationCommand.To("details_screen"))
    }
}
```

In Fragment:

```kotlin
lifecycleScope.launchWhenStarted {
    viewModel.navigationCommands.collect { command ->
        // handle navigation
    }
}
```

**Q7. Why not use Channel instead of SharedFlow for events?**

**Answer:**  
`Channel` is now considered lower-level and harder to manage safely.  
`SharedFlow` integrates better with structured concurrency, error handling, and is simpler for multiple collectors.

**Q8. What are typical problems with SharedFlow if buffer settings are wrong?**

**Answer:**  

- If no replay or buffer, events might get lost if collector is not active.
- Too large buffer can cause memory leaks.
- Incorrect overflow policy can crash or block emissions.

Always configure carefully based on event importance and expected collector behavior.

---

# ⚡ SharedFlow Usage Examples

## Example 1: Basic Event Broadcasting

```kotlin
// In ViewModel
private val _eventFlow = MutableSharedFlow<String>()
val eventFlow: SharedFlow<String> = _eventFlow

fun sendEvent(message: String) {
    viewModelScope.launch {
        _eventFlow.emit(message)
    }
}

// In Fragment
lifecycleScope.launchWhenStarted {
    viewModel.eventFlow.collect { message ->
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
```

## Example 2: Using Replay and BufferOverflow

```kotlin
private val _messages = MutableSharedFlow<String>(
    replay = 2,
    extraBufferCapacity = 3,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)

val messages: SharedFlow<String> = _messages

fun sendMessage(text: String) {
    viewModelScope.launch {
        _messages.emit(text)
    }
}
```

In this case:

- The last 2 messages are automatically replayed for new collectors
- If buffer overflows, the oldest message is dropped first

---

# 📚 When to use SharedFlow?

- Trigger UI actions (navigation, snackbars, dialogs)
- Broadcast events from ViewModel to multiple Fragments
- Send analytics events
- Multi-cast important updates to multiple UI layers

---

# 🎯 Key Summary Points

- `SharedFlow` = **Event Broadcasting**, **Multiple Collectors**, **Hot Flow**
- Configure `replay`, `extraBufferCapacity`, and `onBufferOverflow` carefully
- Prefer `SharedFlow` over `Channel` for modern event handling
- `MutableSharedFlow` inside ViewModel, expose only `SharedFlow` to outside

---

# 🚀 Quick Comparison: StateFlow vs SharedFlow vs Channel

| Feature | StateFlow | SharedFlow | Channel |
|:---|:---|:---|:---|
| Type | Hot Flow | Hot Flow | Cold Stream |
| Holds Value | Yes (latest value) | No | No |
| Initial Value Required | Yes | No | No |
| Multiple Collectors | Yes | Yes | No (each receives separately) |
| Best for | State updates (UI) | Events broadcast (UI actions) | One-time communication (single consumer) |
| Replay | Always latest value | Configurable (0 or more) | No replay |

# 🧠 Key Takeaways

- Use **StateFlow** for **continuous UI states** (example: screen loading, data updates).
- Use **SharedFlow** for **events** (example: navigation, showing snackbars, dialog popups).
- Use **Channel** when **only one receiver** should get the event and it should not be replayed later (rare in modern Android).

✅ In modern Android + Kotlin apps, prefer **StateFlow** and **SharedFlow** over **Channel**.

