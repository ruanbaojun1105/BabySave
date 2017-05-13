package mybaby.ui.community.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import mybaby.ui.community.CommunityFragment;

public class CommunityFragmentPagerAdapter extends FragmentStatePagerAdapter{

	private String[] mTitles;
	private List<Fragment> mFragments;
	private FragmentManager fm;

	public CommunityFragmentPagerAdapter(FragmentManager fm, String[] mTitles, List<Fragment> mFragments) {
		super(fm);
		this.mTitles = mTitles;
		this.mFragments = mFragments;
		this.fm = fm;
	}

	/**
	 * 可动态刷新fragment
	 * @param fragments
	 */
	public void setFragments(List<Fragment> fragments) {
		if(this.mFragments != null){
			FragmentTransaction ft = fm.beginTransaction();
			for(Fragment f:this.mFragments){
				ft.remove(f);
			}
			ft.commit();
			ft=null;
			fm.executePendingTransactions();
		}
		this.mFragments = fragments;
		notifyDataSetChanged();
	}
	@Override
	public CharSequence getPageTitle(int position) {
		return mTitles[position];
	}

	/*@Override
	public Object instantiateItem(ViewGroup container, int position) {
		//得到缓存的fragment

		Fragment fragment = (Fragment)super.instantiateItem(container,
				position);
		//得到tag ❶
		String fragmentTag = fragment.getTag();
		if (fragmentsUpdateFlag[position %fragmentsUpdateFlag.length]) {
			//如果这个fragment需要更新
			FragmentTransaction ft =fm.beginTransaction();
			//移除旧的fragment
			ft.remove(fragment);
			//换成新的fragment
			fragment =fragments[position %fragments.length];
			//添加新fragment时必须用前面获得的tag ❶
			ft.add(container.getId(), fragment, fragmentTag);
			ft.attach(fragment);
			ft.commit();
			//复位更新标志
			fragmentsUpdateFlag[position %fragmentsUpdateFlag.length] =false;

		}



		return fragment;
	}*/

	@Override
	public Fragment getItem(int position) {
		Fragment page = null;
		if (mFragments.size() > position) {
			page = mFragments.get(position);
			if (page != null) {
				return page;
			}
		}

		while (position>=mFragments.size()) {
			mFragments.add(null);
		}
		switch (position%2) {
			case 0:
				page = CommunityFragment.getFragment1();
				mFragments.set(position, page);
				break;
			case 1:
				page = CommunityFragment.getFragment2();
				mFragments.set(position, page);
				break;
		}
		return page;
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
	}

}
