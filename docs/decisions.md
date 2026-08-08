# 📌 Architectural Decisions

This document records key architectural and design decisions made in the NoteCraft project.

Each decision includes:
- Context
- Decision
- Rationale
- Trade-offs

---

## 1. Clean Architecture with Layer Separation

### Context
We needed a scalable architecture that prevents tight coupling between UI, data, and business logic.

### Decision
Adopt a layered architecture:

Room → DAO → Repository → ViewModel → Compose UI

### Rationale
- Clear separation of concerns
- Easier testing and maintenance
- Supports future scalability (e.g., remote data source)

### Trade-offs
- Slight increase in boilerplate
- Requires discipline to maintain boundaries

---

## 2. Repository as Single Source of Truth

### Context
Multiple parts of the app require access to note data.

### Decision
All data access must go through the Repository.

### Rationale
- Centralized data handling
- Easier to introduce caching, sync, or multiple data sources later
- Keeps ViewModel simple

### Trade-offs
- Additional abstraction layer

---

## 3. Domain Models vs Entities Separation

### Context
Room entities are tightly coupled with database schema.

### Decision
Use separate Domain models (`Note`) and map from Entity → Domain.

### Rationale
- Domain speaks business language
- Prevents database schema leaking into UI
- Enables future backend/API compatibility

### Trade-offs
- Requires mapping code

---

## 4. Unidirectional Data Flow (UDF)

### Context
UI state can become unpredictable in complex apps.

### Decision
Adopt UDF:

Event → ViewModel → State → UI

### Rationale
- Predictable state management
- Easier debugging
- Scales well for complex features

### Trade-offs
- Slightly more structured code

---

## 5. Single UiState per Screen

### Context
Managing multiple state objects leads to inconsistency.

### Decision
Each screen owns exactly one UiState.

### Rationale
- Single source of truth for UI
- Easier Compose recomposition
- Better readability

### Trade-offs
- Large UiState classes for complex screens

---

## 6. Auto-save Instead of Manual Save

### Context
Modern note apps do not require explicit save actions.

### Decision
Implement auto-save using Flow debounce.

### Rationale
- Better user experience
- Matches industry standards (Google Keep, Notion)
- Reduces user friction

### Trade-offs
- Requires careful handling of duplicate inserts
- Needs debounce to avoid excessive DB writes

---

## 7. Debounced Auto-save Strategy

### Context
Saving on every keystroke causes performance issues.

### Decision
Use:
- debounce(500ms)
- distinctUntilChanged

### Rationale
- Efficient database usage
- Smooth typing experience

### Trade-offs
- Slight delay before save

---

## 8. ID Stabilization After Insert

### Context
Auto-save initially created multiple notes.

### Decision
After first insert:
- Capture generated ID
- Store in UiState
- Use update for subsequent saves

### Rationale
- Ensures single note per session
- Prevents duplicate entries

### Trade-offs
- Slightly more complex ViewModel logic

---

## 9. Effects for One-Time Events

### Context
Navigation and UI events should not persist in state.

### Decision
Use Channel + receiveAsFlow for Effects.

### Rationale
- Prevents duplicate navigation
- Keeps UiState clean

### Trade-offs
- Requires separate handling from state

---

## 10. Koin for Dependency Injection

### Context
Need a simple DI framework for managing dependencies.

### Decision
Use Koin with constructor injection.

### Rationale
- Lightweight and easy to use
- No code generation
- Clean ViewModel injection

### Trade-offs
- Less compile-time safety compared to Dagger/Hilt

---

## 11. Avoid DataSource Layer (for now)

### Context
Only one data source exists (Room).

### Decision
Repository directly communicates with DAO.

### Rationale
- Avoid unnecessary abstraction
- Keep architecture simple

### Trade-offs
- DataSource layer will be needed when adding remote APIs

---

## 12. Compose-First UI

### Context
Modern Android UI development standard.

### Decision
Use Jetpack Compose for all UI.

### Rationale
- Declarative UI
- Better state handling
- Less boilerplate

### Trade-offs
- Learning curve
- Some APIs are still experimental

---

## 13. Navigation Without String Routes

### Context
String-based navigation is error-prone.

### Decision
Use @Serializable typed destinations.

### Rationale
- Type safety
- Compile-time validation
- Cleaner navigation logic

### Trade-offs
- Requires serialization setup

---

## 14. Experimental Material3 APIs

### Context
Some Material3 components are experimental.

### Decision
Use them with @OptIn(ExperimentalMaterial3Api).

### Rationale
- Access modern UI components
- Align with latest Android design

### Trade-offs
- Possible API changes in future updates

---