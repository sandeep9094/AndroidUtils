# 📦 Repository Pattern in Android (MVVM Architecture)

This example demonstrates how to structure an Android app using the **Repository Pattern** along with **MVVM (Model-View-ViewModel)** architecture. It uses a **Remote Data Source** (Retrofit) and **Local Data Source** (Room) to load and cache data efficiently.


## ✅ What You’ll Achieve:
- A `DataRepository` that hides data-fetching logic.
- It will first try **remote**, and **fallback to local** if there's an error (e.g., no internet).
- Clean architecture-friendly and future-scalable.

---

### 🧱 Step 1: Define Your Data Model

```kotlin
data class NewsArticle(
    val id: String,
    val title: String,
    val content: String
)
```


### 🌐 Step 2: Create Retrofit API (Remote)

```kotlin
interface NewsApiService {
    @GET("top-headlines")
    suspend fun getTopHeadlines(): List<NewsArticle>
}
```


### 💾 Step 3: Create Local DAO (Room)

```kotlin
@Dao
interface NewsDao {
    @Query("SELECT * FROM NewsArticle")
    suspend fun getAllArticles(): List<NewsArticle>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<NewsArticle>)
}
```

Make sure you have an Entity for Room:

```kotlin
@Entity
data class NewsArticleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String
)
```

Then map between `NewsArticleEntity ↔ NewsArticle`.


### 🔌 Step 4: Create Remote and Local Data Sources

#### ✅ RemoteDataSource.kt

```kotlin
class RemoteDataSource(private val api: NewsApiService) {
    suspend fun fetchTopHeadlines(): List<NewsArticle> {
        return api.getTopHeadlines()
    }
}
```

#### ✅ LocalDataSource.kt

```kotlin
class LocalDataSource(private val newsDao: NewsDao) {

    suspend fun getCachedArticles(): List<NewsArticle> {
        return newsDao.getAllArticles()
            .map { entity -> entity.toNewsArticle() }
    }

    suspend fun saveArticles(articles: List<NewsArticle>) {
        newsDao.insertAll(articles.map { it.toEntity() })
    }
}
```


### 🧠 Step 5: Create the DataRepository

```kotlin
class NewsRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) {

    suspend fun getNews(): List<NewsArticle> {
        return try {
            val remoteNews = remoteDataSource.fetchTopHeadlines()
            localDataSource.saveArticles(remoteNews) // Save for offline use
            remoteNews
        } catch (e: Exception) {
            // Network failed, fallback to local
            localDataSource.getCachedArticles()
        }
    }
}
```

> This way your app is resilient — **if API fails, it shows last cached news**.


### 💡 Step 6: Use It in ViewModel

```kotlin
class NewsViewModel(private val repository: NewsRepository) : ViewModel() {

    private val _news = MutableStateFlow<List<NewsArticle>>(emptyList())
    val news: StateFlow<List<NewsArticle>> = _news

    fun loadNews() {
        viewModelScope.launch {
            val articles = repository.getNews()
            _news.value = articles
        }
    }
}
```


### ✅ Summary

| Layer               | Responsibility                           |
|--------------------|--------------------------------------------|
| `RemoteDataSource` | Get data from Retrofit API                |
| `LocalDataSource`  | Get/store data from Room DB               |
| `Repository`       | Glue logic – try remote, fallback to local |
| `ViewModel`        | Launch coroutine and expose UI state      |
