package ru.vsu.puggybank.fragments

import android.app.AlertDialog
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
import android.text.InputType
import android.widget.EditText
import kotlinx.coroutines.runBlocking
import ru.vsu.puggybank.transactions.banking.MockCredentialProvider
import ru.vsu.puggybank.transactions.banking.gazprom.GazpromAuthProvider
import java.time.LocalDate

/**
 * A simple [Fragment] subclass.
 * Use the [MainScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainScreenFragment : Fragment() {
    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!
    private var dateFrom: LocalDate? = null
    private var dateTo: LocalDate? = null
    private val authProvider = GazpromAuthProvider()
    private val mockCredentialsProvider = MockCredentialProvider("", "")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categories = IntArray(30) { it + 1 }.map { it.toString() }

        val now = LocalDate.now()
        this.dateFrom = LocalDate.of(now.year, now.month, 1)
        this.dateTo = now

        this.updateSelectDateToText()
        this.binding.selectDateFrom.text = this.dateFrom.toString()

        this.binding.selectDateTo.setOnClickListener {
            val dpd = DatePickerDialog(requireActivity(), { _ , year, month, day ->
                this.dateTo = LocalDate.of(year, month, day)
                this.updateSelectDateToText()
            }, this.dateTo!!.year, this.dateTo!!.monthValue, this.dateTo!!.dayOfMonth)

            dpd.show()
        }

        this.binding.selectDateFrom.setOnClickListener {
            val dpd = DatePickerDialog(requireActivity(), { _, year, month, day ->
                this.dateFrom = LocalDate.of(year, month, day)
                this.updateSelectDateFromText()
            }, this.dateFrom!!.year, this.dateFrom!!.monthValue, this.dateFrom!!.dayOfMonth)

            dpd.show()
        }

        val chart = AAChartModel()
            .chartType(AAChartType.Spline)
            .title("₽")
            .backgroundColor("#ffffff")
            .categories(categories.toTypedArray())
            .dataLabelsEnabled(false)
            .yAxisTitle("")
            .series(arrayOf(
                AASeriesElement()
                    .name("Расход")
                    .data(arrayOf(401, 1097, 404, 1125, 639, 291, 351, 1398, 217, 1487, 703, 935, 962, 626, 325, 848, 340, 759, 933, 289, 1414, 851, 333, 296, 1542, 620, 1328, 796, 1185, 844)),
                AASeriesElement()
                    .name("Приход")
                    .data(arrayOf(145, 362, 288, 181, 295, 134, 161, 116, 163, 148, 277, 198, 264, 159, 361, 377, 301, 371, 350, 62, 25, 397, 122, 10, 361, 216, 218, 389, 280, 259)),
                )
            )

        runBlocking {
            try {
                authProvider.auth(mockCredentialsProvider.getCredentials())
            } catch (e: Exception) {
                showDoubleFactorCodeEnterDialog()
            }
        }

        this.binding.aaChartView.aa_drawChartWithChartModel(chart)
    }

    private fun showDoubleFactorCodeEnterDialog(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle("Title")

        val input = EditText(activity)
        input.hint = "Enter double factor code"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Submit") { _, _ ->
            runBlocking {
                val code = input.text.toString()
                authProvider.auth(mockCredentialsProvider.getCredentials(), code)
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun updateSelectDateToText() {
        this.binding.selectDateTo.text = this.dateTo.toString()
    }

    private fun updateSelectDateFromText() {
        this.binding.selectDateFrom.text = this.dateFrom.toString()
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
        authProvider.close()
        _binding = null
    }
}