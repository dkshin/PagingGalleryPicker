package kr.dksin.paginggallerypicker.ui.home

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kr.dksin.paginggallerypicker.R
import kr.dksin.paginggallerypicker.base.BaseFragment
import kr.dksin.paginggallerypicker.databinding.FragmentHomeBinding
import kr.dksin.paginggallerypicker.ext.getSupportActionBar
import kr.dksin.paginggallerypicker.ext.setSupportActionBar

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>(R.layout.fragment_home, HomeViewModel::class.java) {

    private val requestGalleryPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        navigateToGallery()
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(binding){

            toolbar.apply {
                setSupportActionBar(this)
                getSupportActionBar()?.run {
                    setDisplayShowTitleEnabled(false)
                    setDisplayShowHomeEnabled(false)
                    setDisplayHomeAsUpEnabled(false)
                }

                toolbarTitle.text = getString(R.string.app_name)
            }

            homeGalleryButton.setOnClickListener {
                requestGalleryPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    override fun initObserve() {
    }

    private fun hasGalleryPermission(activity: Activity): Boolean {
        return (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    private fun navigateToGallery(){
        activity?.let{
            if(hasGalleryPermission(it)){
                findNavController().navigate(HomeFragmentDirections.actionHomeToGallery())
            }else{
                Toast.makeText(it, "permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }
}