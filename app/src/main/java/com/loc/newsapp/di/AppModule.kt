package com.loc.newsapp.di

import android.app.Application
import androidx.room.Room
import com.loc.newsapp.data.local.Dao
import com.loc.newsapp.data.local.NewsDatabase
import com.loc.newsapp.data.local.NewsTypeConvertor
import com.loc.newsapp.data.manager.LocalUserManagerImpl
import com.loc.newsapp.data.remote.NewsApi
import com.loc.newsapp.data.repository.NewsRepositoryImpl
import com.loc.newsapp.domain.manager.LocalUserManager
import com.loc.newsapp.domain.repository.NewsRepository
import com.loc.newsapp.domain.usecases.app_entry.AppEntryUseCases
import com.loc.newsapp.domain.usecases.app_entry.ReadAppEntry
import com.loc.newsapp.domain.usecases.app_entry.SaveAppEntry
import com.loc.newsapp.domain.usecases.news.DeleteArticle
import com.loc.newsapp.domain.usecases.news.GetNews
import com.loc.newsapp.domain.usecases.news.NewsUseCases
import com.loc.newsapp.domain.usecases.news.SearchNews
import com.loc.newsapp.domain.usecases.news.SelectArticles
import com.loc.newsapp.domain.usecases.news.SelectedArticle
import com.loc.newsapp.domain.usecases.news.UpsertArticle
import com.loc.newsapp.util.Constant.BASE_URL
import com.loc.newsapp.util.Constant.NEWS_DATA_BASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLocalUserManager(
        application: Application
    ): LocalUserManager = LocalUserManagerImpl(application)

    @Provides
    @Singleton
    fun provideAppEntryUseCases(
        localUserManager: LocalUserManager
    ) = AppEntryUseCases(
        readAppEntry = ReadAppEntry(localUserManager),
        saveAppEntry = SaveAppEntry(localUserManager)
    )

    @Provides
    @Singleton
    fun provideNewsApi(): NewsApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNewsRepository(
        newsApi: NewsApi,
        dao: Dao
    ): NewsRepository = NewsRepositoryImpl(newsApi, dao)

    @Provides
    @Singleton
    fun provideNewUseCase(
        newsRepository: NewsRepository,
    ): NewsUseCases {
        return NewsUseCases(
            getNews = GetNews(newsRepository),
            searchNews = SearchNews(newsRepository),
            upsertArticle = UpsertArticle(newsRepository),
            deleteArticle = DeleteArticle(newsRepository),
            selectArticles = SelectArticles(newsRepository),
            selectedArticle = SelectedArticle(newsRepository)

        )
    }

    @Provides
    @Singleton
    fun providesNewsDataBase(application: Application): NewsDatabase{
        return Room.databaseBuilder(
            context = application,
            klass = NewsDatabase::class.java,
            name = NEWS_DATA_BASE_NAME
        ).addTypeConverter(NewsTypeConvertor())
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsDTO(newsDatabase: NewsDatabase): Dao = newsDatabase.newsDao
}