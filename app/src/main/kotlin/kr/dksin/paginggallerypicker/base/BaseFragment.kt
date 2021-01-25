package kr.dksin.paginggallerypicker.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.orhanobut.logger.Logger
import kr.dksin.paginggallerypicker.BR

abstract class BaseFragment<VM : BaseViewModel, B : ViewDataBinding>(@LayoutRes private val layoutResId: Int, private val viewModelClass: Class<VM>) : Fragment() {

    protected val viewModel: VM by lazy {
        ViewModelProvider(this).get(viewModelClass)
    }

    private var _binding: B? = null
    protected val binding: B get() = _binding ?: throw IllegalStateException("Trying to access the binding outside of the view lifecycle")

    protected abstract fun initView(savedInstanceState: Bundle?)
    protected abstract fun initObserve()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        DataBindingUtil.inflate<B>(inflater, layoutResId, container, false).also {
            it.setVariable(BR.viewModel, viewModel)
            it.lifecycleOwner = viewLifecycleOwner
            _binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Logger.w("base onViewCreated")
        initView(savedInstanceState)
        initObserve()

    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}