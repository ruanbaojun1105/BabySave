package mybaby.ui.community.fragment;

public class FragmentHeaderUtil {

	/**
	 * 头部刷新UI
	 * @param context
	 * @param mPtrFrameLayout
	 */
	/*public static void setHeader(Context context,PtrFrameLayout mPtrFrameLayout) {
		
		*//*final RentalsSunHeaderView header = new RentalsSunHeaderView(getActivity());
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, 0, 0, 0);
        header.setUp(mPtrFrameLayout);
        mPtrFrameLayout.setLoadingMinTime(300);//最短刷新时间
        mPtrFrameLayout.setDurationToCloseHeader(500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);*//*
        //mPtrFrameLayout.setKeepHeaderWhenRefresh(false);
		// header
        final MaterialHeader header = new MaterialHeader(context);
        int[] colors = context.getResources().getIntArray(R.array.load_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, LocalDisplay.dp2px(10), 0, LocalDisplay.dp2px(10));
        header.setPtrFrameLayout(mPtrFrameLayout);
        mPtrFrameLayout.setLoadingMinTime(300);
        mPtrFrameLayout.setDurationToCloseHeader(500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        *//*mPtrFrameLayout.postDelayed(new Runnable() {
        @Override
        public void run() {
            mPtrFrameLayout.autoRefresh(false);
        }
    }, 0);*//*
	}
	*//**
	 * 底部刷新UI
	 * @param context
	 * @param loadMoreListViewContainer
	 *//*
    public static void setFooter(Context context,LoadMoreListViewContainer loadMoreListViewContainer,AbsListView.OnScrollListener listener) {
           setFooter(context,loadMoreListViewContainer,listener,true);
    }
	public static void setFooter(Context context,LoadMoreListViewContainer loadMoreListViewContainer) {
		 setFooter(context,loadMoreListViewContainer,null,true);
	}
        public static void setFooter(Context context,LoadMoreListViewContainer loadMoreListViewContainer,AbsListView.OnScrollListener listener,boolean statusLoadImage) {
                //loadMoreListViewContainer.useDefaultHeader();
                // 创建
                final View footerView = LayoutInflater.from(context).inflate(
                        R.layout.footer_cus_listview, null);
                loadMoreListViewContainer.setLoadMoreView(footerView);
                loadMoreListViewContainer.setShowLoadingForFirstPage(false);
                loadMoreListViewContainer.setLoadMoreUIHandler(new LoadMoreUIHandler() {
                        @Override
                        public void onLoading(LoadMoreContainer container) {
                                footerView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadFinish(LoadMoreContainer container, boolean empty, boolean hasMore) {
                                footerView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onWaitToLoadMore(LoadMoreContainer container) {
                                footerView.setVisibility(View.GONE);
                        }
                });
                //第一个参数就是我们的图片加载对象ImageLoader, 第二个是控制是否在滑动过程中暂停加载图片，如果需要暂停传true就行了，第三个参数控制猛的滑动界面的时候图片是否暂停加载
                if (statusLoadImage)
                        loadMoreListViewContainer.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), Build.VERSION.SDK_INT <18?true:false, true, listener));
                else
                        loadMoreListViewContainer.setOnScrollListener(listener);
        }*/
}
