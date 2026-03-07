package com.example.hotelbooking.features.vouchers.di

import com.example.hotelbooking.features.vouchers.data.repository.VoucherRepositoryImpl
import com.example.hotelbooking.features.vouchers.domain.repository.VoucherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class VoucherModule {

    @Binds
    @Singleton
    abstract fun bindVoucherRepository(
        voucherRepositoryImpl: VoucherRepositoryImpl
    ): VoucherRepository
}