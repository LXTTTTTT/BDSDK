package com.pancoit.mod_main.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewParent
import android.widget.LinearLayout
import androidx.annotation.IntRange
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

// T 数据类型，B ViewBinding 类
abstract class BaseRecyclerViewAdapter<T, B : ViewBinding> : RecyclerView.Adapter<BaseViewHolder>() {

    // 数据
    private var data: MutableList<T> = mutableListOf()

    // 这个项目用不上
    private lateinit var mHeaderLayout: LinearLayout  // 头布局
    private lateinit var mFooterLayout: LinearLayout  // 脚布局

    companion object {
        const val HEADER_VIEW = 0x10000111
        const val FOOTER_VIEW = 0x10000222
    }


// 接口 ------------------------------------------------------------------------------------
    var onItemClickListener: ((view: View, position: Int) -> Unit)? = null  // 点击
    var onItemLongClickListener: ((view: View, position: Int) -> Boolean) = { view, position -> false }  // 长按

    /**
     * 子类不可重载，如果有需要请重写[onCreateDefViewHolder]实现自定义ViewHolder
     * 或者重写[getViewBinding]传入布局，不需要创建ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val baseViewHolder: BaseViewHolder
        when (viewType) {
            // 头、脚、子布局
            HEADER_VIEW -> {
                val headerParent: ViewParent? = mHeaderLayout.parent
                if (headerParent is ViewGroup) {
                    headerParent.removeView(mHeaderLayout)
                }
                baseViewHolder = BaseViewHolder(mHeaderLayout)
            }
            FOOTER_VIEW -> {
                val headerParent: ViewParent? = mFooterLayout.parent
                if (headerParent is ViewGroup) {
                    headerParent.removeView(mFooterLayout)
                }
                baseViewHolder = BaseViewHolder(mFooterLayout)
            }
            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                baseViewHolder = onCreateDefViewHolder(layoutInflater, parent, viewType)
                bindViewClickListener(baseViewHolder)  // 设置点击事件
            }
        }
        return baseViewHolder
    }

    /**
     * 子类可以选择重载该方法，如果有需要可重写[onBindDefViewHolder]，点击事件调用super即可
     */
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder.itemViewType) {
            HEADER_VIEW, FOOTER_VIEW -> {
                return
            }
            else -> {
                //holder必须继承自BaseBindViewHolder
                if (holder is BaseBindViewHolder<*>) {
                    holder as BaseBindViewHolder<B>
                    val realPosition = position - headerLayoutCount
                    val item = getItem(realPosition)
                    item?.let {
                        onBindDefViewHolder(holder, it, realPosition)
                    }
                }
            }
        }
    }

    protected open fun bindViewClickListener(holder: BaseViewHolder) {
        onItemClickListener?.let {
            holder.itemView.setOnClickListener { v ->
                var position = holder.adapterPosition
                if (position == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }
                position -= headerLayoutCount
                it.invoke(holder.itemView, position)
            }
        }

        onItemLongClickListener?.let {
            holder.itemView.setOnLongClickListener { v ->
                var position = holder.adapterPosition
                if (position == RecyclerView.NO_POSITION) {
                    return@setOnLongClickListener false
                }
                position -= headerLayoutCount
                it.invoke(holder.itemView, position)
            }
        }
    }

    /**
     * 子类重写该方法绑定数据
     * 重写[onCreateDefViewHolder]即可实现不同ViewHolder传递
     */
    protected abstract fun onBindDefViewHolder(holder: BaseBindViewHolder<B>, item: T?, position: Int)

    /**
     * 子类不可重载该方法，如有需要请重写[getDefItemViewType]
     */
    // 子项类型
    override fun getItemViewType(position: Int): Int {
        return if (hasHeaderView() && position == headerViewPosition) {
            // 如果初始化了头布局并且位置是0就是头布局
            HEADER_VIEW
        } else if (hasFooterView() && position == footerViewPosition) {
            // 如果初始化了脚布局并且位置是末尾就是脚布局
            FOOTER_VIEW
        } else {
            val realPosition = if (hasHeaderView()) {
                position - 1
            } else {
                position
            }
            getDefItemViewType(realPosition)
        }
    }

    // 重写此方法，返回 ViewType
    protected open fun getDefItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    /**
     * 不要重写此方法，如果有需要请重写[getDefItemCount]
     */
    override fun getItemCount(): Int {
        return headerLayoutCount + getDefItemCount() + footerLayoutCount
    }


    // 重写此方法，返回数据量
    protected open fun getDefItemCount(): Int {
        return data.size
    }

    // 子类实现创建自定义ViewHolder，父类提供了LayoutInflater
    protected open fun onCreateDefViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder {
        return BaseBindViewHolder(getViewBinding(layoutInflater, parent, viewType))
    }

    // 获取子项的 ViewBinding ：子类实现ViewBinding，父类提供了LayoutInflater，可以根据不同的viewType传递不同的viewBinding
    abstract fun getViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): B

    /**
     * 添加HeaderView
     * @param view
     * @param index view在HeaderView中的位置
     * @return Int
     */
    fun addHeadView(view: View, index: Int = -1): Int {
        if (!this::mHeaderLayout.isInitialized) {
            mHeaderLayout = LinearLayout(view.context)
            mHeaderLayout.orientation = LinearLayout.VERTICAL
            mHeaderLayout.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }
        val childCount = mHeaderLayout.childCount
        var mIndex = index
        if (index < 0 || index > childCount) {
            mIndex = childCount
        }
        mHeaderLayout.addView(view, mIndex)
        if (mHeaderLayout.childCount == 1) {
            notifyItemInserted(headerViewPosition)
        }
        return mIndex
    }

    // 移除头布局
    fun removeHeadView(header: View) {
        if (hasHeaderView()) {
            mHeaderLayout.removeView(header)
            if (mHeaderLayout.childCount == 0) {
                notifyItemRemoved(headerViewPosition)
            }
        }
    }

    // 移除所有头布局
    fun removeAllHeadView() {
        if (hasHeaderView()) {
            mHeaderLayout.removeAllViews()
            notifyItemRemoved(headerViewPosition)
        }
    }


    // Heater 位置
    val headerViewPosition: Int = 0

    // 是否有头布局
    fun hasHeaderView(): Boolean {
        // 头布局已经初始化并且里面有内容
        return this::mHeaderLayout.isInitialized && mHeaderLayout.childCount > 0
    }


    // 头布局数量
    val headerLayoutCount: Int
        get() {
            return if (hasHeaderView()) {
                1
            } else {
                0
            }
        }

    // 头布局的子View数量
    val headerViewCount: Int
        get() {
            return if (hasHeaderView()) {
                mHeaderLayout.childCount
            } else {
                0
            }
        }


    // 获取头布局
    val headerBinding: LinearLayout?
        get() {
            return if (this::mHeaderLayout.isInitialized) {
                mHeaderLayout
            } else {
                null
            }
        }

    /**
     * 添加FooterView
     * @param view
     * @param index view在FooterView中的位置
     * @return Int
     */
    fun addFootView(view: View, index: Int = -1): Int {
        if (!this::mFooterLayout.isInitialized) {
            mFooterLayout = LinearLayout(view.context)
            mFooterLayout.orientation = LinearLayout.VERTICAL
            mFooterLayout.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }
        val childCount = mFooterLayout.childCount
        var mIndex = index
        if (index < 0 || index > childCount) {
            mIndex = childCount
        }
        mFooterLayout.addView(view, mIndex)
        if (mFooterLayout.childCount == 1) {
            notifyItemInserted(footerViewPosition)
        }
        return mIndex
    }

    // 移除脚布局
    fun removeFootView(header: View) {
        if (hasFooterView()) {
            mFooterLayout.removeView(header)
            if (mFooterLayout.childCount == 0) {
                notifyItemRemoved(footerViewPosition)
            }
        }
    }


    // 移除所有脚布局
    fun removeAllFootView() {
        if (hasFooterView()) {
            mFooterLayout.removeAllViews()
            notifyItemRemoved(footerViewPosition)
        }
    }

    // 是否有 FooterView
    fun hasFooterView(): Boolean {
        return this::mFooterLayout.isInitialized && mFooterLayout.childCount > 0
    }


    // 脚布局的位置是 头布局+子项数量
    val footerViewPosition: Int
        get() = headerLayoutCount + data.size

    // 脚布局数量
    val footerLayoutCount: Int
        get() {
            return if (hasFooterView()) {
                1
            } else {
                0
            }
        }


    // footerView的子View数量
    val footerViewCount: Int
        get() {
            return if (hasFooterView()) {
                mFooterLayout.childCount
            } else {
                0
            }
        }

    // 获取尾布局
    val footerBinding: LinearLayout?
        get() {
            return if (this::mFooterLayout.isInitialized) {
                mFooterLayout
            } else {
                null
            }
        }


    // 获取data
    fun getData(): MutableList<T> {
        return data
    }

    // 设置/更新列表数据
    fun setData(list: Collection<T>?) {
        this.data.clear()
        if (!list.isNullOrEmpty()) {
            this.data.addAll(list)
        }
        notifyDataSetChanged()
    }

    // 添加数据
    fun addAll(newList: Collection<T>?) {
        if (newList.isNullOrEmpty()) {
            return
        }
        val lastIndex = data.size + headerLayoutCount
        if (this.data.addAll(newList)) {
            notifyItemRangeInserted(lastIndex, newList.size)
        }
    }

    // 清空数据
    fun clear() {
        this.data.clear()
        notifyDataSetChanged()
    }

    // 获取指定item
    fun getItem(@IntRange(from = 0) position: Int): T? {
        if (position >= data.size) {
            return null
        }
        return data[position]
    }

    // 获取item位置，如果返回-1，表示不存在
    fun getItemPosition(item: T?): Int {
        return if ((item != null && data.isNotEmpty())) {
            data.indexOf(item)
        } else {
            -1
        }
    }

    // 更新某一个位置上的数据
    fun updateItem(@IntRange(from = 0) position: Int, data: T) {
        if (position >= this.data.size) {
            return
        }
        this.data[position] = data
        notifyItemChanged(position + headerLayoutCount)
    }

    // 在某一个位置上添加一条数据
    fun setItem(@IntRange(from = 0) position: Int, data: T) {
        //如果超出则添加到最后
        val realPosition = if (position > this.data.size) {
            this.data.size
        } else {
            position
        }
        this.data.add(realPosition, data)
        notifyItemInserted(realPosition + headerLayoutCount)
    }

    // 增加一条数据到末尾
    fun addItem(data: T) {
        this.data.add(data)
        notifyItemInserted(this.data.size - 1 + headerLayoutCount)
    }

    // 移除某个位置上的数据
    fun removeAt(@IntRange(from = 0) position: Int): T? {
        if (position >= data.size) {
            return null
        }
        val remove = this.data.removeAt(position)
        notifyItemRemoved(position + headerLayoutCount)
        return remove
    }

    // 移除某个item数据，-1表示不存在该条数据
    fun remove(data: T): Int {
        val index = this.data.indexOf(data)
        if (index != -1) {
            removeAt(index)
        }
        return index
    }

}