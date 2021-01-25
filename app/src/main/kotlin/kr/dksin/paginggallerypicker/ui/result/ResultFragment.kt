package kr.dksin.paginggallerypicker.ui.result

import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kr.dksin.paginggallerypicker.R
import kr.dksin.paginggallerypicker.base.BaseFragment
import kr.dksin.paginggallerypicker.databinding.FragmentResultBinding
import kr.dksin.paginggallerypicker.ext.getSupportActionBar
import kr.dksin.paginggallerypicker.ext.setSupportActionBar
import kr.dksin.paginggallerypicker.ui.gallery.GalleryFragment
import javax.inject.Inject

@AndroidEntryPoint
class ResultFragment : BaseFragment<ResultViewModel, FragmentResultBinding>(R.layout.fragment_result, ResultViewModel::class.java) {

    @Inject
    lateinit var resultAdapter: ResultAdapter

    private val args: ResultFragmentArgs by navArgs()

    override fun initView(savedInstanceState: Bundle?) {
        with(binding){

            toolbar.apply {
                setSupportActionBar(this)
                getSupportActionBar()?.run {
                    setDisplayShowTitleEnabled(false)
                    setDisplayShowHomeEnabled(false)
                    setDisplayHomeAsUpEnabled(false)
                }

                setBackgroundColor(Color.WHITE)
                setNavigationIcon(R.drawable.baseline_close_black_24dp)
                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
            }

            resultViewPager.apply {
                adapter = resultAdapter.apply {
                    submitList(args.galleryDatas.toList())
                }
                offscreenPageLimit = 5

            }
        }
    }

    override fun initObserve() {

        with(viewModel){
            currentPage.observe(viewLifecycleOwner, Observer { position ->
                binding.toolbarTitle.text = "(${position+1}/${args.galleryDatas.size})"
            })

        }
    }


}