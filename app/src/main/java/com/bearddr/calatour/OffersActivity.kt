package com.bearddr.calatour

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bearddr.calatour.chat.remote.ChatApi
import com.bearddr.calatour.offers.Offer
import com.bearddr.calatour.offers.OffersAdapter
import com.bearddr.calatour.util.UserInfo
import com.bearddr.calatour.util.UserInfo.username
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.concurrent.schedule

class OffersActivity : AppCompatActivity() {

    private val chatApi = ChatApi.create()

    private lateinit var myAdapter: OffersAdapter

    private val detailsActivityId = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offers)

        title = "Offers List"

        val listView = findViewById<ListView>(R.id.offersList)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)


        myAdapter = OffersAdapter(
            context = this,
        )
        listView.adapter = myAdapter
        listView.setOnItemClickListener { adapterView, view, position, id ->
            val offer = myAdapter.getItem(position) as Offer
            myAdapter.increaseViews(position)

            val intent = Intent(this@OffersActivity, OfferDetailsActivity::class.java)
            intent.putExtra("offer_detail", offer)
            intent.putExtra("offer_position", position)
            startActivityForResult(intent, detailsActivityId)
        }
        myAdapter.setDataSource(Offer.getOffersFromFile("offers.json", this))

        registerForContextMenu(listView)


        listView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
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

        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo

        when(item.itemId) {
            R.id.addOffer -> { myAdapter.addOffer(
                position = info.position,
                offer = Offer(
                    imageUrl = "https://i.imgur.com/D7LiBDn.png",
                    city = "Cluj-Napoca",
                    period = "3 night",
                    price = 300,
                    currency = "EUR",
                    description = "Offer description"
                )
            ) }
            R.id.removeOffer -> { myAdapter.removeOffer(
                position = info.position
            )}
        }
        return super.onContextItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.offers_options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.sign_out -> { onBackPressed() }
            R.id.reset_list -> {
                val offers = Offer.getOffersFromFile("offers.json", this)
                myAdapter.setDataSource(
                    newItems = offers
                )
                Toast.makeText(applicationContext, "List has been reset", Toast.LENGTH_SHORT).show()
            }
            R.id.clear_favorites -> {
                myAdapter.clearFavorites()
            }
            R.id.chat -> {
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("username", username)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val builder =  AlertDialog.Builder(this)
        builder.setTitle("PLease Confirm")
            .setMessage("Are you sure?")
            .setPositiveButton("Sign Out") { _, _ ->
                chatApi.logout(
                    header = "Bearer ${UserInfo.token}"
                ).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            finish()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) = Unit
                })
            }
            .setNegativeButton("Cancel", null)
            .create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when(requestCode) {
            detailsActivityId -> {
                if (resultCode == RESULT_OK) {
                    val isFavorite = data?.getBooleanExtra("offer_isFavorite", false)!!
                    val position = data.getIntExtra("offer_position", -1)

                    if (position > -1) {
                        val offer = myAdapter.getItem(position) as Offer
                        offer.isFavorite = isFavorite
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}