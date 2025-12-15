package com.hieunt.base.data.dto

import com.hieunt.base.domain.model.FixtureDomain
import com.hieunt.base.domain.model.LeagueDomain
import com.hieunt.base.domain.model.ParticipantDomain
import com.hieunt.base.domain.model.State
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DataFixtures(
    @Json(name = "data") val data: List<FixtureResponse>,
    @Json(name = "timezone") val timezone: String
)

@JsonClass(generateAdapter = true)
data class FixtureResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "league") val league: LeagueResponse,
    @Json(name = "state") val state: StateResponse?,
    @Json(name = "participants") val participants: List<ParticipantResponse>?
)

@JsonClass(generateAdapter = true)
data class LeagueResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "image_path") val imagePath: String
)

@JsonClass(generateAdapter = true)
data class StateResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "state") val state: String,
    @Json(name = "name") val name: String,
    @Json(name = "short_name") val shortName: String,
    @Json(name = "developer_name") val developerName: String
)

@JsonClass(generateAdapter = true)
data class ParticipantResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "sport_id") val sportId: Long,
    @Json(name = "country_id") val countryId: Long,
    @Json(name = "venue_id") val venueId: Long?,
    @Json(name = "gender") val gender: String?,
    @Json(name = "name") val name: String,
    @Json(name = "short_code") val shortCode: String?,
    @Json(name = "image_path") val imagePath: String,
    @Json(name = "founded") val founded: Long?,
    @Json(name = "type") val type: String,
    @Json(name = "placeholder") val placeholder: Boolean,
    @Json(name = "last_played_at") val lastPlayedAt: String,
)

fun FixtureResponse.toFixtureDomain(): FixtureDomain {
    return FixtureDomain(
        id = this.id,
        leagueDomain = this.league.toLeagueDomain(),
        state = this.state?.toStateDomain(),
        participants = this.participants?.map { it.toParticipantDomain() } ?: emptyList()
    )
}

fun LeagueResponse.toLeagueDomain() = LeagueDomain(
    id = id,
    name = name,
    logoLeague = imagePath
)

fun StateResponse.toStateDomain() = State(
    id = id,
    state = state,
    name = name
)

fun ParticipantResponse.toParticipantDomain() = ParticipantDomain(
    id = id,
    sportId = sportId,
    countryId = countryId,
    logoTeam = imagePath
)
