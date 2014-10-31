package com.ideacode.news.db;

/**
 * <p>
 * FileName: DataBaseContext.java
 * </p>
 * <p>
 * Description: ����ģʽ��ȷ������ϵͳ����ֻ��һ��DataHelperʵ��
 * <p>
 * Copyright: IdeaCode(c) 2012
 * </p>
 * <p>
 * @author Vic Su
 * </p>
 * <p>
 * @content andyliu900@gmail.com
 * </p>
 * <p>
 * @version 1.0
 * </p>
 * <p>
 * CreatDate: 2012-9-25 ����12:00:17
 * </p>
 * <p>
 * Modification History
 * </p>
 */
import android.content.Context;

public class DataBaseContext {

    private static DataHelper dataHelper;

    private static Object INSTANCE_LOCK = new Object();

    public static DataHelper getInstance(Context context) {
        synchronized (INSTANCE_LOCK) {
            if (dataHelper == null) {
                dataHelper = new DataHelper(context);
            }
            return dataHelper;
        }
    }
}
