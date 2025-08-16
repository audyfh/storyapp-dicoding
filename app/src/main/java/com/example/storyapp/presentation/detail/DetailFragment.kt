package com.example.storyapp.presentation.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import coil3.load
import com.example.storyapp.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val storyId = args.storyId
        viewModel.getStory(storyId)
        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.detailUiState.collect { uiState ->
                        binding.apply {
                            progressBar.isVisible = uiState.isLoading
                        }
                        if (uiState.isSuccess && uiState.story != null) {
                            val story = uiState.story
                            binding.apply {
                                ivStory.load(story.photoUrl)
                                tvName.text = story.name
                                tvDesc.text = story.description
                            }
                        }
                        if (uiState.isError) {
                            Toast.makeText(
                                requireContext(),
                                uiState.errorMessage,
                                Toast.LENGTH_SHORT
                            )
                        }
                    }
                }
            }
        }
    }


}