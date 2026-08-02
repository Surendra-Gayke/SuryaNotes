package com.surendra.suryanotes.di

import com.surendra.suryanotes.data.repository.NoteRepositoryImpl
import com.surendra.suryanotes.domain.repository.NoteRepository
import com.surendra.suryanotes.ui.editor.EditorViewModel
import com.surendra.suryanotes.ui.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<NoteRepository> {

        NoteRepositoryImpl(
            noteDao = get()
        )

    }

    viewModel {

        HomeViewModel(
            noteRepository = get()
        )

    }

    viewModel {

        EditorViewModel()

    }

}