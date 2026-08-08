The Domain should speak the language of the business, not the storage technology.

```markdown
Room (Entity)
↓
DAO
↓
Repository (Entity → Domain mapping)
↓
ViewModel (Domain → UiState)
↓
Compose UI
```

Architecture Invariants

UI never accesses Room directly.

UI never knows Entity classes.
Repository is the single source of truth for note data.
Domain layer never imports Android classes.
Data layer never imports Compose UI.
Every layer communicates only with its immediate neighbor.

### Dependency Injection Rules

- Depend on abstractions, not implementations.
- Register interfaces in Koin whenever a stable contract exists.
- Constructor injection is preferred over property injection.
- Koin modules should contain only dependency wiring.
- Business logic must never exist inside Koin modules.
- Avoid service locator patterns outside the DI container.
- ViewModels depend on Repository interfaces, never Repository implementations.

### Data Source Rules

- Data Sources are introduced only when more than one
  physical source of data exists.
- Do NOT create LocalDataSource while Room is the
  only source.
- Repository may communicate directly with DAO until
  another data source becomes necessary.

### UiState Rules

- Every screen owns exactly one immutable UiState.
- UiState is represented by a data class unless mutually exclusive
  states genuinely require a sealed hierarchy.
- UiState contains only presentation state.
- UiState never contains business logic.
- UiState is created and owned by the ViewModel.
- Composables receive UiState and render it.
- Composables must never directly observe repositories.

### ViewModel Rules

- Every ViewModel owns exactly one UiState.
- ViewModels expose immutable StateFlow only.
- MutableStateFlow remains private.
- ViewModels depend on Repository interfaces.
- ViewModels never depend on DAO.
- ViewModels never depend on Room.
- ViewModels never expose mutable state.
- ViewModels coordinate; they do not implement business rules.
- ViewModel may contain **UI-level business logic**, but not **domain/business rules**

### StateFlow Update Rule

When updating immutable UiState inside a ViewModel,
prefer using:

```kotlin
_uiState.update { current ->
current.copy(...)
}
```

instead of:

```kotlin
_uiState.value = ...
```

Reasons:

- Encourages immutable state updates.
  • Easier to read.
  • Scales better as UiState grows.
  • Prevents accidental overwriting of unrelated fields.

### Flow Collection Rules

- ViewModels own Flow collection.
- Repositories expose Flow.
- Composables never collect directly from repositories.
- Long-lived streams should begin in init {} when appropriate.
- Every collected Flow updates UiState using:

```kotlin
_uiState.update { current ->
    current.copy(...)
}
```

- ViewModels should collect using viewModelScope.

### Navigation Destination Rules

- Every destination is a top-level @Serializable object or data class.
- Do not use string routes.
- Do not wrap destinations inside sealed interfaces unless a real
  requirement emerges.
- Use sealed hierarchy only when polymorphism is required.

Examples:

```kotlin
@Serializable
data object Home
@Serializable
data class Editor(
val noteId: Long?
)
```

## Repository Design Rules

Repositories define business operations, not implementation details.

Rules:

- Repository interfaces belong to the Domain layer.
  • Repository implementations belong to the Data layer.
- Repository names should express business intent.
  Example:
  createNote()
  instead of:
  insertNote()
- Repositories expose immutable APIs only.
- Use Flow

  for continuously changing data.

- Use suspend functions for one-time operations.
- Never expose:
- MutableStateFlow
- MutableSharedFlow
- MutableList
- LiveData
- Repository interfaces must never depend on:
- Room
- Compose
- Android Framework
- Retrofit
- DataStore
- Repository implementations are responsible for mapping
  between persistence models and domain models.

Repositories describe WHAT the application can do,
not HOW it is implemented.

### Effects Rules

- Persistent UI belongs in UiState.
- Effects must be **consumed exactly once**
- One-time events belong in Effect.
- ViewModels emit Effect through Channel.
- Screens collect Effect using receiveAsFlow().
- Effects are used for:
- Navigation
- Snackbars
- Dialogs
- Toasts
- Permission requests
- Effects must never be stored in UiState.

### Mapping Rules

- Mapping must be explicit and unidirectional.
- Entity → Domain in data layer.
- Domain → UiState in ViewModel.
- Never expose Entity outside data layer.
- Never expose UiState outside UI layer.

### Compose Stability Rules

- UiState should be immutable.
- Prefer data classes with val properties only.
- Avoid passing mutable collections to UI.
- Use derivedStateOf only when needed.

### Coroutine Rules

- Use viewModelScope for UI-related work.
- Repository should not know about UI scope.
- Avoid launching coroutines inside Composables.
- Prefer suspend functions or Flow from Repository.The Domain should speak the language of the business, not the storage technology.

```markdown
Room (Entity)
↓
DAO
↓
Repository (Entity → Domain mapping)
↓
ViewModel (Domain → UiState)
↓
Compose UI
```

Architecture Invariants

UI never accesses Room directly.

UI never knows Entity classes.
Repository is the single source of truth for note data.
Domain layer never imports Android classes.
Data layer never imports Compose UI.
Every layer communicates only with its immediate neighbor.

### Dependency Injection Rules

- Depend on abstractions, not implementations.
- Register interfaces in Koin whenever a stable contract exists.
- Constructor injection is preferred over property injection.
- Koin modules should contain only dependency wiring.
- Business logic must never exist inside Koin modules.
- Avoid service locator patterns outside the DI container.
- ViewModels depend on Repository interfaces, never Repository implementations.

### Data Source Rules

- Data Sources are introduced only when more than one
  physical source of data exists.
- Do NOT create LocalDataSource while Room is the
  only source.
- Repository may communicate directly with DAO until
  another data source becomes necessary.

### UiState Rules

- Every screen owns exactly one immutable UiState.
- UiState is represented by a data class unless mutually exclusive
  states genuinely require a sealed hierarchy.
- UiState contains only presentation state.
- UiState never contains business logic.
- UiState is created and owned by the ViewModel.
- Composables receive UiState and render it.
- Composables must never directly observe repositories.

### ViewModel Rules

- Every ViewModel owns exactly one UiState.
- ViewModels expose immutable StateFlow only.
- MutableStateFlow remains private.
- ViewModels depend on Repository interfaces.
- ViewModels never depend on DAO.
- ViewModels never depend on Room.
- ViewModels never expose mutable state.
- ViewModels coordinate; they do not implement business rules.
- ViewModel may contain **UI-level business logic**, but not **domain/business rules**

### StateFlow Update Rule

When updating immutable UiState inside a ViewModel,
prefer using:

```kotlin
_uiState.update { current ->
current.copy(...)
}
```

instead of:

```kotlin
_uiState.value = ...
```

Reasons:

- Encourages immutable state updates.
  • Easier to read.
  • Scales better as UiState grows.
  • Prevents accidental overwriting of unrelated fields.

### Flow Collection Rules

- ViewModels own Flow collection.
- Repositories expose Flow.
- Composables never collect directly from repositories.
- Long-lived streams should begin in init {} when appropriate.
- Every collected Flow updates UiState using:

```kotlin
_uiState.update { current ->
    current.copy(...)
}
```

- ViewModels should collect using viewModelScope.

### Navigation Destination Rules

- Every destination is a top-level @Serializable object or data class.
- Do not use string routes.
- Do not wrap destinations inside sealed interfaces unless a real
  requirement emerges.
- Use sealed hierarchy only when polymorphism is required.

Examples:

```kotlin
@Serializable
data object Home
@Serializable
data class Editor(
val noteId: Long?
)
```

### Effects Rules

- Persistent UI belongs in UiState.
- Effects must be **consumed exactly once**
- One-time events belong in Effect.
- ViewModels emit Effect through Channel.
- Screens collect Effect using receiveAsFlow().
- Effects are used for:
- Navigation
- Snackbars
- Dialogs
- Toasts
- Permission requests
- Effects must never be stored in UiState.

### Mapping Rules

- Mapping must be explicit and unidirectional.
- Entity → Domain in data layer.
- Domain → UiState in ViewModel.
- Never expose Entity outside data layer.
- Never expose UiState outside UI layer.

### Compose Stability Rules

- UiState should be immutable.
- Prefer data classes with val properties only.
- Avoid passing mutable collections to UI.
- Use derivedStateOf only when needed.

### Coroutine Rules

- Use viewModelScope for UI-related work.
- Repository should not know about UI scope.
- Avoid launching coroutines inside Composables.
- Prefer suspend functions or Flow from Repository.