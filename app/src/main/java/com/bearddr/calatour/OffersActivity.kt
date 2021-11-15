package com.bearddr.calatour

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.ProgressBar
import com.bearddr.calatour.offers.Offer
import com.bearddr.calatour.offers.OffersAdapter
import java.util.*
import kotlin.concurrent.schedule

class OffersActivity : AppCompatActivity() {

    private lateinit var myAdapter: OffersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offers)

        title = "Offers List"

        val listView = findViewById<ListView>(R.id.offersList)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)


        myAdapter = OffersAdapter(
            context = this,
            dataSource = Offer.getOffersFromFile("offers.json", this)
        )
        listView.adapter = myAdapter

        registerForContextMenu(listView)


        Timer().schedule(delay = 3000) {
            Handler(mainLooper).post {
                listView.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        if (v!!.id == R.id.offersList) {
            val info = menuInfo as AdapterView.AdapterContextMenuInfo
            val offer = myAdapter.getItem(info.position) as Offer

            menu!!.setHeaderTitle("${offer.city}, ${offer.period}")
            menuInflater.inflate(R.menu.offer_context_menu, menu)
        }

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return super.onContextItemSelected(item)
    }
}