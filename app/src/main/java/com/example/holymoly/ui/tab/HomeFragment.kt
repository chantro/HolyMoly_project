package com.example.holymoly.ui.tab

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.holymoly.HolyDay
import com.example.holymoly.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 * Use the [BucketListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@RequiresApi(Build.VERSION_CODES.O)
class HomeFragment : Fragment() , OnYearItemSelectedListener{
    private lateinit var binding : FragmentHomeBinding

    private val currentYear = LocalDate.now().year
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        //연도 combo box 초기화
        val yearList = setYearList()
        val yearAdapter = SolyearAdapter(requireContext(), binding.solyearSpinner, yearList)
        yearAdapter.setOnYearItemSelectedListener(this)

        return binding.root
    }

    //년도 list
    private fun setYearList(): List<String>{
        var yearList = mutableListOf<String>()
        for(i in 0..2){
            yearList.add((currentYear+i).toString())}

        return yearList
    }

    //년도 콤보박스 선택시 정보 업데이트
    override fun onYearItemSelected(yearItem: String) {
        setMainPage(yearItem)
    }

    //main page에 공휴일 정보 표시하기
    private fun setMainPage(year: String){
        GlobalScope.launch(Dispatchers.Main){
            //공휴일 정보 가져오기 - 생성자 값 setting 될 때까지 대기
            val holy = fetchHolyDay(year)
            val holidayDatas : List<List<String>>
            val datas_each_month_holidays : List<Int>
            val datas_each_month = mutableListOf<String>("Jan","Feb","March","April","May","June","July",
                "Aug","Sep","Oct","Nov","Dec")

            if(year == currentYear.toString()) { //올해일 경우
                //올해 남은 공휴일 수
                binding.restOfYear.text = holy.restHolyOfYear().toString()
                //이 달의 공휴일
                binding.holydaysOfMonthText.text = "이 달의 공휴일"
                holidayDatas = holy.HolyListOfMonth()
                datas_each_month_holidays = holy.restHolyOfMonth()
            }
            else{  //다른 년도일 경우
                binding.holydaysOfMonthText.text = year + "년도 첫 공휴일"
                binding.restOfYear.text = holy.totalHolyOfYear().toString() + "일"
                holidayDatas = holy.FirstHolyListOfMonth()
                datas_each_month_holidays = holy.totalHolyOfMonth()}

            //이달의 공휴일 리사이클러 뷰
            binding.holydaysOfMonthLayout.adapter = HolidayOfMonthAdapter(holidayDatas)
            binding.holydaysOfMonthLayout.layoutManager = LinearLayoutManager(requireContext())

            //각 달의 공휴일 리사이클러 뷰
            binding.holidaysOfEachMonthLayout.adapter = HolidayEachMonthAdapter(datas_each_month, datas_each_month_holidays)
            binding.holidaysOfEachMonthLayout.layoutManager = GridLayoutManager(activity,2)
    }}

    //HolyDay 생성자 패치
    private suspend fun fetchHolyDay(year: String): HolyDay{
        //HolyDay의 값을 비동기적으로 가져옴
        return withContext(Dispatchers.IO){
            HolyDay(year)
        }
    }

}