package com.ideacode.news.logic;

/**
 * <p>
 * FileName: MainService.java
 * </p>
 * <p>
 * Description: ��̨�ܵ��ȷ�����
 * <p>
 * Copyright: IdeaCode(c) 2012
 * </p>
 * <p>
 * 
 * @author Vic Su
 *         </p>
 *         <p>
 * @content andyliu900@gmail.com
 *          </p>
 *          <p>
 * @version 1.0
 *          </p>
 *          <p>
 *          CreatDate: 2012-9-7 ����11:35:56
 *          </p>
 *          <p>
 *          Modification History
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.ideacode.news.app.AppException;
import com.ideacode.news.app.AppManager;
import com.ideacode.news.bean.Mood;
import com.ideacode.news.bean.NewsDetail;
import com.ideacode.news.bean.Paging;
import com.ideacode.news.bean.TbFeedBack;
import com.ideacode.news.bean.TbUser;
import com.ideacode.news.common.util.CommonSetting;
import com.ideacode.news.module.util.AppStartUtil;
import com.ideacode.news.module.util.FeedBackUtil;
import com.ideacode.news.module.util.FindUtil;
import com.ideacode.news.module.util.MemberUtil;
import com.ideacode.news.module.util.MoodUtil;
import com.ideacode.news.module.util.NewsUtil;
import com.ideacode.news.module.util.RegUtil;

public class MainService extends Service implements Runnable {

    public static boolean isrun = false;
    private static ArrayList<Task> allTask = new ArrayList<Task>();

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    // ��Ӵ��ڵ�������
    public static void addActivity(IdeaCodeActivity ia) {
        AppManager.getAppManager().addActivity(ia);
    }

    public static void removeActivity(IdeaCodeActivity ia) {
        AppManager.getAppManager().finishActivity(ia);
    }

    // �������
    public static void newTask(Task ts) {
        allTask.add(ts);
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        isrun = false;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        isrun = true;
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (isrun) {
            if (allTask.size() > 0) {
                doTask(allTask.get(0));
            } else {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {

                }
            }
        }
    }

    private void doTask(Task ts) {
        Message message = hand.obtainMessage();
        message.what = ts.getTaskID();
        switch (ts.getTaskID()) {
            case TaskType.TS_EXAM_GETINITIALIZEDATA: // ��������ʱ����ȡassset����ĳ�ʼ������
                IdeaCodeActivity appStart = (IdeaCodeActivity) ts.getTaskParam().get("context");
                try {
                    AppStartUtil.getProvinces(appStart);
                } catch (Exception e) {
                    e.printStackTrace();
                    message.arg1 = CommonSetting.InitSystemDataException;
                }
                message.obj = null;
                break;
            case TaskType.TS_EXAM_SEARCH_NEWS: // ��ѯ����
                try {
                    int newsType = (Integer) ts.getTaskParam().get("newsType");
                    int currentpage = (Integer) ts.getTaskParam().get("currentpage");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Map<String, Object>> newsList = NewsUtil.getNewsForList(this, newsType, currentpage, isRefresh);
                    message.obj = newsList;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_NEWS_MORE: // ��ѯ��������
                try {
                    int newsType = (Integer) ts.getTaskParam().get("newsType");
                    int currentpage = (Integer) ts.getTaskParam().get("currentpage");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Map<String, Object>> newsList = NewsUtil.getNewsForList(this, newsType, currentpage, isRefresh);
                    message.obj = newsList;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_NEWS_DETAIL_LOAD:// ��ѯ������ϸ����
                try {
                    int newsType = (Integer) ts.getTaskParam().get("newsType");
                    String newsDetail_url = (String) ts.getTaskParam().get("newsDetail_url");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    NewsDetail newsDetail = NewsUtil.getNewsDetailByUrl(this, newsType, newsDetail_url, isRefresh);
                    message.obj = newsDetail;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_NEWS_FAVOURITE: // �ղ�����
                try {
                    long uid = (Long) ts.getTaskParam().get("uid");
                    NewsDetail newsDetail = (NewsDetail) ts.getTaskParam().get("newsDetail");
                    int code = NewsUtil.addFavouriteNews(uid, newsDetail);
                    message.obj = code;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_MOOD: // ��ѯ����
                try {
                    Paging p = (Paging) ts.getTaskParam().get("paging");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Mood> moodList = MoodUtil.getMoodForList(this, p, isRefresh);
                    message.obj = moodList;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_MOOD_MORE: // ��ѯ��������
                try {
                    Paging p = (Paging) ts.getTaskParam().get("paging");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Mood> moodList = MoodUtil.getMoodForList(this, p, isRefresh);
                    message.obj = moodList;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_SEND_MOOD: // ��������
                try {
                    Mood mood = (Mood) ts.getTaskParam().get("mood");
                    int code = MoodUtil.addMood(mood);
                    message.obj = code;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_PRAISE_MOOD: // ��һ������
                try {
                    Mood mood = (Mood) ts.getTaskParam().get("mood");
                    int code = MoodUtil.addMoodPraise(mood);
                    message.obj = code;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_BELITTLE_MOOD: // ��һ������
                try {
                    Mood mood = (Mood) ts.getTaskParam().get("mood");
                    int code = MoodUtil.addMoodBelittle(mood);
                    message.obj = code;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_USER_MOOD: // ��ѯ�û�����
                try {
                    long userId = (Long) ts.getTaskParam().get("userId");
                    Paging p = (Paging) ts.getTaskParam().get("paging");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Mood> userMoodList = MoodUtil.getUserMoodForList(this, userId, p, isRefresh);
                    message.obj = userMoodList;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_USER_MOOD_MORE: // ��ѯ�����û�����
                try {
                    long userId = (Long) ts.getTaskParam().get("userId");
                    Paging p = (Paging) ts.getTaskParam().get("paging");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Mood> userMoodList = MoodUtil.getUserMoodForList(this, userId, p, isRefresh);
                    message.obj = userMoodList;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_USER_FAVOURITE: // ��ѯ�û��ղص�����
                try {
                    long userId = (Long) ts.getTaskParam().get("userId");
                    Paging p = (Paging) ts.getTaskParam().get("paging");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Map<String, Object>> newsList = MemberUtil.getUserFavouriteForList(this, userId, p, isRefresh);
                    message.obj = newsList;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_USER_FAVOURITE_MORE: // ��ѯ�����û��ղص�����
                try {
                    long userId = (Long) ts.getTaskParam().get("userId");
                    Paging p = (Paging) ts.getTaskParam().get("paging");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Map<String, Object>> newsList = MemberUtil.getUserFavouriteForList(this, userId, p, isRefresh);
                    message.obj = newsList;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_LOGIN: // �û���¼
                try {
                    TbUser tbUser = (TbUser) ts.getTaskParam().get("tbUser");
                    HashMap<String, Object> return_map = MemberUtil.login(tbUser);
                    message.obj = return_map;
                } catch (AppException e) {
                    message.arg1 = AppException.TYPE_SOAP;
                }
                break;
            case TaskType.TS_EXAM_USER_INFO: // ����û���Ϣ
                try {
                    long userId = (Long) ts.getTaskParam().get("userId");
                    TbUser tbUser = MemberUtil.getUserInfo(userId);
                    message.obj = tbUser;
                } catch (AppException e) {
                    message.arg1 = CommonSetting.SoapException;
                }
                break;
            case TaskType.TS_EXAM_UPDATEUSERINFO: // �����û���Ϣ
                try {
                    TbUser tbUser = (TbUser) ts.getTaskParam().get("tbUser");
                    int updateUser_code = MemberUtil.updateUserInfo(tbUser);
                    message.obj = updateUser_code;
                } catch (AppException e) {
                    message.arg1 = CommonSetting.SoapException;
                }
                break;
            case TaskType.TS_EXAM_CHECKUSER: // ����û�ע������
                try {
                    String reg_name = (String) ts.getTaskParam().get("reg_name");
                    String reg_email = (String) ts.getTaskParam().get("reg_email");

                    int email_code = RegUtil.checkUser(reg_name, reg_email);
                    message.obj = email_code;
                } catch (AppException e) {
                    message.arg1 = CommonSetting.SoapException;
                }
                break;
            case TaskType.TS_EXAM_REGUSER: // ע���û�
                try {
                    TbUser RegtbUser = (TbUser) ts.getTaskParam().get("tbUser");

                    Map reg_map = RegUtil.regUser(RegtbUser);
                    message.obj = reg_map;
                } catch (AppException e) {
                    message.arg1 = CommonSetting.SoapException;
                }
                break;
            case TaskType.TS_EXAM_SENDFEEDBACK: // �����û�������Ϣ
                try {
                    TbFeedBack tbFeedBack = (TbFeedBack) ts.getTaskParam().get("feedback");
                    FeedBackUtil.sendFeedBackInfo(tbFeedBack);
                    message.obj = CommonSetting.Success;
                } catch (AppException e) {
                    message.arg1 = CommonSetting.SoapException;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_POP_MOOD: // ��ѯ��������
                try {
                    Paging p = (Paging)ts.getTaskParam().get("paging");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Mood> moodList = FindUtil.getPopMoodForList(this, p, isRefresh);
                    message.obj = moodList;
                } catch (AppException e) {
                    message.arg1 = CommonSetting.SoapException;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_POP_MOOD_MORE: // ��ѯ������������
                try {
                    Paging p = (Paging) ts.getTaskParam().get("paging");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Mood> moodList = FindUtil.getPopMoodForList(this, p, isRefresh);
                    message.obj = moodList;
                } catch (AppException e) {
                    message.arg1 = CommonSetting.SoapException;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_POP_FAVOURITE: // ��ѯ�����ղ�
                try {
                    Paging p = (Paging) ts.getTaskParam().get("paging");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Map<String, Object>> favouriteList = FindUtil.getPopFavouriteForList(this, p, isRefresh);
                    message.obj = favouriteList;
                } catch (AppException e) {
                    message.arg1 = CommonSetting.SoapException;
                }
                break;
            case TaskType.TS_EXAM_SEARCH_POP_FAVOURITE_MORE: // ��ѯ���������ղ�
                try {
                    Paging p = (Paging) ts.getTaskParam().get("paging");
                    boolean isRefresh = (Boolean) ts.getTaskParam().get("isRefresh");
                    ArrayList<Map<String, Object>> favouriteList = FindUtil.getPopFavouriteForList(this, p, isRefresh);
                    message.obj = favouriteList;
                } catch (AppException e) {
                    message.arg1 = CommonSetting.SoapException;
                }
                break;
        }
        allTask.remove(ts);
        hand.sendMessage(message);
    }

    private final Handler hand = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TaskType.TS_EXAM_GETINITIALIZEDATA: // ��������ʱ����ȡassset����ĳ�ʼ������
                    AppManager.getAppManager().getActivityByName("AppStart").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEARCH_NEWS: // ��ѯ����
                    AppManager.getAppManager().getActivityByName("TabNewsActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEARCH_NEWS_MORE: // ��ѯ��������
                    AppManager.getAppManager().getActivityByName("TabNewsActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEARCH_NEWS_DETAIL_LOAD: // ��ѯ������ϸ����
                    if (AppManager.getAppManager().getActivityByName("NewsDetailActivity") != null) {
                        AppManager.getAppManager().getActivityByName("NewsDetailActivity").refresh(msg.what, msg.obj, msg.arg1);
                    }
                    break;
                case TaskType.TS_EXAM_NEWS_FAVOURITE: // �ղ�����
                    if (AppManager.getAppManager().getActivityByName("NewsDetailActivity") != null) {
                        AppManager.getAppManager().getActivityByName("NewsDetailActivity").refresh(msg.what, msg.obj, msg.arg1);
                    }
                    break;
                case TaskType.TS_EXAM_SEARCH_MOOD: // ��ѯ����
                    AppManager.getAppManager().getActivityByName("TabMoodActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEARCH_MOOD_MORE: // ��ѯ��������
                    AppManager.getAppManager().getActivityByName("TabMoodActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEND_MOOD: // ��������
                    if (AppManager.getAppManager().getActivityByName("AddMoodActivity") != null) {
                        AppManager.getAppManager().getActivityByName("AddMoodActivity").refresh(msg.what, msg.obj, msg.arg1);
                    }
                    break;
                case TaskType.TS_EXAM_PRAISE_MOOD: // ��һ������
                    AppManager.getAppManager().getActivityByName("TabMoodActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_BELITTLE_MOOD: // ��һ������
                    AppManager.getAppManager().getActivityByName("TabMoodActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEARCH_USER_MOOD: // ��ѯ�û�����
                    AppManager.getAppManager().getActivityByName("TabUserActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEARCH_USER_MOOD_MORE: // ��ѯ�����û�����
                    AppManager.getAppManager().getActivityByName("TabUserActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEARCH_USER_FAVOURITE: // ��ѯ�û��ղص�����
                    AppManager.getAppManager().getActivityByName("TabUserActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEARCH_USER_FAVOURITE_MORE: // ��ѯ�����û��ղص�����
                    AppManager.getAppManager().getActivityByName("TabUserActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_LOGIN: // �û���¼
                    AppManager.getAppManager().getActivityByName("LoginDialog").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_USER_INFO:// ��ȡ�û���Ϣ
                    AppManager.getAppManager().getActivityByName("TabUserActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_UPDATEUSERINFO: // �����û���Ϣ
                    AppManager.getAppManager().getActivityByName("LoadingActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_CHECKUSER: // ����û�ע������
                    AppManager.getAppManager().getActivityByName("LoadingActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_REGUSER: // ע���û�
                    AppManager.getAppManager().getActivityByName("LoadingActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SENDFEEDBACK: // �����û�������Ϣ
                    if (AppManager.getAppManager().getActivityByName("FeedBack") != null) {
                        AppManager.getAppManager().getActivityByName("FeedBack").refresh(msg.what, msg.obj, msg.arg1);
                    }
                    break;
                case TaskType.TS_EXAM_SEARCH_POP_MOOD: // ��ѯ��������
                    AppManager.getAppManager().getActivityByName("TabFindActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEARCH_POP_MOOD_MORE: // ��ѯ������������
                    AppManager.getAppManager().getActivityByName("TabFindActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEARCH_POP_FAVOURITE: // ��ѯ�����ղ�
                    AppManager.getAppManager().getActivityByName("TabFindActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
                case TaskType.TS_EXAM_SEARCH_POP_FAVOURITE_MORE: // ��ѯ���������ղ�
                    AppManager.getAppManager().getActivityByName("TabFindActivity").refresh(msg.what, msg.obj, msg.arg1);
                    break;
            }
        };
    };
}
