package com.auto_reply.template

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.auto_reply.R
import com.auto_reply.base.BaseActivity
import com.auto_reply.databinding.ActivityTemplateBinding

class TemplateActivity : BaseActivity() {

    lateinit var mBinding: ActivityTemplateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_template)
        setSupportActionBar(mBinding.toolbar);

        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        setupViewPager(mBinding.viewpager);

        mBinding.tabs.setupWithViewPager(mBinding.viewpager);
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager);
        adapter.addFragment(FragmentEnglishTemplate(), "English");
        adapter.addFragment(FragmentHindiTemplate(), "Hindi");
        viewPager.adapter = adapter;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
}

class ViewPagerAdapter(supportFragmentManager: FragmentManager) :
    FragmentPagerAdapter(supportFragmentManager) {
    var mFragmentList = ArrayList<Fragment>()
    var mFragmentTitleList = ArrayList<String>()

    public fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList.get(position)
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return mFragmentList.get(position);
    }
}