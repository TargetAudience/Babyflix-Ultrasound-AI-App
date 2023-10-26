package com.babyfilx.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.babyfilx.data.models.response.HomeEntriesModel
import com.babyflix.mobileapp.R
import com.babyflix.mobileapp.databinding.RvVideoItemBinding

class GenericRecyclerViewAdapter(val onClick: (HomeEntriesModel,Int) -> Unit) :
    ListAdapter<HomeEntriesModel, GenericRecyclerViewAdapter.MyViewHolder>(DiffUtilCall()) {

    class MyViewHolder(val binding: RvVideoItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            RvVideoItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.apply {
            val model = getItem(position)
            videoThum.load(model.thumb_url){
                diskCachePolicy(CachePolicy.ENABLED)
                memoryCachePolicy(CachePolicy.ENABLED)
                placeholder(R.drawable.no_media)
                error(R.drawable.no_thumbnail)
                crossfade(true)
            }

            root.setOnClickListener {
                onClick(model,position)
            }
        }
    }


    class DiffUtilCall : DiffUtil.ItemCallback<HomeEntriesModel>() {
        override fun areItemsTheSame(
            oldItem: HomeEntriesModel,
            newItem: HomeEntriesModel
        ): Boolean = oldItem.node_id == newItem.node_id

        override fun areContentsTheSame(
            oldItem: HomeEntriesModel,
            newItem: HomeEntriesModel
        ): Boolean = oldItem == newItem

    }
}