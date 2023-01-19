package ru.vsu.puggybank.utils

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ru.vsu.puggybank.dto.gazprom.convertTimestamp
import ru.vsu.puggybank.dto.view.Transaction

class TransactionArrayAdapter(private val context: Context): BaseAdapter() {
    private var _transactions: List<Transaction> = listOf()
    var transactions: List<Transaction>
        get() = _transactions
        set (value) {
            _transactions = value
            notifyDataSetChanged()
        }


    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.simple_list_item_2, null)
        val text1 = view.findViewById<View>(android.R.id.text1) as TextView
        val text2 = view.findViewById<View>(android.R.id.text2) as TextView

        text1.text = transactions[position].description.split("\n")[0]
        text2.text = context.resources.getString(ru.vsu.puggybank.R.string.transactionText).format(transactions[position].amount,
            convertTimestamp(transactions[position].timestamp)
        )
        return view
    }

    override fun getCount(): Int {
        return transactions.size
    }

    override fun getItem(position: Int): Any {
        return transactions[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}