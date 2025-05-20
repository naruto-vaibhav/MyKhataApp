package com.naruto.managekhata.service.module

import com.naruto.managekhata.service.AccountService
import com.naruto.managekhata.service.StorageService
import com.naruto.managekhata.service.impl.AccountServiceImpl
import com.naruto.managekhata.service.impl.StorageServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

    @Binds
    abstract fun provideStorageService(impl: StorageServiceImpl): StorageService
}