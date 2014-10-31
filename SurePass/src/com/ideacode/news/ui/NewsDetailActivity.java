package com.ideacode.news.ui;

import java.util.HashMap;

//import net.youmi.android.banner.AdSize;
//import net.youmi.android.banner.AdView;
//import net.youmi.android.banner.AdViewLinstener;
//import net.youmi.android.offers.OffersAdSize;
//import net.youmi.android.offers.OffersBanner;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ideacode.news.R;
import com.ideacode.news.app.AppContext;
import com.ideacode.news.bean.NewsDetail;
import com.ideacode.news.common.util.CommonSetting;
import com.ideacode.news.common.util.StringUtils;
import com.ideacode.news.common.util.UIHelper;
import com.ideacode.news.logic.IdeaCodeActivity;
import com.ideacode.news.logic.MainService;
import com.ideacode.news.logic.Task;
import com.ideacode.news.logic.TaskType;

public class NewsDetailActivity extends IdeaCodeActivity {

	private final static int DATA_LOAD_ING = 0x001;
	private final static int DATA_LOAD_COMPLETE = 0x002;

	// �Ƿ���ˢ�±�־
	private boolean isRefresh = false;

	/**
	 * �����ͷ��FrameLayout ������ĸ��ؼ�
	 */
	private RelativeLayout mHeader;
	private ImageButton mBack;
	private TextView mHeadTitle;
	private ProgressBar mProgressbar;
	private ImageButton mRefresh;

	private ScrollView mScrollView;
	private TextView mTitle;
	private TextView mAuthor;
	private TextView mPubDate;
	private WebView mWebView;

	private GestureDetector gd;
	private boolean isFullScreen;

	/**
	 * NewsDetail bean
	 */
	private NewsDetail newsDetail;
	/**
	 * ͨ��intent���ݹ�����news_id��ֵ
	 */
	private String newsDetail_url;
	private int newsType;
	private long _uid;
	private AppContext appContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_detail);
		appContext = (AppContext) getApplication();

		this.initView();
		this.initData();

		// ע��˫��ȫ���¼�
		this.regOnDoubleEvent();

        // ���׹������
        this.configYoumiAd();
	}

	/**
	 * ��ʼ���ؼ�
	 * 
	 * @author Simon Xu 2013-4-19����2:16:43
	 */
	private void initView() {
		newsDetail_url = getIntent().getStringExtra("newsDetail_url");
		newsType = getIntent().getIntExtra("newsType", 0);

		mHeader = (RelativeLayout) findViewById(R.id.news_detail_header);
		mBack = (ImageButton) findViewById(R.id.main_head_back_button);
		mBack.setOnClickListener(new NewsDetailOnClickListener());
		mHeadTitle = (TextView) findViewById(R.id.systv);
		mHeadTitle.setText(R.string.news_dedail_title);
		mProgressbar = (ProgressBar) findViewById(R.id.main_head_progress);
		mRefresh = (ImageButton) findViewById(R.id.main_head_refresh_button);
		mRefresh.setOnClickListener(new NewsDetailOnClickListener());

		mScrollView = (ScrollView) findViewById(R.id.news_detail_scrollview);
		mTitle = (TextView) findViewById(R.id.news_detail_title);
		mAuthor = (TextView) findViewById(R.id.news_detail_author);
		mPubDate = (TextView) findViewById(R.id.news_detail_date);
		mWebView = (WebView) findViewById(R.id.news_detail_webview);
		mWebView.getSettings().setJavaScriptEnabled(false);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setDefaultFontSize(15);
	}

	/**
	 * ��ʼ���ؼ�����
	 * 
	 * @author Simon Xu 2013-4-19����4:47:30
	 */
	private void initData() {
		loadNewsDetailData(newsDetail_url, false);
	}

	/**
	 * ����������ϸ����
	 * 
	 * @author Simon Xu 2013-4-19����3:17:52
	 */
	private void loadNewsDetailData(final String newsDetail_url, boolean isRefresh) {
		headButtonSwitch(DATA_LOAD_ING);
		HashMap params = new HashMap();
		params.put("newsType", newsType);
		params.put("newsDetail_url", newsDetail_url);
		params.put("isRefresh", isRefresh);
		Task ts = new Task(TaskType.TS_EXAM_SEARCH_NEWS_DETAIL_LOAD, params);
		MainService.newTask(ts);
	}

	/**
	 * ͷ����ťչʾ
	 * 
	 * @author Simon Xu
	 * @param type
	 *            2013-4-19����2:27:11
	 */
	private void headButtonSwitch(int type) {
		switch (type) {
		case DATA_LOAD_ING:
			mScrollView.setVisibility(View.GONE);
			mProgressbar.setVisibility(View.VISIBLE);
			mRefresh.setVisibility(View.GONE);
			break;
		case DATA_LOAD_COMPLETE:
			mScrollView.setVisibility(View.VISIBLE);
			mProgressbar.setVisibility(View.GONE);
			mRefresh.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	/**
	 * OnClickListener�¼�����
	 * 
	 * @author Simon Xu
	 * 
	 *         2013-4-19����5:29:30
	 */
	private class NewsDetailOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.main_head_back_button:
				finish();
				break;
			case R.id.main_head_refresh_button:
				headButtonSwitch(DATA_LOAD_ING);
				isRefresh = true;
				loadNewsDetailData(newsDetail_url, isRefresh);
				break;
			}
		}
	}

	/**
	 * ע��˫��ȫ���¼�
	 * 
	 * @author Simon Xu 2013-4-19����6:02:56
	 */
	private void regOnDoubleEvent() {
		gd = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onDoubleTap(MotionEvent e) {
				isFullScreen = !isFullScreen;
				if (!isFullScreen) {
					WindowManager.LayoutParams params = getWindow().getAttributes();
					params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
					getWindow().setAttributes(params);
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
					mHeader.setVisibility(View.VISIBLE);
				} else {
					WindowManager.LayoutParams params = getWindow().getAttributes();
					params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
					getWindow().setAttributes(params);
					getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
					mHeader.setVisibility(View.GONE);
				}
				return true;
			}
		});
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		gd.onTouchEvent(event);
		return super.dispatchTouchEvent(event);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int order = item.getOrder();
        if (order == getResources().getInteger(R.integer.menu_share_index)) {
            if (newsDetail_url.equals("")) {
                UIHelper.ToastMessage(this, R.string.msg_read_detail_fail);
                return false;
            }
            UIHelper.showShare(NewsDetailActivity.this, newsDetail.getNewsDetailsTitle(), newsDetail.getNewsDetailsUrl());
        }
        if (order == getResources().getInteger(R.integer.menu_favourite_index)) {
            if (newsDetail_url.equals("")) {
                return false;
            }

            if (!appContext.isLogin()) {
                UIHelper.showLoginDialog(NewsDetailActivity.this);
            } else {
                mProgressbar.setVisibility(View.VISIBLE);
                mRefresh.setVisibility(View.GONE);
                _uid = appContext.getLoginUid();
                HashMap params = new HashMap();
                params.put("uid", _uid);
                params.put("newsDetail", newsDetail);
                Task ts = new Task(TaskType.TS_EXAM_NEWS_FAVOURITE, params);
                MainService.newTask(ts);
            }
        }
        return super.onContextItemSelected(item);
    }

    private void configYoumiAd() {
        // (��ѡ)ʹ�û���Mini Banner-һ���µĻ���ǽ��ڵ㣬��ʱ������û���ע�µĻ��ֹ��
//        OffersBanner mMiniBanner = new OffersBanner(this, OffersAdSize.SIZE_MATCH_SCREENx32);
        RelativeLayout layoutOffersMiniBanner = (RelativeLayout) findViewById(R.id.OffersMiniBannerLayout);
        layoutOffersMiniBanner.setVisibility(View.GONE);
//        layoutOffersMiniBanner.addView(mMiniBanner);

        // ������ӿڵ���
        // �������adView��ӵ���Ҫչʾ��layout�ؼ���
        LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
        adLayout.setVisibility(View.GONE);
//        AdView adView = new AdView(this, AdSize.SIZE_320x50);
//        adLayout.addView(adView);
        // ����������ӿ�
//        adView.setAdListener(new AdViewLinstener() {
//
//            @Override
//            public void onSwitchedAd(AdView arg0) {
//                Log.i("YoumiSample", "������л�");
//            }
//
//            @Override
//            public void onReceivedAd(AdView arg0) {
//                Log.i("YoumiSample", "������ɹ�");
//
//            }
//
//            @Override
//            public void onFailedToReceivedAd(AdView arg0) {
//                Log.i("YoumiSample", "������ʧ��");
//            }
//        });
    }

    @Override
	public void init() {

	}

	@Override
	public void refresh(Object... param) {
		int type = (Integer) param[0];
		switch (type) {
		case TaskType.TS_EXAM_SEARCH_NEWS_DETAIL_LOAD:
			if (param[2] != null && (Integer) param[2] != 0) {
				finish();
				UIHelper.ToastMessage(this, R.string.http_exception_error);
			} else {
				newsDetail = (NewsDetail) param[1];
				if (newsDetail == null || newsDetail.getNewsDetailsTitle() == null) {
					finish();
					UIHelper.ToastMessage(NewsDetailActivity.this, R.string.msg_load_is_null);
				} else {
					headButtonSwitch(DATA_LOAD_COMPLETE);

					mTitle.setText(newsDetail.getNewsDetailsTitle());
					mAuthor.setText(newsDetail.getNewsDetailsAuthor());
					mPubDate.setText(StringUtils.friendly_time(newsDetail.getNewsDetailsCreateDate()));

					String body = UIHelper.WEB_STYLE + newsDetail.getNewsDetailsBody();
					// ��ȡ�û����ã��Ƿ��������ͼƬ--Ĭ����wifi��ʼ�ռ���ͼƬ
					boolean isLoadImage;
					AppContext ac = (AppContext) getApplication();
					if (AppContext.NETTYPE_WIFI == ac.getNetworkType()) {
						isLoadImage = true;
					} else {
						isLoadImage = ac.isLoadImage();
					}

					if (isLoadImage) {
						// ���˵� img��ǩ��width,height����
						body = body.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
						body = body.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");
					} else {
						// ���˵� img��ǩ
						body = body.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");
					}

					mWebView.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
				}
			}
			break;
		case TaskType.TS_EXAM_NEWS_FAVOURITE:
			mProgressbar.setVisibility(View.GONE);
			mRefresh.setVisibility(View.VISIBLE);
			if (param[2] != null && (Integer) param[2] != 0) {
				UIHelper.ToastMessage(this, R.string.http_exception_error);
			} else {
				int code = (Integer) param[1];
				if (code == CommonSetting.Success) {
					UIHelper.ToastMessage(this, R.string.favourite_sucess);
				} else {
					UIHelper.ToastMessage(this, R.string.favourite_fail);
				}
			}
			break;
		}
	}
}
