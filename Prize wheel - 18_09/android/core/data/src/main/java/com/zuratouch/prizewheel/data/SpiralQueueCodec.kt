package com.zuratouch.prizewheel.data

import com.zuratouch.prizewheel.domain.QueuedProduct
import org.json.JSONArray
import org.json.JSONObject

object SpiralQueueCodec {
    fun encode(queue: List<QueuedProduct>): String {
        val array = JSONArray()
        queue.forEach { product ->
            array.put(
                JSONObject()
                    .put("categoryId", product.categoryId)
                    .put("productName", product.productName),
            )
        }
        return array.toString()
    }

    fun decode(json: String): List<QueuedProduct> {
        if (json.isBlank() || json == "[]") return emptyList()
        return runCatching {
            val array = JSONArray(json)
            List(array.length()) { index ->
                val item = array.getJSONObject(index)
                QueuedProduct(
                    categoryId = item.getString("categoryId"),
                    productName = item.getString("productName"),
                )
            }
        }.getOrDefault(emptyList())
    }
}
