package ru.vsu.puggybank

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import ru.vsu.puggybank.databinding.FragmentMainScreenBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AADataLabels

/**
 * A simple [Fragment] subclass.
 * Use the [MainScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainScreenFragment : Fragment() {
    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categories = IntArray(30) { it + 1 }.map { it.toString() }

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

        this.binding.aaChartView.aa_drawChartWithChartModel(chart)
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