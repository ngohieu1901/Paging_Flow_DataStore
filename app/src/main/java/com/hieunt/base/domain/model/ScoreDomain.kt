package com.hieunt.base.domain.model


data class ScoreDomain(
    val id: Long,
    val fixtureId: Long,
    val typeId: Long,
    val participantId: Long,
    val score: ScoreDetailDomain,
    val description: String
)