package ru.vsu.puggybank.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import ru.vsu.puggybank.databinding.FragmentMainScreenBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import android.app.DatePickerDialog
import android.content.Context
import ru.vsu.puggybank.transactions.banking.gazprom.GazpromAuthProvider
import android.widget.TextView
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ru.vsu.puggybank.R
import ru.vsu.puggybank.dto.gazprom.GazpromResponse
import ru.vsu.puggybank.dto.gazprom.convertTimestamp
import ru.vsu.puggybank.dto.gazprom.formatTimestamp
import ru.vsu.puggybank.dto.gazprom.mapGazpromResponseToTransactions
import ru.vsu.puggybank.dto.view.Transaction
import ru.vsu.puggybank.transactions.banking.gazprom.GazpromClient
import ru.vsu.puggybank.transactions.banking.gazprom.GazpromSharedPreferencesSessionManager
import java.time.temporal.ChronoUnit
import java.time.LocalDate
import kotlin.math.max

class MainScreenFragment : Fragment() {
    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!

    private var dateFrom: LocalDate? = null
    private var dateTo: LocalDate? = null
    private var allTransactions: List<Transaction> = listOf()
    private var filteredTransactions: List<Transaction> = listOf()
    private var categories: Array<String> = arrayOf()
    private var inTransactionsByDays: Array<Double> = arrayOf()
    private var outTransactionsByDays: Array<Double> = arrayOf()
    private val authProvider = GazpromAuthProvider()
    private var gazpromClient: GazpromClient? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gazpromClient = GazpromClient(GazpromSharedPreferencesSessionManager(activity?.getSharedPreferences("gazprom_session.xml", Context.MODE_PRIVATE)!!).session)

        initDates()
        val format = Json { isLenient = true }
        val a = format.decodeFromString<GazpromResponse>(gazpromClient!!.getTransactionsJSONString())
        allTransactions = mapGazpromResponseToTransactions(a)
        updateTransactions()

        updateSelectDateToText()
        binding.selectDateFrom.text = dateFrom.toString()

        binding.selectDateTo.setOnClickListener {
            val dpd = DatePickerDialog(requireActivity(), { _ , year, month, day ->
                dateTo = LocalDate.of(year, month + 1, day)
                updateSelectDateToText()
                update()
            }, dateTo!!.year, dateTo!!.monthValue - 1, dateTo!!.dayOfMonth)

            dpd.show()
        }

        binding.selectDateFrom.setOnClickListener {
            val dpd = DatePickerDialog(requireActivity(), { _, year, month, day ->
                dateFrom = LocalDate.of(year, month + 1, day)
                updateSelectDateFromText()
                update()
            }, dateFrom!!.year, dateFrom!!.monthValue - 1 , dateFrom!!.dayOfMonth)

            dpd.show()
        }
        initChart()
    }

    private fun initDates() {
        val now = LocalDate.now()
        dateFrom = LocalDate.of(now.year, now.month, 1)
        dateTo = now
    }

    private fun updateSelectDateToText() {
        binding.selectDateTo.text = dateTo.toString()
        updateTransactions()
    }

    private fun update() {
        updateTransactions()
        updateTransactionsView()
        binding.aaChartView.aa_refreshChartWithChartModel(getChartModel())
    }

    private fun updateTransactions() {
        val daysQuantity = max((ChronoUnit.DAYS.between(dateFrom, dateTo) + 1).toInt(), 0)
        var currentDay = 0

        categories = Array(daysQuantity) { "" }

        inTransactionsByDays = Array(daysQuantity) { 0.0 }
        outTransactionsByDays = Array(daysQuantity) { 0.0 }
        filteredTransactions = allTransactions.filter { dateFrom!! < convertTimestamp(it.timestamp) && dateTo!! > convertTimestamp(it.timestamp)  }

        while (currentDay < daysQuantity) {
            val day = LocalDate.from(dateFrom).plusDays(currentDay.toLong())
            categories[currentDay] = day.toString()
            currentDay++
        }
    }

    private fun updateTransactionsView() {
        val daysQuantity = (ChronoUnit.DAYS.between(dateFrom, dateTo) + 1).toInt()
        binding.transactionsView.removeAllViews()
        for (transaction in filteredTransactions) {
            val textView = TextView(context)
            textView.text = getString(R.string.transactionText, transaction.description.split("\n")[0], transaction.amount.toString(), formatTimestamp(transaction.timestamp))

            binding.transactionsView.addView(textView)
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
            .title("₽")
            .backgroundColor("#ffffff")
            .categories(categories)
            .dataLabelsEnabled(false)
            .yAxisTitle("")
            .series(arrayOf(
                AASeriesElement()
                    .name("Расход")
                    .data(outTransactionsByDays as Array<Any>),
                AASeriesElement()
                    .name("Приход")
                    .data(inTransactionsByDays as Array<Any>)),
            )
    }

    private fun updateSelectDateFromText() {
        binding.selectDateFrom.text = dateFrom.toString()
        updateTransactions()
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