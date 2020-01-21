/*
 * Copyright (C) 2020 Stichting Akvo (Akvo Foundation)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.akvo.flow.mapbox.offline.reactive.example

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mapbox.mapboxsdk.offline.OfflineRegion
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus
import org.akvo.flow.mapbox.offline.reactive.RegionNameMapper

class AreasAdapter(
    private val pairs: MutableList<Pair<OfflineRegion, OfflineRegionStatus>> = mutableListOf(),
    private val nameMapper: RegionNameMapper,
    private val areaListener: AreaListener?
) : RecyclerView.Adapter<AreasAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
        return ViewHolder(textView, nameMapper, areaListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setTextView(pairs[position])
    }

    override fun getItemCount(): Int {
        return pairs.size
    }

    fun setRegions(pairs: List<Pair<OfflineRegion, OfflineRegionStatus>>) {
        this.pairs.clear()
        this.pairs.addAll(pairs)
        notifyDataSetChanged()
    }

    class ViewHolder(
        private val textView: TextView,
        private val nameMapper: RegionNameMapper,
        private val areaListener: AreaListener?
    ) :
        RecyclerView.ViewHolder(textView) {

        fun setTextView(offlineArea: Pair<OfflineRegion, OfflineRegionStatus>) {
            textView.text = nameMapper.getRegionName(offlineArea.first)
            textView.tag = offlineArea.first.id
            textView.setOnClickListener { areaListener?.rename(textView.tag as Long) }
            textView.setOnLongClickListener {
                areaListener?.delete(textView.tag as Long)
                return@setOnLongClickListener true
            }
        }
    }
}

interface AreaListener {

    fun rename(id: Long)

    fun delete(id: Long)
}
