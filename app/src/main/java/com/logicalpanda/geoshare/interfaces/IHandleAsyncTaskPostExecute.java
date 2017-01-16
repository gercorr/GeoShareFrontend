package com.logicalpanda.geoshare.interfaces;

import com.logicalpanda.geoshare.enums.AsyncTaskType;

/**
 * Created by Ger on 14/01/2017.
 */

public interface IHandleAsyncTaskPostExecute {
    void onAsyncTaskPostExecute(AsyncTaskType taskType);
}
