package mx.mauriciogs.ittalent.data.notification.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mx.mauriciogs.ittalent.data.notification.TokenRemoteDataSource

@Module
@InstallIn(SingletonComponent::class)
object NotiModule {

    @Provides
    fun provideTokenRemoteDataSource() = TokenRemoteDataSource()
}