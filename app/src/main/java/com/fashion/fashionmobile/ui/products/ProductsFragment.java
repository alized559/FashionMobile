package com.fashion.fashionmobile.ui.products;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.fashion.fashionmobile.MainActivity;
import com.fashion.fashionmobile.R;
import com.fashion.fashionmobile.adapters.CategoryAdapter;
import com.fashion.fashionmobile.adapters.ViewPagerAdapter;
import com.fashion.fashionmobile.databinding.FragmentProductsBinding;
import com.fashion.fashionmobile.helpers.CategoryDataModel;
import com.google.android.material.tabs.TabLayout;

public class ProductsFragment extends Fragment {

    private FragmentProductsBinding binding;
    private TabLayout tab;
    private ViewPager viewPager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tab = root.findViewById(R.id.tab);
        viewPager = root.findViewById(R.id.viewPager);
        tab.setupWithViewPager(viewPager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new ProductsWomenFragment(), "Women");
        adapter.addFragment(new ProductsMenFragment(), "Men");
        viewPager.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}