package com.tomgu.rawgcards.cardstack

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.Constraints
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.api.Game
import kotlinx.android.synthetic.main.card_layout.view.*

class MyCardStack(context: Context, attributeSet: AttributeSet): RelativeLayout(context, attributeSet){

    private var dX: Float = 0.toFloat()
    private var dY: Float = 0.toFloat()
    var game : String? = null

     var cardStackListener: CardStackListener? = null

     var cardViewList = listOf(CardView(context, Constraints.LayoutParams(900,1400)), CardView(context, Constraints.LayoutParams(900,1400)), CardView(context, Constraints.LayoutParams(900,1400)))

    private var gameList: MutableList<Game> = mutableListOf()

     fun setList(list: List<Game>){

         gameList = list as MutableList<Game>
         if(this.childCount <= 0){
             gameList.take(3)
                 .reversed()
                 .forEachIndexed { index, game ->
                 val card = cardViewList[index]
                 card.setCardGame(game)
                 addView(card)
                 gameList.remove(game)
                     if(index == 2){
                         card.image_content.transitionName = "image_trans"
                     }
             }
     }

     }

    fun resetCardStack(){
        this.removeAllViews()
        gameList.clear()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        val newX: Float
        val newY: Float
        val view = getChildAt(childCount - 1)

        if (view != null) {
            val imageRejected = view.findViewById<ImageView>(R.id.rejected_image)
            val imageApproved = view.findViewById<ImageView>(R.id.approved_image)
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY

                    view.animate().cancel()
                }
                MotionEvent.ACTION_UP -> {

                    val childEndPositionX = view.x + (view.width / 2)
                    val childEndPostionY = view.y + (view.height / 2)
                    var corner: Float = 0f

                    if (childEndPositionX > this.width - (this.width / 4)) {

                        Log.d("TGIW", "GAME SAVED")
                        if (childEndPostionY > this.height / 2) {
                            corner = +(this.height).toFloat()

                        } else {
                            corner = -(this.height).toFloat()
                        }

                        view.animate()
                            .x((view.width + (view.width / 3)).toFloat())
                            .y(corner)
                            .withEndAction {
                                cardStackListener?.onApproved((view as CardView).viewGame!!)
                                cardStackListener?.viewCountRemain(gameList.count())

                                this.removeView(view)
                                (view as CardView).setCardGame(gameList[0])
                                this.addView(view, 0)
                                gameList.removeAt(0)
                                view.x = (this.width / 2).toFloat() - view.width / 2
                                view.y = (this.height / 2).toFloat() - view.height / 2
                                view.rotation = 0f
                                view.rejected_image.alpha = 0f
                                view.approved_image.alpha = 0f
                            }
                            .setDuration(300)
                            .start()

                    } else if (childEndPositionX < 0 + (this.width / 4)) {

                        Log.d("TGIW", "GAME REMOVED")
                        if (childEndPostionY > this.height / 2) {
                            corner = +(this.height).toFloat()
                        } else {
                            corner = -(this.height).toFloat()
                        }

                        view.animate()
                            .x((-view.width).toFloat())
                            .y(corner)
                            .withEndAction {
                                cardStackListener?.onRejected((view as CardView).viewGame!!)
                                cardStackListener?.viewCountRemain(gameList.count())

                                this.removeView(view)
                                (view as CardView).setCardGame(gameList[0])
                                this.addView(view, 0)
                                gameList.removeAt(0)
                                view.x = (this.width / 2).toFloat() - view.width / 2
                                view.y = (this.height / 2).toFloat() - view.height / 2
                                view.rotation = 0f
                                view.rejected_image.alpha = 0f
                                view.approved_image.alpha = 0f
                            }
                            .setDuration(300)
                            .start()

                    } else {
                        view.animate()
                            .x((this.width / 2).toFloat() - view.width / 2)
                            .y((this.height / 2).toFloat() - view.height / 2)
                            .withEndAction {
                                view.rejected_image.alpha = 0f
                                view.approved_image.alpha = 0f
                            }
                            .rotation(0f)
                            .setDuration(300)
                            .start()
                    }
                }
                MotionEvent.ACTION_MOVE -> {

                    val childEndPositionX = view.x + (view.width / 2)

                    val totalDragRange = this.width / 4
                    val offsetFromMiddle = childEndPositionX - (this.width / 2)

                    val imageAlpha = offsetFromMiddle / totalDragRange

                    if (imageAlpha > 0) {
                        imageApproved.alpha = imageAlpha
                    } else if (imageAlpha < 0) {
                        imageRejected.alpha = imageAlpha * -1
                    }

                    newX = event.rawX + dX
                    newY = event.rawY + dY

                    view.pivotY = view.height.toFloat()
                    if (imageAlpha < 0) {
                        view.rotation = (imageAlpha * 10)
                    } else {
                        view.rotation = imageAlpha * 10
                    }

                    view.x = newX
                    view.y = newY
                }

                else -> return true
            }

            return true
        } else {
            return false

        }
    }
 }
