package com.example.android.coding.challenge.ui.photodetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.android.coding.challenge.R
import com.example.android.coding.challenge.api.Result
import com.example.android.coding.challenge.databinding.FragmentViewPhotosDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Use the [ViewPhotosDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ViewPhotosDetailFragment : Fragment() {
    private lateinit var binding: FragmentViewPhotosDetailBinding
    private val viewModel: PhotoDetailsViewModel by viewModels()
    val args: ViewPhotosDetailFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewPhotosDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.photo = args.photo
        binding.emptyString = ""
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.getPhotoDetails(binding.photo!!.id)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getPhotoDetailsResult.collect {
                    when (it) {
                        is Result.Success -> {
                            binding.photoDetails = it.data
                            binding.progressBar.isVisible = false
                        }
                        is Result.Error -> {
                            binding.progressBar.isVisible = false
                            Toast.makeText(
                                requireActivity(),
                                getString(R.string.network_error_msg),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}