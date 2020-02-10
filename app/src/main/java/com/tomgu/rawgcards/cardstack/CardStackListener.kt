package com.tomgu.rawgcards.cardstack

import com.tomgu.rawgcards.main.api.Game

interface CardStackListener {

    fun onRejected(game:Game)

    fun onApproved(game:Game)

    fun viewCountRemain(count : Int)
}