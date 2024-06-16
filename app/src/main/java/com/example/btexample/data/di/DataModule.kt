package com.example.btexample.data.di

import android.app.Application
import com.example.btexample.data.BluetoothRepository
import com.example.btexample.domain.IBluetoothRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Singleton
    @Provides
    fun provideBluetoothRepository(application: Application): IBluetoothRepository =
        BluetoothRepository(
            application
        )
}