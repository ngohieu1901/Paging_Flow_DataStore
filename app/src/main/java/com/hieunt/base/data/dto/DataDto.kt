package com.hieunt.base.data.dto


import com.hieunt.base.domain.model.FixtureDomain
import com.hieunt.base.domain.model.LeagueDomain
import com.hieunt.base.domain.model.ParticipantDomain
import com.hieunt.base.domain.model.ScoreDetailDomain
import com.hieunt.base.domain.model.ScoreDomain
import com.hieunt.base.domain.model.State
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DataFixtures(
    @Json(name = "data") val data: List<FixtureResponse>?,
    @Json(name = "timezone") val timezone: String
)

@JsonClass(generateAdapter = true)
data class FixtureResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "league") val league: LeagueResponse,
    @Json(name = "state") val state: StateResponse?,
    @Json(name = "participants") val participants: List<ParticipantResponse>?,
    @Json(name = "scores") val scores: List<ScoreResponse>?,
    @Json(name = "starting_at") val startingAt: String,
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
    @Json(name = "placeholder") val placeholder: Boolean
)

@JsonClass(generateAdapter = true)
data class ScoreResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "fixture_id") val fixtureId: Long,
    @Json(name = "type_id") val typeId: Long,
    @Json(name = "participant_id") val participantId: Long,
    @Json(name = "score") val score: ScoreDetailResponse,
    @Json(name = "description") val description: String
)

@JsonClass(generateAdapter = true)
data class ScoreDetailResponse(
    @Json(name = "goals") val goals: Int,
    @Json(name = "participant") val participant: String
)

fun ScoreResponse.toScoreDomain(): ScoreDomain {
    return ScoreDomain(
        id = this.id,
        fixtureId = this.fixtureId,
        typeId = this.typeId,
        participantId = this.participantId,
        score = this.score.toScoreDetailDomain(),
        description = this.description
    )
}

fun ScoreDetailResponse.toScoreDetailDomain(): ScoreDetailDomain {
    return ScoreDetailDomain(
        goals = this.goals,
        participant = this.participant
    )
}

fun FixtureResponse.toFixtureDomain(): FixtureDomain {
    return FixtureDomain(
        id = this.id,
        leagueDomain = this.league.toLeagueDomain(),
        state = this.state?.toStateDomain(),
        participants = this.participants?.map { it.toParticipantDomain() } ?: emptyList(),
        scores = this.scores?.map { it.toScoreDomain() } ?: emptyList(),
        startingAt = this.startingAt
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
    logoTeam = imagePath,
    name = name
)
