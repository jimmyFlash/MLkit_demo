package com.jimmy.actions.featureflags

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jimmy.mlkit.R


private class FeatureFlagAdapter<T : Feature>(
    val items: Array<T>,
    val provider: FeatureFlagProvider,
    val checkedListener: Function2<Feature, Boolean, Unit>
) : RecyclerView.Adapter<FeatureFlagAdapter.FeatureFlagViewHolder<T>>() {



    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: FeatureFlagViewHolder<T>, position: Int) =
        holder.bind(items[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureFlagViewHolder<T> {
        //val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_featureflag, parent, false)
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_google_lens, parent, false)
        return FeatureFlagViewHolder(itemView, provider, checkedListener)
    }

    private class FeatureFlagViewHolder<T : Feature>(
        view: View,
        val provider: FeatureFlagProvider,
        val checkedListener: Function2<Feature, Boolean, Unit>
    ) : RecyclerView.ViewHolder(view) {

        fun bind(feature: T) {
        /*    title.text = feature.title
            description.text = feature.explanation
            switch.isChecked = provider.isFeatureEnabled(feature)
            switch.setOnCheckedChangeListener { switch, isChecked ->
                if (switch.isPressed) checkedListener.invoke(feature, isChecked) }*/
        }
    }
}
