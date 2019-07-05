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
        }
    }
}

interface AreaListener {

    fun rename(id: Long)
}
