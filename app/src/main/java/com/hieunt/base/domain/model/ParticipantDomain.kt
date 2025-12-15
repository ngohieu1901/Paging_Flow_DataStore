package com.hieunt.base.domain.model

data class ParticipantDomain(
    val id: Long,
    val sportId: Long,
    val countryId: Long,
    val logoTeam: String,
)