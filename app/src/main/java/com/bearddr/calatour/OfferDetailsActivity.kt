package com.bearddr.calatour

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bearddr.calatour.offers.Offer
import com.squareup.picasso.Picasso

class OfferDetailsActivity : AppCompatActivity() {

    private lateinit var offer: Offer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_details)

        offer = intent.getSerializableExtra("offer_detail") as Offer

        val titleRef = findViewById<TextView>(R.id.offerDetailTitle)
        val imageRef = findViewById<ImageView>(R.id.offerDetailImage)
        val descriptionRef = findViewById<TextView>(R.id.offerDetailDescription)
        val priceRef = findViewById<TextView>(R.id.offerDetailPrice)
        val counterRef = findViewById<TextView>(R.id.offerDetailCounter)

        titleRef.text = offer.city + " - " + offer.period
        Picasso.with(this)
            .load(offer.imageUrl)
            .into(imageRef)
        descriptionRef.text = offer.description
        priceRef.text = "${offer.price} ${offer.currency}"
        counterRef.text = offer.counter.toString()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.offer_detail_options_menu, menu)

        if (offer.isFavorite) {
            menu!!.getItem(0).title = "Remove from favorites"
        } else {
            menu!!.getItem(0).title = "Add to favorites"
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.offerDetailFavorites) {
            offer.isFavorite = !offer.isFavorite
            if (offer.isFavorite) {
                item.title = "Remove from favorites"
                Toast.makeText(applicationContext, "Added to favorites", Toast.LENGTH_SHORT).show()
            } else {
                item.title = "Add to favorites"
                Toast.makeText(applicationContext, "Removed from favorites", Toast.LENGTH_SHORT).show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("offer_isFavorite", offer.isFavorite)
        intent.putExtra("offer_position", this.intent.getIntExtra("offer_position", -1))
        setResult(RESULT_OK, intent)
        super.onBackPressed()
    }
}