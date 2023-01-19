package ru.vsu.puggybank.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import ru.vsu.puggybank.R
import ru.vsu.puggybank.databinding.FragmentMainScreenBinding
import ru.vsu.puggybank.dto.gazprom.convertTimestamp
import ru.vsu.puggybank.dto.gazprom.formatTimestamp
import ru.vsu.puggybank.dto.gazprom.mapGazpromResponseToTransactions
import ru.vsu.puggybank.dto.view.Transaction
import ru.vsu.puggybank.transactions.banking.gazprom.GazpromAuthProvider
import ru.vsu.puggybank.transactions.banking.gazprom.GazpromClient
import ru.vsu.puggybank.transactions.banking.gazprom.GazpromSharedPreferencesSessionManager
import ru.vsu.puggybank.transactions.banking.interfaces.TransactionsProvider
import ru.vsu.puggybank.utils.TransactionArrayAdapter
import ru.vsu.puggybank.utils.data.DatePickerDialogBuilder
import ru.vsu.puggybank.utils.json.ResponseMapper
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.max


class MainScreenFragment : Fragment() {
    private var _binding: FragmentMainScreenBinding? = null
    private var dateFrom: LocalDate? = null
    private var dateTo: LocalDate? = null
    private var allTransactions: List<Transaction> = listOf()
    private var filteredTransactions: List<Transaction> = listOf()
    private var categories: Array<String> = arrayOf()
    private var inTransactionsByDays: Array<Double> = arrayOf()
    private var outTransactionsByDays: Array<Double> = arrayOf()
    private var transactionsProvider: TransactionsProvider? = null
    private var _transactionArrayAdapter: TransactionArrayAdapter? = null
    private val transactionArrayAdapter get() = _transactionArrayAdapter!!
    private val authProvider = GazpromAuthProvider()
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gazpromSharedPreferences = activity?.getSharedPreferences("${GAZPROM_PREFIX}_session.xml", Context.MODE_PRIVATE)!!
        transactionsProvider = GazpromClient(GazpromSharedPreferencesSessionManager(gazpromSharedPreferences).session)
        _transactionArrayAdapter = TransactionArrayAdapter(requireContext())
        binding.listView.adapter = _transactionArrayAdapter

        initDates()

        val json = transactionsProvider!!.getTransactionsJSONString()
        allTransactions = ResponseMapper.mapResponse(json, ::mapGazpromResponseToTransactions)
        updateTransactionsList()

        updateSelectDateToText()
        binding.selectDateFrom.text = dateFrom.toString()

        binding.selectDateTo.setOnClickListener {
            val dpd = DatePickerDialogBuilder.build(requireActivity(), dateTo!!) {
                dateTo = it
                updateSelectDateToText()
                updateTransactions()
            }

            dpd.show()
        }

        binding.selectDateFrom.setOnClickListener {
            val dpd = DatePickerDialogBuilder.build(requireActivity(), dateFrom!!) {
                dateFrom = it
                updateSelectDateFromText()
                updateTransactions()
            }

            dpd.show()
        }
        initChart()
    }

    private fun initDates() {
        val now = LocalDate.now()
        dateFrom = LocalDate.of(now.year, now.month, 1)
        dateTo = now
    }

    private fun updateTransactions() {
        updateTransactionsList()
        updateTransactionsView()
        binding.aaChartView.aa_refreshChartWithChartModel(getChartModel())
    }

    private fun updateTransactionsList() {
        val daysQuantity = max((ChronoUnit.DAYS.between(dateFrom, dateTo) + 1).toInt(), 0)
        var currentDay = 0

        categories = Array(daysQuantity) { "" }

        inTransactionsByDays = Array(daysQuantity) { 0.0 }
        outTransactionsByDays = Array(daysQuantity) { 0.0 }
        filteredTransactions = allTransactions.filter { dateFrom!! < convertTimestamp(it.timestamp) && dateTo!! > convertTimestamp(it.timestamp)  }
        transactionArrayAdapter.transactions = filteredTransactions

        while (currentDay < daysQuantity) {
            val day = LocalDate.from(dateFrom).plusDays(currentDay.toLong())
            categories[currentDay] = day.toString()
            currentDay++
        }
    }

    private fun updateTransactionsView() {
        val daysQuantity = (ChronoUnit.DAYS.between(dateFrom, dateTo) + 1).toInt()

        for (transaction in filteredTransactions) {
            val textView = TextView(context)
            textView.text = getString(R.string.transactionText, transaction.description.split("\n")[0], transaction.amount.toString(), formatTimestamp(transaction.timestamp))

            val transactionDay = convertTimestamp(transaction.timestamp)
            val dayOffset = (ChronoUnit.DAYS.between(dateFrom, transactionDay)).toInt()

            if (dayOffset in 0 until daysQuantity) {
                if (transaction.amount > 0) {
                    inTransactionsByDays[dayOffset] += transaction.amount
                } else {
                    outTransactionsByDays[dayOffset] += transaction.amount
                }
            }
        }

    }

    private fun initChart() {
        binding.aaChartView.aa_drawChartWithChartModel(getChartModel())
    }

    @Suppress("UNCHECKED_CAST")
    private fun getChartModel(): AAChartModel {
        return AAChartModel()
            .chartType(AAChartType.Spline)
            .title(resources.getString(R.string.rubleSign))
            .backgroundColor("#ffffff")
            .categories(categories)
            .dataLabelsEnabled(false)
            .yAxisTitle("")
            .series(
                arrayOf(
                    AASeriesElement()
                        .name(resources.getString(R.string.consumption))
                        .data(outTransactionsByDays as Array<Any>),
                    AASeriesElement()
                        .name(resources.getString(R.string.income))
                        .data(inTransactionsByDays as Array<Any>)
                ),
            )
    }

    private fun updateSelectDateFromText() {
        binding.selectDateFrom.text = dateFrom.toString()
    }

    private fun updateSelectDateToText() {
        binding.selectDateTo.text = dateTo.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        authProvider.close()
        _binding = null
    }
}

