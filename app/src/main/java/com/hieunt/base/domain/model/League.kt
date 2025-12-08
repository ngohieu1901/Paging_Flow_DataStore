package com.hieunt.base.domain.model

data class League(
    val id: Long,
    val name: String,
    val logoLeague: String,
    val listFixture: List<Fixture>,
    val isShowListFixture: Boolean
)