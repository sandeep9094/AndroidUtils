[<h4> Room Database </h4>](https://github.com/sandeep9094/AndroidUtils/tree/master/retrofit)

The Room persistence library provides an abstraction layer over SQLite to allow fluent database access while harnessing the full power of SQLite. In particular, Room provides the following benefits:
   - Compile-time verification of SQL queries.
-    Convenience annotations that minimize repetitive and error-prone boilerplate code.
 -   Streamlined database migration paths.

```sh
plugins {
    id 'kotlin-kapt'
}

dependencies {
    def room_version = "2.4.1"
    implementation "androidx.room:room-runtime:$room_version"
    implementation "androidx.room:room-ktx:$room_version"
    kapt "androidx.room:room-compiler:$room_version"
}
```

Below are the classes required for Working with Retrofit
1. AppDatabase
2. DatabaseDao

AppDatabase

```sh
@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getDao(): DatabaseDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null
        private const val DATABASE_NAME= "room_example_database"

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val roomInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigration().build()
                instance = roomInstance
                roomInstance
            }
        }

    }
}
```

DatabaseDao

```sh
@Dao
interface DatabaseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User)

    @Delete
    fun deleteUser(user: User)

    @Query("SELECT * from user_list")
    fun getAllUser(): LiveData<List<User>>
}
```

Usage

```sh

//Application Class
class MyApplicationClass: Application() {

    /**
     * Create instance of database in application class with lazy keyword. (lazy keyword allow only to create database only if database instance will access in application)
     */

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

}

// Activity to access Database via viewModel
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel : MainViewModel by viewModels {
        MainViewModelFactory((application as MyApplicationClass).database.getDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = User("Sandeep", "Kumar")
        viewModel.insertUser(user)

        viewModel.getList().observe(this) {
            Log.d(TAG, "User List: $it")
        }
    }
}

//ViewModel Class
class MainViewModel(val databaseDao: DatabaseDao) : ViewModel() {

    fun getList(): LiveData<List<User>> {
        return databaseDao.getAllUser()
    }

    fun insertUser(user: User) {
        viewModelScope.launch {
            databaseDao.insertUser(user)
        }
    }

}

//ViewModel Factory to access databaseDao in constructor of ViewModel
class MainViewModelFactory(private val databaseDao: DatabaseDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(databaseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

```