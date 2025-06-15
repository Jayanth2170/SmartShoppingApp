package com.example.smartshoppingcart

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class ItemInfo(
    val name: String,
    val price: Int,
    val expiry: String,
    val calories: Int?,
    val carbs: Int?
)

class DBHelper(context: Context) : SQLiteOpenHelper(context, "ItemsDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE Items (
                name TEXT PRIMARY KEY,
                price INTEGER,
                expiry TEXT,
                calories INTEGER,
                carbs INTEGER
            )
        """.trimIndent()
        )
        insertItems(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Items")
        onCreate(db)
    }

    private fun insertItems(db: SQLiteDatabase) {
        val items = listOf(
            ItemInfo("1 Dark Chocolate", 80, "2025-12-31", 210, 25),
            ItemInfo("Ayurveda gel", 100, "2026-06-01", null, null),
            ItemInfo("Balaji Aloo Sev", 30, "2025-11-20", 180, 20),
            ItemInfo("Balaji Ratlam Sev", 35, "2025-11-25", 185, 22),
            ItemInfo("Celebrations Pack", 200, "2025-12-15", 250, 28),
            ItemInfo("Closeup", 50, "2026-01-01", null, null),
            ItemInfo("Colgate", 80, "2026-01-01", null, null),
            ItemInfo("Dabeli Masala", 20, "2025-10-10", 70, 12),
            ItemInfo("Dabeli burger", 40, "2025-09-10", 250, 30),
            ItemInfo("Dark Fantasy", 150, "2025-09-15", 300, 35),
            ItemInfo("Dove Shampoo", 180, "2026-04-10", null, null),
            ItemInfo("Dove soap", 40, "2026-03-30", null, null),
            ItemInfo("Everest", 10, "2025-10-01", 50, 6),
            ItemInfo("Garam Masala", 30, "2025-09-01", 60, 7),
            ItemInfo("Head Shoulders Shampoo", 250, "2026-02-20", null, null),
            ItemInfo("Krack Jack", 20, "2025-07-15", 120, 15),
            ItemInfo("Liril", 30, "2026-01-15", null, null),
            ItemInfo("Lux soap", 40, "2026-01-10", null, null),
            ItemInfo("Malan", 50, "2025-06-30", null, null),
            ItemInfo("Marie Gold", 20, "2025-07-01", 100, 12),
            ItemInfo("Nescafe", 150, "2026-05-01", 40, 4),
            ItemInfo("Real Grape", 30, "2025-08-15", 150, 18),
            ItemInfo("Rin Big Bar", 45, "2026-02-01", null, null),
            ItemInfo("TATA Salt", 20, "2026-06-10", 0, 0),
            ItemInfo("Tomato Twist Lays", 40, "2025-08-01", 200, 23),
            ItemInfo("Tresemme", 250, "2026-04-15", null, null),
            ItemInfo("Undhiya", 70, "2025-07-20", 130, 14),
            ItemInfo("Vaseline Aloe", 120, "2026-03-01", null, null),
            ItemInfo("Veg Hakka Noodles", 50, "2025-08-05", 220, 27),
            ItemInfo("ViccoVajradant", 90, "2026-02-28", null, null),
            ItemInfo("Vim soap", 50, "2026-03-10", null, null),
            ItemInfo("White Lakme", 200, "2026-02-01", null, null),
            ItemInfo("blue lays", 20, "2025-08-15", 210, 24),
            ItemInfo("lifeboy soap", 30, "2026-01-01", null, null),
            ItemInfo("maggie", 20, "2025-08-30", 190, 21),
            ItemInfo("orange lays", 20, "2025-08-15", 200, 22),
            ItemInfo("pears soap", 50, "2026-03-01", null, null),
            ItemInfo("surf", 100, "2026-04-01", null, null)
        )

        for (item in items) {
            val values = ContentValues().apply {
                put("name", item.name)
                put("price", item.price)
                put("expiry", item.expiry)
                put("calories", item.calories)
                put("carbs", item.carbs)
            }
            db.insert("Items", null, values)
        }
    }


    // Method to retrieve item details based on item name
    // Method to retrieve item details based on item name
    fun getItemDetails(db: SQLiteDatabase, itemName: String): ItemInfo? {
        val cursor = db.rawQuery("SELECT * FROM Items WHERE name = ?", arrayOf(itemName))

        return if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex("name")
            val priceIndex = cursor.getColumnIndex("price")
            val expiryIndex = cursor.getColumnIndex("expiry")
            val caloriesIndex = cursor.getColumnIndex("calories")
            val carbsIndex = cursor.getColumnIndex("carbs")

            // Ensure indices are valid (>= 0) before accessing the values
            if (nameIndex >= 0 && priceIndex >= 0 && expiryIndex >= 0) {
                val name = cursor.getString(nameIndex)
                val price = cursor.getInt(priceIndex)
                val expiry = cursor.getString(expiryIndex)
                val calories = if (caloriesIndex >= 0) cursor.getInt(caloriesIndex) else null
                val carbs = if (carbsIndex >= 0) cursor.getInt(carbsIndex) else null
                ItemInfo(name, price, expiry, calories, carbs)
            } else {
                null
            }
        } else {
            null
        }.also {
            cursor.close()
        }
    }
}


