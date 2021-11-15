package com.bearddr.calatour.offers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bearddr.calatour.R
import com.squareup.picasso.Picasso

class OffersAdapter(
    private val context: Context,
    private var dataSource: List<Offer>
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int = dataSource.size

    override fun getItem(p0: Int): Any = dataSource[p0]

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.item_offer, parent, false)
        val titleView = rowView.findViewById<TextView>(R.id.offerTitle)
        val imageView = rowView.findViewById<ImageView>(R.id.offerImage)
        val descriptionView = rowView.findViewById<TextView>(R.id.offerDescription)
        val priceView = rowView.findViewById<TextView>(R.id.offerPrice)

        val offer = getItem(position) as Offer

        titleView.text = "${offer.city}, ${offer.period}"
        descriptionView.text = offer.description
        priceView.text = offer.price.toString()
        Picasso.with(context)
            .load(offer.imageUrl)
            .into(imageView)


        return rowView
    }


}