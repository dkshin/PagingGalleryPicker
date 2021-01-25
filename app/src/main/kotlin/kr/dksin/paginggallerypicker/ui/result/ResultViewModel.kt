package kr.dksin.paginggallerypicker.ui.result

import androidx.databinding.ObservableInt
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.widget.ViewPager2
import kr.dksin.paginggallerypicker.base.BaseViewModel

class ResultViewModel @ViewModelInject constructor(): BaseViewModel(){

    private val _currentPage =
        MutableLiveData<Int>()
    val currentPage: LiveData<Int>
        get() = _currentPage

    val observableCurrentPage = ObservableInt()

    val pageChangeListener: ViewPager2.OnPageChangeCallback = object :  ViewPager2.OnPageChangeCallback(){
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            observableCurrentPage.set(position)
            _currentPage.postValue(position)
        }
    }

}