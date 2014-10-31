package com.ideacode.news.ui;

import java.io.File;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.ideacode.news.R;
import com.ideacode.news.app.AppContext;
import com.ideacode.news.app.AppManager;
import com.ideacode.news.common.util.FileUtils;
import com.ideacode.news.common.util.MethodsCompat;
import com.ideacode.news.common.util.UIHelper;
import com.ideacode.news.common.util.UpdateManager;

public class TabSettingActivity extends PreferenceActivity {

    TextView headTv;
    SharedPreferences mPreferences;
    Preference account;
    Preference cache;
    Preference feedback;
    Preference update;
    Preference about;
    CheckBoxPreference loadimage;


    AppContext ac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ���Activity����ջ
        AppManager.getAppManager().addActivity(this);

        // ������ʾPreferences
        addPreferencesFromResource(R.xml.preferences);

        // ���SharedPreferences
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        ListView localListView = getListView();
        localListView.setBackgroundColor(0);
        localListView.setCacheColorHint(0);
        ((ViewGroup) localListView.getParent()).removeView(localListView);
        ViewGroup localViewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.setting, null);
        ((ViewGroup) localViewGroup.findViewById(R.id.setting_content)).addView(localListView, -1,
                -1);
        setContentView(localViewGroup);

        headTv = (TextView) findViewById(R.id.systv);
        headTv.setText(R.string.setting_head_title);

        ac = (AppContext) getApplication();

        //��¼��ע��       
        account = findPreference("account");
        checkLogin();
        account.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UIHelper.loginOrLogout(TabSettingActivity.this);
                account.setTitle(R.string.main_menu_login);
                return true;
            }
        });

        countCashData();
        cache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UIHelper.clearAppCache(TabSettingActivity.this);
                cache.setSummary("0KB");
                return true;
            }
        });

        // ����ͼƬloadimage
        loadimage = (CheckBoxPreference) findPreference("loadimage");
        loadimage.setChecked(ac.isLoadImage());
        if (ac.isLoadImage()) {
            loadimage.setSummary("ҳ�����ͼƬ (Ĭ����WIFI�����¼���ͼƬ)");
        } else {
            loadimage.setSummary("ҳ�治����ͼƬ (Ĭ����WIFI�����¼���ͼƬ)");
        }
        loadimage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UIHelper.changeSettingIsLoadImage(TabSettingActivity.this, loadimage.isChecked());
                if (loadimage.isChecked()) {
                    loadimage.setSummary("ҳ�����ͼƬ (Ĭ����WIFI�����¼���ͼƬ)");
                } else {
                    loadimage.setSummary("ҳ�治����ͼƬ (Ĭ����WIFI�����¼���ͼƬ)");
                }
                return true;
            }
        });

        // �������
        feedback = findPreference("feedback");
        feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UIHelper.showFeedBack(TabSettingActivity.this);
                return true;
            }
        });

        // �汾����
        update = findPreference("update");
        update.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UpdateManager.getUpdateManager().checkAppUpdate(TabSettingActivity.this, true);
                return true;
            }
        });

        // ��������
        about = findPreference("about");
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                UIHelper.showAbout(TabSettingActivity.this);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        checkLogin();
        countCashData();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ����Activity&�Ӷ�ջ���Ƴ�
        AppManager.getAppManager().finishActivity(this);
    }

    // ����¼״̬
    private void checkLogin(){
        if(ac.isLogin()){
            account.setTitle(R.string.main_menu_logout);
        }else{
            account.setTitle(R.string.main_menu_login);
        }
    }

    // ���㻺���С
    private void countCashData() {
        long fileSize = 0;
        String cacheSize = "0KB";
        File filesDir = getFilesDir();
        File cacheDir = getCacheDir();

        fileSize += FileUtils.getDirSize(filesDir);
        fileSize += FileUtils.getDirSize(cacheDir);
        // 2.2�汾���н�Ӧ�û���ת�Ƶ�sd���Ĺ���
        if (AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            File externalCacheDir = MethodsCompat.getExternalCacheDir(this);
            fileSize += FileUtils.getDirSize(externalCacheDir);
        }
        if (fileSize > 0)
            cacheSize = FileUtils.formatFileSize(fileSize);

        // �������
        cache = findPreference("cache");
        cache.setSummary(cacheSize);
    }
}
