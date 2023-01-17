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
import android.widget.TextView
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ru.vsu.puggybank.dto.gazprom.GazpromResponse
import ru.vsu.puggybank.dto.gazprom.convertTimestamp
import ru.vsu.puggybank.dto.gazprom.mapGazpromResponseToTransactions
import ru.vsu.puggybank.dto.view.Transaction
import java.time.temporal.ChronoUnit
import java.time.LocalDate

class MainScreenFragment : Fragment() {
    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!
    private var dateFrom: LocalDate? = null
    private var dateTo: LocalDate? = null
    private val data = """
        {"result":0,"hasNextData":true,"id":"17900996","history":[{"amount":-4000.00,"feeCurrency":"RUR","description":"Перевод на карту Tinkoff Card2Card\n\nКод операции\n19011716863\n\nКарта получателя\n553691******6726","type":"Перевод","tid":"19011716863","hold":1,"feeAmount":0.00,"isRepeatable":false,"transRef":null,"hasReceipt":false,"currency":"RUR","hasTemplate":false,"timestamp":"20221222175405"},{"amount":4000.00,"feeCurrency":"RUR","description":"Зачисление зарплаты Код операции 19005733986","type":"Изменение баланса","tid":"19005733986","hold":1,"feeAmount":0,"isRepeatable":false,"transRef":null,"hasReceipt":false,"currency":"RUR","hasTemplate":false,"timestamp":"20221222114618"},{"amount":-99.00,"feeCurrency":"RUR","description":"XSOLLA\n\nКод операции\n18302125957","type":"Покупка","tid":"18302125957","hold":1,"feeAmount":0.00,"isRepeatable":false,"transRef":null,"hasReceipt":false,"currency":"RUR","hasTemplate":false,"timestamp":"20221023182008"},{"amount":100.00,"feeCurrency":"RUR","description":"Пополнение с карты\n\nКод операции\n18302041218","type":"Входящий перевод","tid":"18302041218","hold":1,"feeAmount":0,"isRepeatable":false,"transRef":null,"hasReceipt":false,"currency":"RUR","hasTemplate":false,"timestamp":"20221023181230"},{"amount":-86.00,"feeCurrency":"RUR","description":"Теле2\n\nКод операции\n18161615705\n\nНомер телефона\n9518669892","type":"Покупка","tid":"18161615705","hold":1,"feeAmount":0.00,"isRepeatable":true,"transRef":"558042555","hasReceipt":true,"currency":"RUR","hasTemplate":true,"timestamp":"20221011104517"},{"amount":-21.00,"feeCurrency":"RUR","description":"INFORMSETI.RU VOZ\n\nКод операции\n18132325732","type":"Покупка","tid":"18132325732","hold":1,"feeAmount":0.00,"isRepeatable":false,"transRef":null,"hasReceipt":false,"currency":"RUR","hasTemplate":false,"timestamp":"20221008130934"},{"amount":-52.00,"feeCurrency":"RUR","description":"GALCHONOK\n\nКод операции\n18126407578","type":"Покупка","tid":"18126407578","hold":1,"feeAmount":0.00,"isRepeatable":false,"transRef":null,"hasReceipt":false,"currency":"RUR","hasTemplate":false,"timestamp":"20221007191706"},{"amount":-21.00,"feeCurrency":"RUR","description":"VPT SBERTROJKA\n\nКод операции\n17902918429","type":"Покупка","tid":"17902918429","hold":1,"feeAmount":0.00,"isRepeatable":false,"transRef":null,"hasReceipt":false,"currency":"RUR","hasTemplate":false,"timestamp":"20220917141630"},{"amount":-21.00,"feeCurrency":"RUR","description":"INFORMSETI.RU VOZ\n\nКод операции\n17899245784","type":"Покупка","tid":"17899245784","hold":1,"feeAmount":0.00,"isRepeatable":false,"transRef":null,"hasReceipt":false,"currency":"RUR","hasTemplate":false,"timestamp":"20220917084735"},{"amount":-163.00,"feeCurrency":"RUR","description":"RUSSKIJ APPETIT\n\nКод операции\n17896467360","type":"Покупка","tid":"17896467360","hold":1,"feeAmount":0.00,"isRepeatable":false,"transRef":null,"hasReceipt":false,"currency":"RUR","hasTemplate":false,"timestamp":"20220916201014"}],"page":1,"techInfo":{"requestId":"fca05789-d1ed-438d-9f80-d840bc92d887"}}
    """.trimIndent()
    private var allTransactions: List<Transaction> = listOf()
    private var filteredTransactions: List<Transaction> = listOf()
    private var categories: Array<String> = arrayOf()
    private var inTransactionsByDays: Array<Double> = arrayOf()
    private var outTransactionsByDays: Array<Double> = arrayOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDates()
        val format = Json { isLenient = true }
        val a = format.decodeFromString<GazpromResponse>(data)
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
        val daysQuantity = (ChronoUnit.DAYS.between(dateFrom, dateTo) + 1).toInt()
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
        for (transaction in allTransactions) {
            val textView = TextView(context)
            textView.text = "${transaction.description.split("\n")[0]}, ${transaction.amount}"

            binding.transactionsView.addView(textView)
            val transactionDay = convertTimestamp(transaction.timestamp)
            val dayOffset = (ChronoUnit.DAYS.between(dateFrom, transactionDay)).toInt()

            if (dayOffset < daysQuantity && dayOffset >= 0) {
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
    ): View? {
        _binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}