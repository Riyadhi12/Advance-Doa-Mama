package com.loc.newsapp.presentation.bookmark

import com.loc.newsapp.domain.model.Article

data class BookMarkState(
    val article: List<Article> = emptyList()
)
