package com.dicoding.storyapp.data.repository

import androidx.paging.*
import com.dicoding.storyapp.data.model.ListStory
import com.dicoding.storyapp.data.network.api.ApiService
import com.dicoding.storyapp.data.remotemediator.StoryRemoteMediator
import com.dicoding.storyapp.database.StoryDatabase
import kotlinx.coroutines.flow.Flow

class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: ApiService) {

    fun getAllStories(token: String): Flow<PagingData<ListStory>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token), pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).flow
    }
}