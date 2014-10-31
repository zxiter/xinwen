package com.ideacode.news.common.util;

import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.ideacode.news.R;
import com.ideacode.news.app.AppContext;
import com.ideacode.news.app.AppManager;
import com.ideacode.news.ui.About;
import com.ideacode.news.ui.FeedBack;
import com.ideacode.news.ui.LoginDialog;
import com.ideacode.news.ui.MainActivity;
import com.ideacode.news.ui.NewsDetailActivity;

public class UIHelper {

	public final static int LISTVIEW_ACTION_INIT = 0x01;
	public final static int LISTVIEW_ACTION_REFRESH = 0x02;
	public final static int LISTVIEW_ACTION_SCROLL = 0x03;
	public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;

	public final static int LISTVIEW_DATA_MORE = 0x01;
	public final static int LISTVIEW_DATA_LOADING = 0x02;
	public final static int LISTVIEW_DATA_FULL = 0x03;
	public final static int LISTVIEW_DATA_EMPTY = 0x04;
	public final static int LISTVIEW_DATA_ERROR = 0x05;

	public final static int LISTVIEW_DATATYPE_NEWS = 0x01;

	public final static int REQUEST_CODE_FOR_RESULT = 0x01;
	public final static int REQUEST_CODE_FOR_REPLY = 0x02;

	/** ����ͼƬƥ�� */
	private static Pattern facePattern = Pattern
			.compile("\\[{1}([0-9]\\d*)\\]{1}");

	/** ȫ��web��ʽ */
	public final static String WEB_STYLE = "<style>* {font-size:18px;line-height:25px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;} "
			+ "img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#fff;padding:2px;} "
			+ "pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;} "
			+ "a.tag {font-size:15px;text-decoration:none;background-color:#bbd6f3;border-bottom:2px solid #3E6D8E;border-right:2px solid #7F9FB6;color:#284a7b;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;}</style>";

	/**
	 * ��ʾ��ҳ
	 * 
	 * @param activity
	 */
	public static void showHome(Activity activity) {
		Intent intent = new Intent(activity, MainActivity.class);
		activity.startActivity(intent);
		activity.finish();
	}

	/**
	 * ��ʾ��¼ҳ��
	 * 
	 * @param activity
	 */
	public static void showLoginDialog(Context context) {
		Intent intent = new Intent(context, LoginDialog.class);
		// if(context instanceof NewsDetailActivity)
		// intent.putExtra("LOGINTYPE", LoginDialog.LOGIN_OTHER);
		// else if(context instanceof SettingActivity)
		// intent.putExtra("LOGINTYPE", LoginDialog.LOGIN_SETTING);
		// else
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * ��ʾ�û�����
	 * 
	 * @param context
	 */
	public static void showFeedBack(Context context) {
		Intent intent = new Intent(context, FeedBack.class);
		context.startActivity(intent);
	}

	/**
	 * �û���¼��ע��
	 * 
	 * @param activity
	 */
	public static void loginOrLogout(Activity activity) {
		AppContext ac = (AppContext) activity.getApplication();
		if (ac.isLogin()) {
			ac.Logout();
			ToastMessage(activity, "���˳���¼");
		} else {
			showLoginDialog(activity);
		}
	}

	/**
	 * ��ʾ��������
	 * 
	 * @param context
	 * @param newsId
	 */
	public static void showNewsDetail(Context context, int newsId) {
		Intent intent = new Intent(context, NewsDetailActivity.class);
		intent.putExtra("news_id", newsId);
		context.startActivity(intent);
	}

	/**
	 * ��ȡTextWatcher����
	 * 
	 * @param context
	 * @param tmlKey
	 * @return
	 */
	public static TextWatcher getTextWatcher(final Activity context,
			final String temlKey) {
		return new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// ���浱ǰEditText���ڱ༭������
				((AppContext) context.getApplication()).setProperty(temlKey,
						s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		};
	}

	/**
	 * ����ϵͳ��װ�˵�Ӧ�÷���
	 * 
	 * @param context
	 * @param title
	 * @param url
	 */
	public static void showShare(Activity context, final String title,
			final String url) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "����" + title);
		intent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
		context.startActivity(Intent.createChooser(intent, "ѡ�����"));
	}

	/**
	 * ����Toast��Ϣ
	 * 
	 * @param msg
	 */
	public static void ToastMessage(Context cont, String msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, int msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, String msg, int time) {
		Toast.makeText(cont, msg, time).show();
	}

	/**
	 * ���app����
	 * 
	 * @param activity
	 */
	public static void clearAppCache(Activity activity) {
		final AppContext ac = (AppContext) activity.getApplication();
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					ToastMessage(ac, "��������ɹ�");
				} else {
					ToastMessage(ac, "�������ʧ��");
				}
			}
		};
		new Thread() {
			@Override
			public void run() {
				Message msg = new Message();
				try {
					// ac.clearAppCache();
					msg.what = 1;
				} catch (Exception e) {
					e.printStackTrace();
					msg.what = -1;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	/**
	 * ����App�쳣��������
	 * 
	 * @param cont
	 * @param crashReport
	 */
	public static void sendAppCrashReport(final Context cont,
			final String crashReport) {
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_error);
		builder.setMessage(R.string.app_error_message);
		builder.setPositiveButton(R.string.submit_report,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// �����쳣����
						Intent i = new Intent(Intent.ACTION_SEND);
						// i.setType("text/plain"); //ģ����
						i.setType("message/rfc822"); // ���
						i.putExtra(Intent.EXTRA_EMAIL,
								new String[] { "jxsmallmouse@163.com" });
						i.putExtra(Intent.EXTRA_SUBJECT, "�꿼�ع�2.0��Ѷ��- ���󱨸�");
						i.putExtra(Intent.EXTRA_TEXT, crashReport);
						cont.startActivity(Intent.createChooser(i, "���ʹ��󱨸�"));
						// �˳�
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.setNegativeButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// �˳�
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.show();
	}

	/**
	 * �����Ƿ����ͼƬ��ʾ
	 * 
	 * @param activity
	 */
	public static void changeSettingIsLoadImage(Activity activity) {
		AppContext ac = (AppContext) activity.getApplication();
		if (ac.isLoadImage()) {
			ac.setConfigLoadimage(false);
			ToastMessage(activity, "���������²�����ͼƬ");
		} else {
			ac.setConfigLoadimage(true);
			ToastMessage(activity, "���������¼���ͼƬ");
		}
	}

	public static void changeSettingIsLoadImage(Activity activity, boolean b) {
		AppContext ac = (AppContext) activity.getApplication();
		ac.setConfigLoadimage(b);
	}

	/**
	 * ��ʾ��������
	 * 
	 * @param context
	 */
	public static void showAbout(Context context) {
		Intent intent = new Intent(context, About.class);
		context.startActivity(intent);
	}

	/**
	 * �˳�����
	 * 
	 * @param cont
	 */
	public static void Exit(final Context cont) {
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_menu_surelogout);
		builder.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// �˳�
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.setNegativeButton(R.string.cancle,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.show();
	}
}
