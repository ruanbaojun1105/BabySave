package mybaby.ui.community.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.xmlrpc.android.XMLRPCException;

import java.util.ArrayList;

import me.hibb.mybaby.android.R;
import mybaby.models.community.activity.ReplyActivity;
import mybaby.rpc.BaseRPC.Callback;
import mybaby.rpc.community.ReplyRPC;
import mybaby.rpc.community.ReportRPC.ReportType;
import mybaby.ui.community.customclass.CustomAbsClass;
import mybaby.ui.community.customclass.CustomLinkTextView;
import mybaby.ui.widget.RoundImageViewByXfermode;
import mybaby.util.DateUtils;
import mybaby.util.DialogShow;
import mybaby.util.ImageViewUtil;
import mybaby.util.MaterialDialogUtil;
import mybaby.util.Utils;

/**
 * 自定义列表适配器
 * 
 * @author baojun
 * 
 */
public class DetailReplyAdapter extends BaseQuickAdapter<ReplyActivity> {

	/**
	 * 上下文
	 */
	private EditText editReply;
	private TextView comment_tag;
	private RecyclerView listView;
	private Context context;
	/**
	 * 1 2
	 */


	public DetailReplyAdapter(Context context, EditText editReply, TextView comment_tag, RecyclerView listView) {
		super(R.layout.detail_replyitem,new ArrayList<ReplyActivity>());
		this.context = context;
		this.editReply = editReply;
		this.comment_tag = comment_tag;
		this.listView = listView;
	}

	@Override
	protected void convert(BaseViewHolder baseViewHolder, ReplyActivity replyActivity) {
		//ReplyViewHolder viewHolder=new ReplyViewHolder(baseViewHolder.convertView);
		bindData(baseViewHolder, replyActivity);
	}
	/*userimage = (RoundImageViewByXfermode) convertView.findViewById(R.id.userimage);
	time = (TextView) convertView.findViewById(R.id.time);
	username = (CustomLinkTextView) convertView.findViewById(R.id.username);
	reply_username = (CustomLinkTextView) convertView.findViewById(R.id.reply_username);
	content = (CustomLinkTextView) convertView.findViewById(R.id.content);
	text = (TextView) convertView.findViewById(R.id.text);
	showreplytag = (TextView) convertView.findViewById(R.id.showreplytag);*/
	private  void  bindData(final BaseViewHolder baseViewHolder, final ReplyActivity item){
		baseViewHolder.setVisible(R.id.showreplytag,item.getUser().isSelf() ? false : true);
		if (item.getReplyUser() != null) {
			if (item.getReplyUser().getName() != null) {
				baseViewHolder.setText(R.id.text,"回复");
				baseViewHolder.setText(R.id.username,item.getReplyUser().getName());
			}
		} else {
			baseViewHolder.setText(R.id.text,"");
			baseViewHolder.setText(R.id.username,"");
		}
		baseViewHolder.setText(R.id.reply_username,item.getUser().getName());
		baseViewHolder.setText(R.id.time,DateUtils.getCountnumber(item.getDatetime()));
		baseViewHolder.setOnClickListener(R.id.userimage,new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CustomAbsClass.starUserPage(context, item.getUser());
			}
		});
		baseViewHolder.setText(R.id.content,item.getContent());
		ImageViewUtil.displayImage(item.getUser().getAvatarThumbnailUrl(), (ImageView) baseViewHolder.getView(R.id.userimage));

		baseViewHolder.convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				toReply((TextView) baseViewHolder.getView(R.id.reply_username), item, baseViewHolder.getAdapterPosition());
			}
		});

		baseViewHolder.convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub

				if (!TextUtils.isEmpty(item.getContent())) {
					ClipboardManager cmb = (ClipboardManager) context
							.getSystemService(Context.CLIPBOARD_SERVICE);
					if (cmb != null)
						cmb.setText(item.getContent());
					//Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show();
				}

				String[] itemsMe = {"删除"};
				String[] itemsNotMe = {"回复",
						"查看 \b" + item.getUser().getName(), "举报"};
				MaterialDialogUtil.showListDialog(context, null, item.getUser().isSelf() ? itemsMe : itemsNotMe, new MaterialDialogUtil.DialogListListener() {
					@Override
					public void todosomething(int which) {
						int id = item.getReplyId();
						if (which == 0) { // add baby
							if (item.getUser().isSelf()) {
								deleTopic(baseViewHolder.getAdapterPosition(), id);
							} else {
								toReply((TextView) baseViewHolder.getView(R.id.reply_username), item, baseViewHolder.getAdapterPosition());
							}
						} else if (which == 1) {
							CustomAbsClass.starUserPage(context, item.getUser());
						} else if (which == 2) {
							new DialogShow((Activity) context)
									.showReportDialog(
											ReportType.User, id, id);
						}
					}
				});
				return true;
			}
		});
	}


	/**
	 * 判断回复
	 */
	private void toReply(final TextView textView, final ReplyActivity item, final int position){
		new CustomAbsClass.StarEdit() {

			@Override
			public void todo() {
				// TODO Auto-generated method stub
				replyPeople(textView, item, position);
			}
		}.StarTopicEdit(context);
	}
	
	/**
	 * 回复
	 */
	private void replyPeople(TextView textView, ReplyActivity item, final int position) {
		// TODO Auto-generated method stub
		editReply.setHint("回复\t"
				+ textView.getText().toString() + "\t");
		editReply.setTag(item.getReplyId());
		comment_tag.setText("回复");
		Utils.Loge("reply_id:" + item.getReplyId());
		editReply.requestFocus();
		// 隐藏软键盘
		final InputMethodManager imm = (InputMethodManager) context
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		imm.showSoftInput(editReply, InputMethodManager.SHOW_FORCED);
		//DetailsActivity.myCuLv.setSelection(position);
		editReply.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (position!=0)
					listView.scrollToPosition(position);
			}
		},100);
	}
	/**
	 * 删除评论
	 */
	private void deleTopic(final int position, int id) {
		ReplyRPC.delete(id, new Callback() {

			@SuppressLint("ShowToast")
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				new CustomAbsClass.doSomething((Activity) context) {

					@Override
					public void todo() {
						// TODO Auto-generated method stub
						Toast.makeText(context, R.string.okdelect, Toast.LENGTH_SHORT).show();
						remove(position-1);
					}
				};
			}

			@Override
			public void onFailure(long id, XMLRPCException error) {
				// TODO Auto-generated method stub
				new CustomAbsClass.doSomething((Activity) context) {

					@SuppressLint("ShowToast")
					@Override
					public void todo() {
						// TODO Auto-generated method stub
						Toast.makeText(context, R.string.baddelect, Toast.LENGTH_SHORT).show();
					}
				};
			}
		});
	}

	/**
	 * 加载回复视图
	 * 
	 * @author baojun
	 * 
	 */
	static class ReplyViewHolder {
		RoundImageViewByXfermode userimage;
		TextView time, text,showreplytag;
		CustomLinkTextView username, content, reply_username;

		public ReplyViewHolder(View convertView) {
			userimage = (RoundImageViewByXfermode) convertView.findViewById(R.id.userimage);
			time = (TextView) convertView.findViewById(R.id.time);
			username = (CustomLinkTextView) convertView.findViewById(R.id.username);
			reply_username = (CustomLinkTextView) convertView.findViewById(R.id.reply_username);
			content = (CustomLinkTextView) convertView.findViewById(R.id.content);
			text = (TextView) convertView.findViewById(R.id.text);
			showreplytag = (TextView) convertView.findViewById(R.id.showreplytag);
		}
	}
}
