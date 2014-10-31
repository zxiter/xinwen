package com.ideacode.news.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ideacode.news.R;
import com.ideacode.news.app.AppContext;
import com.ideacode.news.app.AppException;
import com.ideacode.news.bean.Update;
import com.ideacode.news.net.Tools;

public class UpdateManager {

    private static final int DOWN_NOSDCARD = 0;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;

    private static final int DIALOG_TYPE_LATEST = 0;
    private static final int DIALOG_TYPE_FAIL = 1;

    private static UpdateManager updateManager;

    private Context mContext;
    // ֪ͨ�Ի���
    private Dialog noticeDialog;
    // ���ضԻ���
    private Dialog downloadDialog;
    // '�Ѿ�������' ���� '�޷���ȡ���°汾' �ĶԻ���
    private Dialog latestOrFailDialog;
    // ������
    private ProgressBar mProgress;
    // ��ʾ������ֵ
    private TextView mProgressText;
    // ��ѯ����
    private ProgressDialog mProDialog;
    // ����ֵ
    private int progress;
    // �����߳�
    private Thread downLoadThread;
    // ��ֹ���
    private boolean interceptFlag;
    // ��ʾ��
    private String updateMsg = "";
    // ���صİ�װ��url
    private String apkUrl = "";
    // ���ذ�����·��
    private String savePath = "";
    // apk��������·��
    private String apkFilePath = "";
    // ��ʱ�����ļ�·��
    private String tmpFilePath = "";
    // �����ļ���С
    private String apkFileSize;
    // �������ļ���С
    private String tmpFileSize;

    private String curVersionName = "";
    private int curVersionCode;
    private Update mUpdate;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    mProgressText.setText(tmpFileSize + "/" + apkFileSize);
                    break;
                case DOWN_OVER:
                    downloadDialog.dismiss();
                    installApk();
                    break;
                case DOWN_NOSDCARD:
                    downloadDialog.dismiss();
                    Toast.makeText(mContext, "�޷����ذ�װ�ļ�������SD���Ƿ����", 3000).show();
                    break;
            }
        };
    };

    public static UpdateManager getUpdateManager() {
        if (updateManager == null) {
            updateManager = new UpdateManager();
        }
        updateManager.interceptFlag = false;
        return updateManager;
    }

    /**
     * ���App����
     * 
     * @param context
     * @param isShowMsg
     *            �Ƿ���ʾ��ʾ��Ϣ
     */
    public void checkAppUpdate(Context context, final boolean isShowMsg) {
        this.mContext = context;
        getCurrentVersion();
        if (isShowMsg) {
            if (mProDialog == null)
                mProDialog = ProgressDialog.show(mContext, null, "���ڼ�⣬���Ժ�...", true, true);
            else if (mProDialog.isShowing() || (latestOrFailDialog != null && latestOrFailDialog.isShowing()))
                return;
        }
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                // �������Ի�����ʾ - �����Ҳ����ʾ
                if (mProDialog != null && !mProDialog.isShowing()) {
                    return;
                }
                // �رղ��ͷ��ͷŽ������Ի���
                if (isShowMsg && mProDialog != null) {
                    mProDialog.dismiss();
                    mProDialog = null;
                }
                // ��ʾ�����
                if (msg.what == 1) {
                    mUpdate = (Update) msg.obj;
                    if (mUpdate != null) {
                        if (curVersionCode < mUpdate.getVersionCode()) {
                            apkUrl = mUpdate.getDownloadUrl();
                            updateMsg = mUpdate.getUpdateLog();
                            showNoticeDialog();
                        } else if (isShowMsg) {
                            showLatestOrFailDialog(DIALOG_TYPE_LATEST);
                        }
                    }
                } else if (isShowMsg) {
                    showLatestOrFailDialog(DIALOG_TYPE_FAIL);
                }
            }
        };
        new Thread() {
            public void run() {
                Message msg = new Message();
                try {
                    Update update = Tools.checkVersion((AppContext) mContext.getApplicationContext());
                    msg.what = 1;
                    msg.obj = update;
                } catch (AppException e) {
                    e.printStackTrace();
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * ��ʾ'�Ѿ�������'����'�޷���ȡ�汾��Ϣ'�Ի���
     */
    private void showLatestOrFailDialog(int dialogType) {
        if (latestOrFailDialog != null) {
            // �رղ��ͷ�֮ǰ�ĶԻ���
            latestOrFailDialog.dismiss();
            latestOrFailDialog = null;
        }
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle("ϵͳ��ʾ");
        if (dialogType == DIALOG_TYPE_LATEST) {
            builder.setMessage("����ǰ�Ѿ������°汾");
        } else if (dialogType == DIALOG_TYPE_FAIL) {
            builder.setMessage("�޷���ȡ�汾������Ϣ");
        }
        builder.setPositiveButton("ȷ��", null);
        latestOrFailDialog = builder.create();
        latestOrFailDialog.show();
    }

    /**
     * ��ȡ��ǰ�ͻ��˰汾��Ϣ
     */
    private void getCurrentVersion() {
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            curVersionName = info.versionName;
            curVersionCode = info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * ��ʾ�汾����֪ͨ�Ի���
     */
    private void showNoticeDialog() {
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle("����汾����");
        builder.setMessage(updateMsg);
        builder.setPositiveButton("��������", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog();
            }
        });
        builder.setNegativeButton("�Ժ���˵", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        noticeDialog = builder.create();
        noticeDialog.show();
    }

    /**
     * ��ʾ���ضԻ���
     */
    private void showDownloadDialog() {
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle("���������°汾");

        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.update_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        mProgressText = (TextView) v.findViewById(R.id.update_progress_text);

        builder.setView(v);
        builder.setNegativeButton("ȡ��", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interceptFlag = true;
            }
        });
        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                interceptFlag = true;
            }
        });
        downloadDialog = builder.create();
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.show();

        downloadApk();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                String apkName = "SurePassNewsApp_" + mUpdate.getVersionName() + ".apk";
                String tmpApk = "SurePassNewsApp_" + mUpdate.getVersionName() + ".tmp";
                // �ж��Ƿ������SD��
                String storageState = Environment.getExternalStorageState();
                if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                    savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SurePassDir/Update/";
                    File file = new File(savePath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    apkFilePath = savePath + apkName;
                    tmpFilePath = savePath + tmpApk;
                }

                // û�й���SD�����޷������ļ�
                if (apkFilePath == null || apkFilePath == "") {
                    mHandler.sendEmptyMessage(DOWN_NOSDCARD);
                    return;
                }

                File ApkFile = new File(apkFilePath);

                // �Ƿ������ظ����ļ�
                if (ApkFile.exists()) {
                    downloadDialog.dismiss();
                    installApk();
                    return;
                }

                // �����ʱ�����ļ�
                File tmpFile = new File(tmpFilePath);
                FileOutputStream fos = new FileOutputStream(tmpFile);

                URL url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                // ��ʾ�ļ���С��ʽ��2��С������ʾ
                DecimalFormat df = new DecimalFormat("0.00");
                // ������������ʾ�����ļ���С
                apkFileSize = df.format((float) length / 1024 / 1024) + "MB";

                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
                    // ������������ʾ�ĵ�ǰ�����ļ���С
                    tmpFileSize = df.format((float) count / 1024 / 1024) + "MB";
                    // ��ǰ����ֵ
                    progress = (int) (((float) count / length) * 100);
                    // ���½���
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // ������� - ����ʱ�����ļ�ת��APK�ļ�
                        if (tmpFile.renameTo(ApkFile)) {
                            // ֪ͨ��װ
                            mHandler.sendEmptyMessage(DOWN_OVER);
                        }
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);// ���ȡ����ֹͣ����

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    /**
     * ����apk
     * 
     * @param url
     */
    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * ��װapk
     * 
     * @param url
     */
    private void installApk() {
        File apkfile = new File(apkFilePath);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }
}
