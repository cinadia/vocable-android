package com.willowtree.vocable.presets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.willowtree.vocable.BaseFragment
import com.willowtree.vocable.R
import com.willowtree.vocable.customviews.CategoryButton
import com.willowtree.vocable.customviews.PointerListener
import com.willowtree.vocable.databinding.CategoriesFragmentBinding
import com.willowtree.vocable.databinding.CategoryButtonBinding
import com.willowtree.vocable.room.Category

class CategoriesFragment : BaseFragment() {

    companion object {
        const val KEY_CATEGORIES = "KEY_CATEGORIES"

        fun newInstance(categories: List<Category>): CategoriesFragment {
            return CategoriesFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(KEY_CATEGORIES, ArrayList(categories))
                }
            }
        }
    }

    private var binding: CategoriesFragmentBinding? = null
    private lateinit var viewModel: PresetsViewModel
    private val allViews = mutableListOf<View>()
    private var maxCategories = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CategoriesFragmentBinding.inflate(inflater, container, false)

        maxCategories = resources.getInteger(R.integer.max_categories)
        val isTablet = resources.getBoolean(R.bool.is_tablet)

        val categories = arguments?.getParcelableArrayList<Category>(KEY_CATEGORIES)
        categories?.forEachIndexed { index, category ->
            val categoryButton =
                CategoryButtonBinding.inflate(inflater, binding?.categoryButtonContainer, false)
            with(categoryButton.root as CategoryButton) {
                tag = category
                text = category.name
                action = {
                    viewModel.onCategorySelected(category)
                }
                if (!isTablet && index + 1 == maxCategories) {
                    layoutParams = (layoutParams as LinearLayout.LayoutParams).apply {
                        marginStart = 0
                    }
                }
            }
            binding?.categoryButtonContainer?.addView(categoryButton.root)
        }
        categories?.let {
            for (i in 0 until maxCategories - it.size) {
                val hiddenButton =
                    CategoryButtonBinding.inflate(inflater, binding?.categoryButtonContainer, false)
                binding?.categoryButtonContainer?.addView(hiddenButton.root.apply {
                    isEnabled = false
                    visibility = View.INVISIBLE
                })
            }
        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(PresetsViewModel::class.java)
        subscribeToViewModel()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun subscribeToViewModel() {
        viewModel.selectedCategory.observe(viewLifecycleOwner, Observer { category ->
            category?.let {
                binding?.categoryButtonContainer?.children?.forEach {
                    if (it is CategoryButton) {
                        it.isSelected = (it.tag as? Category)?.identifier == category.identifier
                    }
                }
            }
        })
    }

    override fun getAllViews(): List<View> {
        if (allViews.isEmpty()) {
            getAllChildViews(binding?.categoryButtonContainer)
        }
        return allViews
    }

    private fun getAllChildViews(viewGroup: ViewGroup?) {
        viewGroup?.children?.forEach {
            if (it is PointerListener) {
                allViews.add(it)
            } else if (it is ViewGroup) {
                getAllChildViews(it)
            }
        }
    }
}