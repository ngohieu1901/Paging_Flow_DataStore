package com.hieunt.base.domain.model

data class FixtureDomain(
    val id: Long,
    val leagueDomain: LeagueDomain,
    val state: State?,
    val participants: List<ParticipantDomain>,
    val scores: List<ScoreDomain>,
    val startingAt: String
)