package com.example.storyapp.presentation.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.databinding.FragmentHomeBinding
import com.example.storyapp.presentation.auth.LoginActivity
import com.example.storyapp.util.PreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StoryAdapter
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeState()
        setClickListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupRecyclerView() {
        adapter = StoryAdapter()

        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = this@HomeFragment.adapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    this@HomeFragment.adapter.retry()
                }
            )
        }

        adapter.setOnItemClickCallback { story ->
            val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(story.id)
            findNavController().navigate(action)
        }
    }

    private fun observeState() {

        viewModel.stories.observe(viewLifecycleOwner) { pagingData ->
            adapter.submitData(lifecycle, pagingData)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                val refreshState = it.refresh
                when (refreshState) {
                    is LoadState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.btnRetry.visibility = View.GONE
                    }

                    is LoadState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnRetry.visibility = View.VISIBLE
                        Toast.makeText(
                            requireContext(),
                            refreshState.error.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnRetry.visibility = View.GONE
                    }
                }
            }
        }
    }


    private fun setClickListener() {
        binding.apply {
            btnLogout.setOnClickListener {
                PreferencesManager.clearToken(requireContext())
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }

            btnRetry.setOnClickListener {
                this@HomeFragment.adapter.retry()
            }
        }
    }
}