package com.brokoli.mybeaconadmin.persistence;

import com.brokoli.mybeaconadmin.data.Gate;
import com.brokoli.mybeaconadmin.data.Beacon;
import com.brokoli.mybeaconadmin.data.Logger;
import com.brokoli.mybeaconadmin.data.MyObject;
import com.brokoli.mybeaconadmin.data.User;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    //RUN Make-project BEFORE running this class!!

    public static void main(String[] args) throws Exception {
        final Class<?>[] classes = new Class[]{
                Beacon.class, Gate.class, MyObject.class, User.class, Logger.class
        };

        writeConfigFile("ormlite_config.txt", classes);
    }
}
