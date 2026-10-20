package com.zuratouch.feature.salecards

import com.zuratouch.feature.salecards.R

/**
 * Maps product names to artwork from IMAGENS ROLETA.
 */
object CardProductImages {
    private val exactMatches = mapOf(
        "Pato de Borracha" to R.drawable.card_pato,
        "Porta-chaves" to R.drawable.card_portachaves,
        "Livro" to R.drawable.card_livro,
        "Livro Premium" to R.drawable.card_livro,
        "Mini Ventoinha" to R.drawable.card_fan,
        "Meias" to R.drawable.card_meias,
        "Boné" to R.drawable.card_bone,
        "Saco Reutilizável" to R.drawable.card_tote_bag,
        "Garrafa Reutilizável" to R.drawable.card_garrafa,
        "Cartas Pokémon" to R.drawable.card_pokemon,
        "Peluche" to R.drawable.card_peluche,
        "Auriculares" to R.drawable.card_auriculares,
        "Headphones" to R.drawable.card_auriculares,
        "Funko Pop" to R.drawable.card_funko,
        "Rato Gaming" to R.drawable.card_rato,
        "Power Bank" to R.drawable.card_power_bank,
        "Jantar para Duas Pessoas" to R.drawable.card_jantar,
        "Bilhetes para Concerto" to R.drawable.card_concert,
        "Camisola Oficial de Futebol" to R.drawable.card_camisola,
        "Experiência de Aventura" to R.drawable.card_paraquedas,
        "iPhone" to R.drawable.card_iphone,
        "Viagem" to R.drawable.card_aviao,
        "AirPods" to R.drawable.card_airpods,
    )

    fun drawableFor(productName: String): Int? =
        exactMatches[productName] ?: fuzzyMatch(productName)

    private fun fuzzyMatch(productName: String): Int? {
        val normalized = productName.lowercase()
        return when {
            normalized.contains("pato") -> R.drawable.card_pato
            normalized.contains("porta-chaves") || normalized.contains("portachaves") -> R.drawable.card_portachaves
            normalized.contains("livro") -> R.drawable.card_livro
            normalized.contains("ventoinha") -> R.drawable.card_fan
            normalized.contains("meias") -> R.drawable.card_meias
            normalized.contains("boné") || normalized.contains("bone") -> R.drawable.card_bone
            normalized.contains("saco") -> R.drawable.card_tote_bag
            normalized.contains("garrafa") -> R.drawable.card_garrafa
            normalized.contains("pokémon") || normalized.contains("pokemon") -> R.drawable.card_pokemon
            normalized.contains("peluche") -> R.drawable.card_peluche
            normalized.contains("auricular") || normalized.contains("headphone") -> R.drawable.card_auriculares
            normalized.contains("funko") -> R.drawable.card_funko
            normalized.contains("rato") -> R.drawable.card_rato
            normalized.contains("power bank") -> R.drawable.card_power_bank
            normalized.contains("jantar") -> R.drawable.card_jantar
            normalized.contains("concerto") || normalized.contains("bilhete") -> R.drawable.card_concert
            normalized.contains("camisola") || normalized.contains("futebol") -> R.drawable.card_camisola
            normalized.contains("aventura") || normalized.contains("paraqued") -> R.drawable.card_paraquedas
            normalized.contains("iphone") -> R.drawable.card_iphone
            normalized.contains("airpod") -> R.drawable.card_airpods
            normalized.contains("viagem") || normalized.contains("avião") || normalized.contains("aviao") -> R.drawable.card_aviao
            else -> null
        }
    }
}
