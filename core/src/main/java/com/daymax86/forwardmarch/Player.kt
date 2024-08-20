package com.daymax86.forwardmarch

object Player {

    val playerItems: MutableList<Item> = mutableListOf()
    var luckStat: Int = 50 // Percentage chance of positive event happening
    var coinTotal: Int = 0
    var bombTotal: Int = 0

    fun changeLuck(amount: Int) {
        luckStat += amount
        if (luckStat > 80) { // Luck stat can't be higher than 80% ...
            luckStat = 80
        } else if (luckStat < 30) { // ... or less than 30%
            luckStat = 30
        }
    }

    fun changeCoinTotal(amount: Int) {
        coinTotal += amount
        if (coinTotal < 0) {
            coinTotal = 0
        }
    }

    fun changeBombTotal(amount: Int) {
        bombTotal += amount
        if (bombTotal < 0) {
            bombTotal = 0
        }
    }

    fun canAfford(item: Item): Boolean { // This can be modified if an item reduces shop prices for example
        return coinTotal >= item.shopPrice
    }

}
