package com.masjidjalancahaya.kencelenganreminder.di

import android.app.Application
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.masjidjalancahaya.kencelenganreminder.data.source.remote.FirebaseAuthService
import com.masjidjalancahaya.kencelenganreminder.data.source.remote.FirebaseService
import com.masjidjalancahaya.kencelenganreminder.notifications.KencelNotificationScheduler
import com.masjidjalancahaya.kencelenganreminder.notifications.KencelNotificationSchedulerImpl
import com.masjidjalancahaya.kencelenganreminder.data.repository.Repository
import com.masjidjalancahaya.kencelenganreminder.data.repository.RepositoryImpl
import com.masjidjalancahaya.kencelenganreminder.utils.conversion.DateTimeConversion
import com.masjidjalancahaya.kencelenganreminder.utils.conversion.DateTimeConversionImpl
import com.masjidjalancahaya.kencelenganreminder.utils.conversion.ReminderTimeConversion
import com.masjidjalancahaya.kencelenganreminder.utils.conversion.ReminderTimeConversionImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Injection {

    @Provides
    @Singleton
    fun provideWorkManager(app: Application): WorkManager {
        return WorkManager.getInstance(app)
    }
    @Provides
    @Singleton
    fun provideDateTimeConversion(): DateTimeConversion {
        return DateTimeConversionImpl()
    }

    @Provides
    @Singleton
    fun provideReminderTimeConversion(): ReminderTimeConversion {
        return ReminderTimeConversionImpl()
    }
    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFireStore() = Firebase.firestore

    @Provides
    @Singleton
    fun provideFirebaseService(
        fireStore: FirebaseFirestore
    ) = FirebaseService(fireStore)

    @Provides
    @Singleton
    fun provideKencelNotifScheluder(
        context: Application
    ): KencelNotificationScheduler = KencelNotificationSchedulerImpl(context)

    @Provides
    @Singleton
    fun provideRepository(
        service: FirebaseService,
        serviceAuthService: FirebaseAuthService,
        scheduler: KencelNotificationScheduler
    ): Repository = RepositoryImpl(service, serviceAuthService, scheduler)
}