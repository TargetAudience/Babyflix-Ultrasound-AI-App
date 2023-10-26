package com.babyfilx.ui.screens.selectPlan

import android.content.Context
import com.babyfilx.data.repositories.SelectPlanRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext


@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {
    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }



    @Provides
    fun provideSelectPlanViewModelFactory(
        repository: SelectPlanRepository, context: Context
    ): SelectPlanViewModelFactory {
        return SelectPlanViewModelFactory(context, repository)
    }
}

