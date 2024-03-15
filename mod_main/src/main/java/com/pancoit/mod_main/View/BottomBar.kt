package com.pancoit.mod_main.View

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

// 导航栏，横/纵
class BottomBar: View {

    val TAG = "BottomBar";

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    companion object{
        @JvmField
        val HORIZONTAL = 0;
        @JvmField
        val VERTICAL = 1;
    }

    var containerId = 0  // 存放 fragment 的控件id
    var firstCheckedIndex = 0  // 默认的页面项（第一个）
    var itemCount = 0  // 页面数量

    var fragmentList: MutableList<Fragment> = ArrayList()  // fragment 列表
    var titleList: MutableList<String> = ArrayList()  // 标题列表
    var iconResBeforeList: MutableList<Int> = ArrayList()  // 图标资源列表（选中前）
    var iconResAfterList: MutableList<Int> = ArrayList()  // 图标资源列表（选中后）
    val iconBitmapBeforeList: MutableList<Bitmap> = ArrayList()  // 图标图像列表（选中前）
    val iconBitmapAfterList: MutableList<Bitmap> = ArrayList()  // 图标图像列表（选中后）

    var fragmentClassList: MutableList<Class<*>> = ArrayList()  // fragment类 列表

    var buttomOrientation = HORIZONTAL  // 方向
    var titleSizeInDp = 12  // 标题大小
    var iconWidth = 22  // 图标宽度
    var iconHeight = 22  // 图标高度
    var titleIconMargin = 2  // 图标和标题的间距
    var titleColorBefore = Color.parseColor("#515151")  // 标题选中前的颜色
    var titleColorAfter = Color.parseColor("#ff2704")  // 标题选中后的颜色
    var currentCheckedIndex = 0  // 当前选中项数

    val paint = Paint()  // 画图
    val iconRectList: MutableList<Rect> = ArrayList()  // 图标的图像空间


    // 设置存放 fragment 的控件 id（FrameLayout）
    fun setContainer(containerId: Int): BottomBar {
        this.containerId = containerId
        return this
    }

    fun getCurrentFragmentByIndex(index: Int): Fragment {
        return fragmentList[index]
    }

    @JvmName("getCurrentFragment1")
    fun getCurrentFragment(): Fragment? {
        return currentFragment
    }

    @JvmName("getCurrentCheckedIndex1")
    fun getCurrentCheckedIndex(): Int {
        return currentCheckedIndex
    }

// 接口 --------------------------------------------
    var switchListener: OnSwitchListener? = null
    interface OnSwitchListener {
        fun result(position:Int,currentFragment: Fragment?)
    }
    fun setOnSwitchListener(listener:OnSwitchListener) {
        this.switchListener=listener
    }

    // 设置方向
    fun setOrientation(orientation:Int):BottomBar{
        this.buttomOrientation=orientation
        return this
    }

    // 设置 选中/未选中 的标题颜色
    fun setTitleBeforeAndAfterColor(beforeResCode: String?, AfterResCode: String?): BottomBar { //支持"#333333"这种形式
        titleColorBefore = Color.parseColor(beforeResCode)
        titleColorAfter = Color.parseColor(AfterResCode)
        return this
    }

    // 设置标题大小
    fun setTitleSize(titleSizeInDp: Int): BottomBar? {
        this.titleSizeInDp = titleSizeInDp
        return this
    }

    // 设置图标的宽度
    fun setIconWidth(iconWidth: Int): BottomBar? {
        this.iconWidth = iconWidth
        return this
    }

    // 设置图标的高度
    fun setIconHeight(iconHeight: Int): BottomBar? {
        this.iconHeight = iconHeight
        return this
    }

    // 设置图标和标题的间距
    fun setTitleIconMargin(titleIconMargin: Int): BottomBar {
        this.titleIconMargin = titleIconMargin
        return this
    }

    // 添加页面（先初始化 fragment 再添加）
    fun addItem(fragment: Fragment, title: String, iconResBefore: Int, iconResAfter: Int): BottomBar {
        fragmentList.add(fragment)  // 添加fragment
        titleList.add(title)  // 添加标题
        iconResBeforeList.add(iconResBefore)  // 添加选中前图标资源
        iconResAfterList.add(iconResAfter)  // 添加选中后图标资源
        return this
    }

    // 添加页面（添加fragment类）
    fun addItem(fragmentClass: Class<*>, title: String, iconResBefore: Int, iconResAfter: Int): BottomBar? {
        fragmentClassList.add(fragmentClass)
        titleList.add(title)
        iconResBeforeList.add(iconResBefore)
        iconResAfterList.add(iconResAfter)
        return this
    }

    // 设置首选项
    fun setFirstChecked(firstCheckedIndex: Int): BottomBar {
        this.firstCheckedIndex = firstCheckedIndex
        return this
    }

    // 构造 （用于先初始化 fragment 再添加）
    fun buildWithEntity() {
        itemCount = fragmentList.size
        // 预创建bitmap的Rect并缓存
        // 预创建icon的Rect并缓存
        for (i in 0 until itemCount) {
            val beforeBitmap: Bitmap = getBitmap(iconResBeforeList[i])!!
            iconBitmapBeforeList.add(beforeBitmap)
            val afterBitmap: Bitmap = getBitmap(iconResAfterList[i])!!
            iconBitmapAfterList.add(afterBitmap)
            val rect = Rect()  // 创建矩形空间
            iconRectList.add(rect)
        }
//        initParamHorizontal()
        currentCheckedIndex = firstCheckedIndex
        switchFragment(currentCheckedIndex)
        invalidate()
    }

    // 构造 （用于添加fragment类，通过反射创建对象如果fragment初始化需要传参的话不适用）
    fun buildWithClass() {
        itemCount = fragmentClassList.size
        // 预创建bitmap的Rect并缓存
        // 预创建icon的Rect并缓存
        for (i in 0 until itemCount) {
            val beforeBitmap: Bitmap = getBitmap(iconResBeforeList[i])!!
            iconBitmapBeforeList.add(beforeBitmap)
            val afterBitmap: Bitmap = getBitmap(iconResAfterList[i])!!
            iconBitmapAfterList.add(afterBitmap)
            val rect = Rect()
            iconRectList.add(rect)

            val clx = fragmentClassList[i]
            try {
                val fragment = clx.newInstance() as Fragment
                fragmentList.add(fragment)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
//        initParamHorizontal()
        currentCheckedIndex = firstCheckedIndex
        switchFragment(currentCheckedIndex)
        invalidate()
    }

    // 通过资源获取图像
    private fun getBitmap(resId: Int): Bitmap? {
        val bitmapDrawable = context!!.resources.getDrawable(resId) as BitmapDrawable
        return bitmapDrawable.bitmap
    }

    private var parentItemWidth = 0  // 单个选项的宽度
    private var titleBaseLine = 0  // 第一个标题的基线（水平时都一样）
    private val titleXList : MutableList<Int> = ArrayList()  // 每个标题的x轴起点

    private var parentItemHeight = 0  // 单个选项的高度
    private val titleBaseLines : MutableList<Int> = ArrayList()  // 每个标题的基线（纵向时）

    // 初始化参数
    private fun initParamHorizontal() {
        if (itemCount != 0) {
            // 单个选项宽高
            parentItemWidth = getWidth() / itemCount  // 计算单个选项的宽度：view 的总宽度/页面数
            parentItemHeight = getHeight()  // view 的高度就是 item 的高度
            // 图标宽高
            val iconWidth: Int = dp2px(iconWidth.toFloat())
            val iconHeight: Int = dp2px(iconHeight.toFloat())
            // 图标和标题 margin
            val textIconMargin: Int = dp2px(titleIconMargin.toFloat() / 2)  // 先指定5dp，这里除以一半才是正常的 margin，不知道为啥，可能是图片的原因
            // 标题高度
            val titleSize: Int = dp2px(titleSizeInDp.toFloat())

            paint.textSize = titleSize.toFloat()
            val rect = Rect()
            paint.getTextBounds(titleList[0], 0, titleList[0].length, rect)  // 绘制文本之前确定文本的占用空间
            val titleHeight = rect.height()

            // 计算得出图标的起始 top 坐标、文本的 baseLine
            val iconTop = (parentItemHeight - iconHeight - textIconMargin - titleHeight) / 2  // 总高度-图标高度-图标标题间距-标题的高度
            titleBaseLine = parentItemHeight - iconTop

            // 对icon的rect的参数进行赋值
            val firstRectX = (parentItemWidth - iconWidth) / 2 // 第一个icon的左起始点
            // 依次计算每个图标的左上右下
            for (i in 0 until itemCount) {
                val rectX = i * parentItemWidth + firstRectX
                val temp = iconRectList[i]
                temp.left = rectX
                temp.top = iconTop
                temp.right = rectX + iconWidth
                temp.bottom = iconTop + iconHeight
            }
            titleXList.clear()
            // 依次计算每个标题的x轴起点
            for (i in 0 until itemCount) {
                val title = titleList[i]
                paint.getTextBounds(title, 0, title.length, rect)  // 绘制文本之前确定文本的占用空间，将空间数据存储在rect
                titleXList.add( ((parentItemWidth - rect.width())/2) + (parentItemWidth*i) )  // (总宽度-文本长度)/2
            }
        }
    }

    private fun initParamVertical() {
        if (itemCount != 0) {
            // 单个选项宽高
            parentItemHeight = getHeight() / itemCount  // 计算单个选项的高度：view 的总宽度/页面数
            parentItemWidth = getWidth()  // view 的宽度就是 item 的宽度
            // 图标宽高
            val iconWidth: Int = dp2px(iconWidth.toFloat())
            val iconHeight: Int = dp2px(iconHeight.toFloat())
            // 图标和标题 margin
            val textIconMargin: Int = dp2px(titleIconMargin.toFloat() / 2)  // 先指定5dp，这里除以一半才是正常的 margin，不知道为啥，可能是图片的原因
            // 标题高度
            val titleSize: Int = dp2px(titleSizeInDp.toFloat())

            paint.textSize = titleSize.toFloat()
            val rect = Rect()
            paint.getTextBounds(titleList[0], 0, titleList[0].length, rect)  // 绘制文本之前确定文本的占用空间
            val titleHeight = rect.height()

            // 依次计算每个标题的基线
            val iconTop = (parentItemHeight - iconHeight - textIconMargin - titleHeight) / 2  // 总高度-图标高度-图标标题间距-标题的高度
            titleBaseLine = parentItemHeight - iconTop
            titleBaseLines.clear()
            for (i in 0 until itemCount){
                titleBaseLines.add(titleBaseLine+(i*parentItemHeight))
            }
            // 对icon的rect的参数进行赋值
            val firstRectX = (parentItemWidth - iconWidth) / 2 // 第一个icon的左起始点
            // 依次计算每个图标的左上右下
            for (i in 0 until itemCount) {
                val temp = iconRectList[i]
                temp.left = firstRectX
                temp.top = iconTop+(i*parentItemHeight)
                temp.right = firstRectX + iconWidth
                temp.bottom = iconTop+(i*parentItemHeight)+iconHeight
            }
            titleXList.clear()
            // 依次计算每个标题的x轴起点
            for (i in 0 until itemCount) {
                val title = titleList[i]
                paint.getTextBounds(title, 0, title.length, rect)  // 绘制文本之前确定文本的占用空间，将空间数据存储在rect
                titleXList.add( (parentItemWidth - rect.width())/2)  // (总宽度-文本长度)/2
            }
        }
    }

    fun dp2px(dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if(buttomOrientation== HORIZONTAL){
            initParamHorizontal()
        }else{
            initParamVertical()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas) //这里让view自身替我们画背景 如果指定的话
        if (itemCount != 0) {
            // 画图标
            paint.isAntiAlias = false  // 关闭抗锯齿
            for (i in 0 until itemCount) {
                var bitmap: Bitmap? = if (i==currentCheckedIndex) { iconBitmapAfterList[i] } else { iconBitmapBeforeList[i] }
                val rect = iconRectList[i]
                bitmap?.let { canvas.drawBitmap(it, null, rect, paint) }  // 把图像资源填充到对应的位置
            }
            // 画标题
            paint.isAntiAlias = true  // 打开抗锯齿
            for (i in 0 until itemCount) {
                val title = titleList[i]
                if (i == currentCheckedIndex) { paint.color = titleColorAfter } else { paint.color = titleColorBefore }
                if (titleXList.size == itemCount) {
                    val x = titleXList[i]
                    var y = if(buttomOrientation== HORIZONTAL) titleBaseLine.toFloat() else titleBaseLines[i].toFloat()
                    canvas.drawText(title, x.toFloat(), y, paint)
                }
            }
        }
    }

    // 我观察了微博和掌盟，发现 按下 和 松开 时都在同一个区域内才响应，如果按下这个区域后手指挪出去再松开则不响应
    var target = -1
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            // 按下
            MotionEvent.ACTION_DOWN -> {
                var x = event.x.toInt()
                var y = event.y.toInt()
                var number = if(buttomOrientation== HORIZONTAL) withinWhichArea(x) else withinWhichArea(y)
                Log.i(TAG, "点击x轴: "+ x + " / 点击y轴: " + y + "/" + number)
                target = number
            }
            // 松开
            MotionEvent.ACTION_UP -> {
                // 确保是在这个view之内
                if ( if(buttomOrientation== HORIZONTAL) event.y > 0 else event.x > 0 ) {
                    // 松开时还在这个按键内
                    if (if(buttomOrientation== HORIZONTAL) target == withinWhichArea(event.x.toInt()) else target == withinWhichArea(event.y.toInt()) ) {
                        switchFragment(target)  // 切换页面
                        currentCheckedIndex = target  // 修改当前选中项
                        invalidate()  // 刷新
                    }
                    target = -1
                }
            }
        }
        return true
        // 这里return super为什么up执行不到？是因为return super的值，全部取决于你是否 clickable，当你down事件来临，不可点击，所以return false，也就是说，而且你没有设置onTouchListener，并且控件是ENABLE的，所以dispatchTouchEvent的返回值也是false，所以在view group的dispatchTransformedTouchEvent也是返回false，这样一来，view group中的first touch target就是空的，所以intercept标记位果断为false，然后就再也进不到循环取子项的步骤了，直接调用dispatch-TransformedTouchEvent并传入子项为null，所以直接调用view group自身的dispatch-TouchEvent了
    }

    // 通过点击的x轴坐标计算当前点击的区域
//    private fun withinWhichArea(x: Int): Int { return x / parentItemWidth }
    val withinWhichArea = { x: Int -> if(buttomOrientation== HORIZONTAL) x / parentItemWidth else x / parentItemHeight}

    var currentFragment: Fragment? = null  // 当前的 fragmen 页面
    // 注意这里只支持 AndroidX 版本，旧版自行修改
    private fun switchFragment(whichFragment: Int) {
        val fragment = fragmentList[whichFragment]
        var transactionx = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        if (fragment.isAdded) {
            if (currentFragment != null) {
                transactionx.hide(currentFragment!!).show(fragment)
            } else {
                transactionx.show(fragment)
            }
        } else {
            if (currentFragment != null) {
                transactionx.hide(currentFragment!!).add(containerId, fragment)
            } else {
                transactionx.add(containerId, fragment)
            }
        }
        currentFragment = fragment
        transactionx.commit()
        switchListener?.result(whichFragment,currentFragment)
    }

    // 初始化参数
    fun clear() {
        firstCheckedIndex = 0
        itemCount = 0
        titleList.clear()
        iconResBeforeList.clear()
        iconResAfterList.clear()
        iconBitmapBeforeList.clear()
        iconBitmapAfterList.clear()
        fragmentClassList.clear()
        buttomOrientation = HORIZONTAL
        titleSizeInDp = 12
        iconWidth = 22
        iconHeight = 22
        titleIconMargin = 2
        currentCheckedIndex = 0
        iconRectList.clear()
        parentItemWidth = 0
        titleBaseLine = 0
        titleXList.clear()
        parentItemHeight = 0
        titleBaseLines.clear()
        target = -1

        if (fragmentList.size > 0) {
            val transaction = (context as AppCompatActivity?)!!.supportFragmentManager.beginTransaction()
            for (fragment in fragmentList) {
                transaction.remove(fragment)
            }
            transaction.commit()
        }
        fragmentList.clear()
    }

}