package com.pancoit.mod_main.Adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

open class BaseViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView)
open class BaseBindViewHolder<B : ViewBinding>(val binding: B) : BaseViewHolder(binding.root)