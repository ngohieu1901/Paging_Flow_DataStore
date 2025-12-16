package com.hieunt.base.presentations.feature.main

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hieunt.base.databinding.ItemFixtureBinding
import com.hieunt.base.domain.model.FixtureDomain
import com.hieunt.base.widget.layoutInflate
import com.hieunt.base.widget.loadImage

class FixturePagingAdapter: PagingDataAdapter<FixtureDomain, FixturePagingAdapter.FixtureVH>(FIXTURE_DIFF_CALLBACK) {
    inner class FixtureVH(private val binding: ItemFixtureBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: FixtureDomain) {
            binding.apply {
                tvStatus.text = data.state?.name

                loadImage(ivLogoTeam1, data.participants[0].logoTeam)
                loadImage(ivLogoTeam2, data.participants[1].logoTeam)

                tvNameTeam1.text = data.participants[0].name
                tvNameTeam2.text = data.participants[1].name

                if (data.state?.state != "NS") {
                    if (data.participants.isNotEmpty()) {
                        var scoreOfTeam1 = 0
                        data.scores.forEach {
                            if (it.participantId == data.participants[0].id) {
                                if (scoreOfTeam1 < it.score.goals) {
                                    scoreOfTeam1 = it.score.goals
                                }
                            }
                        }
                        tvScoreTeam1.text = scoreOfTeam1.toString()
                    }

                    if (data.participants.isNotEmpty()) {
                        var scoreOfTeam2 = 0
                        data.scores.forEach {
                            if (it.participantId == data.participants[1].id) {
                                if (scoreOfTeam2 < it.score.goals) {
                                    scoreOfTeam2 = it.score.goals
                                }
                            }
                        }
                        tvScoreTeam2.text = scoreOfTeam2.toString()
                    }
                } else {
                    tvScoreTeam1.text = "N"
                    tvScoreTeam2.text = "S"
                }
            }
        }
    }

    companion object {
        private val FIXTURE_DIFF_CALLBACK = object : DiffUtil.ItemCallback<FixtureDomain>() {
            override fun areItemsTheSame(oldItem: FixtureDomain, newItem: FixtureDomain): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FixtureDomain, newItem: FixtureDomain): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onBindViewHolder(holder: FixtureVH, position: Int) {
        holder.bind(data = getItem(position) ?: return)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FixtureVH = FixtureVH(
        ItemFixtureBinding.inflate(parent.layoutInflate(), parent, false))
}