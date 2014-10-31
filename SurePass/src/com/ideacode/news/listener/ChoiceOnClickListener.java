package com.ideacode.news.listener;

import android.content.DialogInterface;

/**
 * <p>FileName: ChoiceOnClickListener.java</p>
 * <p>Description: ��ѡ��ʾ��ť���������<p>
 * Copyright: IdeaCode(c) 2012
 * </p>
 * <p>@author Vic Su</p>
 * <p>@content andyliu900@gmail.com</p>
 * <p>@version 1.0</p>
 * <p>CreatDate: 2012-12-26 ����12:12:02</p>
 * <p>
 * Modification History
 */
public class ChoiceOnClickListener implements DialogInterface.OnClickListener {

    private int which = 0;

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        this.which = which;
    }

    public int getWhich() {
        return which;
    }
}
