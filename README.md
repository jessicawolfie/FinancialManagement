# 💰 Financial Management

Personal finance control Android application developed as part of **Android Training — Challenge 3**.

---

## 📱 Description

**Financial Management** is an app that allows users to register, view, and analyze income and expenses with local data persistence. The main goal of this challenge was the transition from in-memory storage to a real on-device database using **Room Database**.

### Implemented Features

- Register income and expense transactions with category, account, date, and notes
- View current balance, total income, and total expenses on the Dashboard
- Transaction list with filter by type (All / Income / Expenses)
- Edit and delete existing transactions
- Reports with total spent per category and percentage progress bar
- Default data automatically populated on first launch (categories and accounts)
- Full data persistence with Room Database

---

## 🗂 Project Structure

```
com.jesscafezeiro.financialmanagement
│
├── data/
│   ├── entity/
│   │   ├── Account.kt            → accounts table
│   │   ├── Category.kt           → categories table (Income/Expense)
│   │   └── Transaction.kt        → transactions table with ForeignKeys
│   │
│   ├── dao/
│   │   ├── AccountDao            → Account queries
│   │   ├── CategoryDao           → Category queries
│   │   └── TransactionDao.kt     → Transaction queries (SUM, GROUP BY, LIMIT)
│   │
│   ├── db/
│   │   └── AppDatabase           → database setup, Singleton, Migration
│   │
│   └── repository/
│       └── FinancialRepository   → data access layer, isolates database from UI
│
├── navigation/
│   └── Routes                    → app routes object/sealed classes
│
├── ui/
│   ├── dashboard/
│   │   ├── DashboardScreen.kt    → financial summary screen
│   │   ├── DashboardViewModel.kt → dashboard state with combined Flows
│   │   └── formatCurrency.kt     → currency formatting utility
│   │
│   ├── reports/
│   │   ├── ReportsScreen.kt      → category report with progress bar
│   │   └── ReportsViewModel.kt   → grouping and percentage calculation
│   │
│   ├── splash/
│   │   └── SplashScreen.kt       → initial screen with fade animation
│   │
│   ├── theme/
│   │   ├── Color.kt              → app color palette
│   │   ├── Theme.kt              → MaterialTheme with color scheme
│   │   └── Type.kt               → typography
│   │
│   └── transactions/
│       ├── FormScreen.kt         → create and edit transactions
│       ├── FormViewModel.kt      → form validation and persistence
│       ├── TransactionsScreen.kt → list with filters
│       └── TransactionsViewModel.kt → filter logic by type
│
├── util/
│   └── Converters.kt             → TypeConverter Date ↔ Long for Room
│
├── FinancialApplication          → database initialization and default data
└── MainActivity.kt               → NavHost + Bottom Navigation
```
---

## 🧠 Technologies

| Technology | Version | Usage |
|---|---|---|
| Kotlin | 2.0.21 | Main language |
| Jetpack Compose | BOM 2024.09 | Declarative UI |
| Room Database | 2.6.1 | Local database |
| KSP | 2.0.21-1.0.28 | Room annotation processing |
| Navigation Compose | 2.7.7 | Screen navigation |
| ViewModel | 2.7.0 | State management |
| Kotlin Coroutines | 1.7.3 | Async operations |
| Kotlin Flow | - | Reactive streams |
| Material 3 | - | UI components |

---

## Screenshots



---

## 🏗 Technical Decisions

### 1. Jetpack Compose instead of XML
The UI was built 100% with **Jetpack Compose**, eliminating the need for XML layout files, `ViewBinding`, and `RecyclerView Adapters`. Each screen is a `@Composable` function, making the code more concise and declarative.

### 2. KSP instead of KAPT
The project uses **KSP (Kotlin Symbol Processing)** to process Room annotations, replacing KAPT. KSP is faster, uses less memory, and is the recommended approach for modern Kotlin projects.

### 3. MVVM Architecture with Repository Pattern
The app follows the **MVVM (Model-View-ViewModel)** architecture recommended by Google:
- The **UI** (Compose) observes the ViewModel state and never accesses the database directly
- The **ViewModel** exposes `StateFlow` with the screen state and delegates operations to the Repository
- The **Repository** centralizes data access and isolates DAOs from the UI layer
- **DAOs** declare SQL queries that Room implements automatically

### 4. StateFlow + collectAsState
Screen state is managed with `StateFlow`, which always holds a current value. In the UI, `collectAsState()` converts the `StateFlow` into a Compose `State`, ensuring automatic recomposition whenever data changes.

### 5. Reactive Flow in DAOs
DAOs return `Flow<T>` instead of simple values. This ensures the UI updates automatically whenever the database is modified, without manually reloading data.

### 6. Migration
The database was created at **version 1** without the `notes` field in the transactions table. A **Migration 1→2** was implemented to add this field via `ALTER TABLE`, simulating a real schema evolution scenario without data loss.

### 7. TypeConverter for Date
Room does not natively support `java.util.Date`. The `Converters` class implements `Date ↔ Long` conversion (Unix timestamp in milliseconds), registered in `AppDatabase` via `@TypeConverters`.

### 8. Singleton in AppDatabase
The database is instantiated only once using the **Singleton** pattern with `@Volatile` and a `synchronized` block, ensuring thread-safety and preventing multiple simultaneous connections.

### 9. Initial Data with first()
On the first app launch, default categories and accounts are automatically inserted. The check uses `.first()` on the Flow to read the current database state once, without maintaining an unnecessary active collection.

### 10. Conditional Bottom Navigation
The bottom navigation bar is only displayed on the main screens (Dashboard, Transactions, and Reports). It is hidden on the Splash and Form screens by checking the current route via `currentBackStackEntryAsState()`.

---

## 🗃 Database Structure

```
┌─────────────┐         ┌──────────────────────┐         ┌──────────────┐
│  accounts   │         │     transactions      │         │  categories  │
│─────────────│         │──────────────────────│         │──────────────│
│ id (PK)     │◄────────│ accountId (FK)        │────────►│ id (PK)      │
│ name        │         │ categoryId (FK)        │         │ name         │
└─────────────┘         │ id (PK)               │         │ type         │
                        │ description           │         └──────────────┘
                        │ amount                │
                        │ type                  │
                        │ date                  │
                        │ notes (nullable)      │
                        └──────────────────────┘
```

### Relationships
- **1 Account → N Transactions** (ForeignKey with CASCADE)
- **1 Category → N Transactions** (ForeignKey with CASCADE)

---

## 🚀 How to Run

1. Clone the repository
2. Open in **Android Studio Hedgehog** or later
3. Wait for Gradle sync
4. Run on an emulator or device with **Android 8.0+ (API 26)**

---

## ✅ Challenge Requirements Met

- [x] Room Database with Entities, DAOs and AppDatabase
- [x] TypeConverter for Date type
- [x] Full CRUD (Create, Read, Update, Delete)
- [x] 1-N relationships between entities
- [x] Migration implemented (version 1 → 2)
- [x] Aggregate queries (SUM, GROUP BY)
- [x] Database isolated from UI layer via Repository
- [x] 5 screens: Splash, Dashboard, Transactions, Form, and Reports
- [x] Transaction filter by type
- [x] Category-based report

---

*Developed by Jessica Cafezeiro as part of Android Training — Challenge 3*
