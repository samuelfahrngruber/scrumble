package com.spogss.scrumble.viewItem

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import android.view.View
import com.woxthebox.draglistview.DragItem
import android.widget.TextView
import com.spogss.scrumble.R
import android.widget.FrameLayout
import android.view.animation.DecelerateInterpolator
import android.animation.ObjectAnimator


class CustomDragItem(private val context: Context, res: Int): DragItem(context, res) {

    override fun onBindDragView(clickedView: View, dragView: View) {
        val text = (clickedView.findViewById(R.id.column_item_text_view) as TextView).text
        (dragView.findViewById(R.id.column_item_text_view) as TextView).text = text
        val dragCard = dragView.findViewById<CardView>(R.id.card_view_item)
        val clickedCard = clickedView.findViewById<CardView>(R.id.card_view_item)

        dragCard.maxCardElevation = 40f
        dragCard.cardElevation = clickedCard.cardElevation
        // I know the dragView is a FrameLayout and that is why I can use setForeground below api level 23
        dragCard.foreground = ContextCompat.getDrawable(context, R.drawable.sc_card_view_selector)
        dragCard.setCardBackgroundColor(clickedCard.cardBackgroundColor)
    }

    override fun onMeasureDragView(clickedView: View, dragView: View) {
        val dragCard = dragView.findViewById<CardView>(R.id.card_view_item)
        val clickedCard = clickedView.findViewById<CardView>(R.id.card_view_item)
        val widthDiff = dragCard.paddingLeft - clickedCard.paddingLeft + dragCard.paddingRight - clickedCard.paddingRight
        val heightDiff = dragCard.paddingTop - clickedCard.paddingTop + dragCard.paddingBottom - clickedCard.paddingBottom
        val width = clickedView.measuredWidth + widthDiff
        val height = clickedView.measuredHeight + heightDiff
        dragView.layoutParams = FrameLayout.LayoutParams(width, height)

        val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        dragView.measure(widthSpec, heightSpec)
    }

    override fun onStartDragAnimation(dragView: View?) {
        val dragCard = dragView!!.findViewById<CardView>(R.id.card_view_item)
        val anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.cardElevation, 40f)
        anim.interpolator = DecelerateInterpolator()
        anim.duration = DragItem.ANIMATION_DURATION.toLong()
        anim.start()
    }

    override fun onEndDragAnimation(dragView: View?) {
        val dragCard = dragView!!.findViewById<CardView>(R.id.card_view_item)
        val anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.cardElevation, 6f)
        anim.interpolator = DecelerateInterpolator()
        anim.duration = DragItem.ANIMATION_DURATION.toLong()
        anim.start()
    }
}