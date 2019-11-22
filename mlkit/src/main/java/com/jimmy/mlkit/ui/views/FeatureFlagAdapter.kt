package com.jimmy.mlkit.ui.views

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jimmy.actions.featureflags.Feature
import com.jimmy.actions.featureflags.FeatureFlagProvider
import com.jimmy.mlkit.R
import kotlinx.android.synthetic.main.item_featureflag.view.*

/**
 * show all of the Features
 *
 * FeatureFlags and TestSettings had to be enums, because this allows to pass the FeatureFlagAdapter
 * either FeatureFlag.values() or TestSetting.values() and automatically generate the UI for all
 * defined FeatureFlags or TestSettings
 */
class FeatureFlagAdapter<T : Feature>(
    val items: Array<T>,
    val provider: FeatureFlagProvider,
    val checkedListener: (Feature, Boolean, Unit) -> Unit
) : RecyclerView.Adapter<FeatureFlagAdapter.FeatureFlagViewHolder<T>>() {



    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: FeatureFlagViewHolder<T>, position: Int) =
        holder.bind(items[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureFlagViewHolder<T> {
        val itemView_ = LayoutInflater.from(parent.context).inflate(R.layout.item_featureflag, parent, false)
        return FeatureFlagViewHolder(itemView_, provider, checkedListener)
    }

    class FeatureFlagViewHolder<T : Feature>(view: View, val provider: FeatureFlagProvider,
        val checkedListener: (Feature, Boolean, Unit)-> Unit) : RecyclerView.ViewHolder(view) {

        fun bind(feature: T) {
            itemView.title.text = feature.title
            itemView.description.text = feature.explanation
            itemView.switch_.isChecked = provider.isFeatureEnabled(feature)
            itemView.switch_.setOnCheckedChangeListener { switch, _ ->
                if (switch.isPressed) checkedListener }
        }
    }
}
