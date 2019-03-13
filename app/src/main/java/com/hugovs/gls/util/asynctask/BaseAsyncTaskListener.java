package com.hugovs.gls.util.asynctask;

/**
 * A simple {@link AsyncTaskListener} with dummy implementations.
 */
public class BaseAsyncTaskListener<Params, Progress, Result> implements AsyncTaskListener<Params, Progress, Result> {

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(Result o) {

    }

    @Override
    public void onProgressUpdate(Progress[] values) {

    }

    @Override
    public void onCancelled(Result o) {

    }

    @Override
    public void onCancelled() {

    }
}
