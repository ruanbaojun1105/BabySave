package mybaby.util;

import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

import org.xutils.common.util.LogUtil;

import me.hibb.mybaby.android.R;
import mybaby.ui.MyBabyApp;

public class TextViewUtil {

	/**
	 * 统一设置小箭头的标签
	 * 默认右箭头
	 */
	public static void setTagIcon(TextView textView,Context context){
		textView.setTypeface(MyBabyApp.fontAwesome);
		textView.setText(context.getString(R.string.fa_angle_right));
	}
	public  static void setTagIcon(TextView textView ,Context context,int resId){
		textView.setTypeface(MyBabyApp.fontAwesome);
		textView.setText(context.getString(resId));
	}
private int measureTextViewHeight(TextView textView,int deviceWidth) {
	    
	    int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST);
	    int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
	    textView.measure(widthMeasureSpec, heightMeasureSpec);
	    return textView.getMeasuredHeight();
	}

/**
 * 内容中的文字可点击的TEXTVIEW
 */
public void getSpecilText(final Context context, final TextView textView,final String title,final boolean isDatilPage) {
	ViewTreeObserver observer = textView.getViewTreeObserver(); //textAbstract为TextView控件
	observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		
		@Override
		public void onGlobalLayout() {
			// TODO Auto-generated method stub
			ViewTreeObserver obs = textView.getViewTreeObserver();
			obs.removeGlobalOnLayoutListener(this);
			boolean isMax=textView.getLineCount() > 6?true:false;
			LogUtil.e(textView.getLineCount()+"");
			textView.setTag(R.id.lineCounts,textView.getLineCount());
			if (!isDatilPage) {
				if (isMax) {
				int lineEndIndex = textView.getLayout().getLineEnd(5); //设置第六行打省略号
				String text = textView.getText().subSequence(0, lineEndIndex-2) +"全文";
				
//				/setShowQw(isDatilPage, isMax,textView,title,text);	
				}
			}
			
		}
	});

	// 设置超链接
	// sp.setSpan(new URLSpan(communityData.getItem().getContent()), 5, 7,
	// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	// 设置高亮样式一
	// sp.setSpan(new BackgroundColorSpan(Color.RED), 17
	// ,19,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

	// 设置高亮样式二
	/*
	 * sp.setSpan(new ForegroundColorSpan(Color.BLUE),
	 * communityData.getItem().getContent().length(),
	 * communityData.getItem().getContent().length()+2,
	 * Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
	 */
	// sp.setSpan(new Clickable(l), start, end,
	// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	// 设置斜体
	// sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 27,
	// 29, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
	// SpannableString对象设置给TextView
	/*textView.setMovementMethod(CustomLinkTextView.LocalLinkMovementMethod
			.getInstance());*/

}

/*void setShowQw(boolean isDatilPage,boolean isMax,TextView textView,String title,String content){
	if (!isDatilPage) {
		
		if (isMax) {
			String xxx="全文";
			textView.setText(content);
			textView.setTag(R.id.contentQw,content);
			SpannableString sp = new SpannableString(text);
			sp.setSpan(new ForegroundColorSpan(context.getResources()
						.getColor(R.color.gray_1)), text.length()-2,text.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			List<Link> links = new ArrayList<Link>();
			Link titleLink = new Link(title);
			titleLink.setTextColor(Color.parseColor("#259B24"));
			titleLink.setOnLongClickListener(new Link.OnLongClickListener() {
	            @Override
	            public void onLongClick(String clickedText) {
	            }
	        });
			Link xxxQ = new Link(xxx);
			xxxQ.setTextColor(Color.parseColor("#259B24"));
			xxxQ.setOnLongClickListener(new Link.OnLongClickListener() {
	            @Override
	            public void onLongClick(String clickedText) {
	            }
	        });
	        links.add(titleLink);
	        links.add(xxxQ);
	        LinkBuilder.on(textView,false)
            .addLinks(links)
            .build();
			
			textView.setMovementMethod(TouchableMovementMethod.getInstance());
			
		}
	}
	
}*/
}
