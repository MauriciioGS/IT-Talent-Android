package mx.mauriciogs.ittalent.data.useraccount.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mx.mauriciogs.ittalent.data.useraccount.local.UserDao
import mx.mauriciogs.ittalent.data.useraccount.local.UserDatabase
import mx.mauriciogs.ittalent.data.useraccount.local.UserLocalDataSrc
import mx.mauriciogs.ittalent.data.useraccount.local.UserRepositoryLocal

@Module
@InstallIn(SingletonComponent::class)
class UserAccountModule {

    @Provides
    fun provideUserRoomDatabase(application: Application) = UserDatabase.getDatabase(application)

    @Provides
    fun provideUserDao(userRoomDatabase: UserDatabase) = userRoomDatabase.userDao

    @Provides
    fun provideUserLocalDataSource(userDao: UserDao) = UserLocalDataSrc(userDao)

    @Provides
    fun provideUserRepository(userDataSource: UserLocalDataSrc) = UserRepositoryLocal(userDataSource)
}