package com.zuratouch.prizewheel.data

import com.zuratouch.prizewheel.domain.CardBoard
import com.zuratouch.prizewheel.domain.CardGameSession
import com.zuratouch.prizewheel.domain.CardSlot
import com.zuratouch.prizewheel.domain.DispensePlan
import com.zuratouch.prizewheel.domain.PrizeCategory
import com.zuratouch.prizewheel.domain.QueuedProduct
import com.zuratouch.prizewheel.domain.Spiral
import org.json.JSONArray
import org.json.JSONObject

object CardGameSessionCodec {
    const val SAVED_STATE_KEY = "card_game_session_json"

    fun encode(session: CardGameSession): String {
        val root = JSONObject()
            .put("paymentConfirmed", session.paymentConfirmed)
            .put("isTrialRound", session.isTrialRound)
            .put("boardSeed", session.board.boardSeed)
            .put("generatedAtMs", session.board.generatedAtMs)
            .put("slots", JSONArray(session.board.slots.map { encodeSlot(it) }))
        session.selectedSlot?.let { root.put("selectedIndex", it.index) }
        return root.toString()
    }

    fun decode(json: String): CardGameSession? = runCatching {
        val root = JSONObject(json)
        val slotsArray = root.getJSONArray("slots")
        val slots = List(slotsArray.length()) { index ->
            decodeSlot(slotsArray.getJSONObject(index))
        }
        val board = CardBoard(
            slots = slots,
            generatedAtMs = root.getLong("generatedAtMs"),
            boardSeed = root.getLong("boardSeed"),
        )
        val selectedIndex = if (root.has("selectedIndex")) root.getInt("selectedIndex") else null
        val selectedSlot = selectedIndex?.let { idx -> slots.firstOrNull { it.index == idx } }
        CardGameSession(
            board = board,
            paymentConfirmed = root.getBoolean("paymentConfirmed"),
            selectedSlot = selectedSlot,
            isTrialRound = root.optBoolean("isTrialRound", false),
        )
    }.getOrNull()

    private fun encodeSlot(slot: CardSlot): JSONObject {
        val plan = slot.dispensePlan
        return JSONObject()
            .put("index", slot.index)
            .put("isTargetSlot", slot.isTargetSlot)
            .put("categoryId", slot.category.id)
            .put("categoryLabel", slot.category.label)
            .put("categoryColor", slot.category.color)
            .put("categoryWeight", slot.category.weight)
            .put("categoryIcon", slot.category.icon)
            .put("spiralId", plan.spiral.id)
            .put("shelf", plan.spiral.shelf)
            .put("slot", plan.spiral.slot)
            .put("vmcLane", plan.spiral.vmcLane)
            .put("maxCapacity", plan.spiral.maxCapacity)
            .put("productCategoryId", plan.product.categoryId)
            .put("productName", plan.product.productName)
            .put("displayCategoryId", slot.displayProduct.categoryId)
            .put("displayProductName", slot.displayProduct.productName)
    }

    private fun decodeSlot(obj: JSONObject): CardSlot {
        val category = PrizeCategory(
            id = obj.getString("categoryId"),
            label = obj.getString("categoryLabel"),
            color = obj.getLong("categoryColor"),
            weight = obj.getInt("categoryWeight"),
            icon = obj.getString("categoryIcon"),
        )
        val product = QueuedProduct(
            categoryId = obj.getString("productCategoryId"),
            productName = obj.getString("productName"),
        )
        val displayProduct = if (obj.has("displayProductName")) {
            QueuedProduct(
                categoryId = obj.getString("displayCategoryId"),
                productName = obj.getString("displayProductName"),
            )
        } else {
            product
        }
        val spiral = Spiral(
            id = obj.getString("spiralId"),
            shelf = obj.getInt("shelf"),
            slot = obj.getInt("slot"),
            vmcLane = obj.getInt("vmcLane"),
            maxCapacity = obj.getInt("maxCapacity"),
            queue = listOf(product),
        )
        return CardSlot(
            index = obj.getInt("index"),
            dispensePlan = DispensePlan(spiral, product),
            displayProduct = displayProduct,
            category = category,
            isTargetSlot = obj.getBoolean("isTargetSlot"),
        )
    }
}
