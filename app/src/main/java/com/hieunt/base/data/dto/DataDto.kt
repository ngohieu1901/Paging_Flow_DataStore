package com.hieunt.base.data.dto

data class DataFixtures(
    var data: MutableList<FixtureDto>,
    var timezone: String
)

data class FixtureDto(
    val id: Long,
    val leagueDto: LeagueDto
)

data class LeagueDto(
    val id: Long,
    var name: String,
    val image_path: String,
    val listFixture: MutableList<FixtureDto>,
)

data class StateDto(
    val id: Long,
    val state: String,
    val name: String,
    val short_name: String,
    val developer_name: String
)