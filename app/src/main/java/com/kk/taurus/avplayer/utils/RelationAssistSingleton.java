package com.kk.taurus.avplayer.utils;

import android.content.Context;

import com.kk.taurus.playerbase.assist.RelationAssist;

/**
 * Created by dumingwei on 2019-05-22.
 * Desc:RelationAssist的单例类
 */
public class RelationAssistSingleton {

    private static RelationAssist mAssist;

    private RelationAssistSingleton() {
    }

    public static RelationAssist getAssist(Context context) {
        if (mAssist == null) {
            synchronized (RelationAssistSingleton.class) {
                if (mAssist == null) {
                    mAssist = new RelationAssist(context);
                }
            }
        }
        return mAssist;
    }

    public static void releaseAssist() {
        if (mAssist != null) {
            mAssist.destroy();
            mAssist = null;
        }
    }
}
