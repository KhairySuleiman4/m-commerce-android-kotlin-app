package com.example.mcommerce.data.utils

import com.example.mcommerce.R

fun imagesMapper(imageUrl: String): Int {
    return when (imageUrl) {
        "https://cdn.shopify.com/s/files/1/0935/5981/6465/collections/custom_collections_1.jpg?v=1748952967" -> R.drawable.men
        "https://cdn.shopify.com/s/files/1/0935/5981/6465/collections/custom_collections_2.jpg?v=1748952969" -> R.drawable.women
        "https://cdn.shopify.com/s/files/1/0935/5981/6465/collections/custom_collections_3.jpg?v=1748952970" -> R.drawable.kids
        "https://cdn.shopify.com/s/files/1/0935/5981/6465/collections/custom_collections_4.jpg?v=1748952972" -> R.drawable.onsale
        else -> R.drawable.ic_launcher_foreground
    }
}