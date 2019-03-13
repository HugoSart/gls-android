package com.hugovs.gls.util.asynctask;

import android.os.AsyncTask;

/**
 * An {@link ObservableAsyncTask} is an {@link AsyncTask} that implements the observer pattern.
 */
public abstract class ObservableAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    protected AsyncTaskListener<Params, Progress, Result> listener;

    public void setListener(AsyncTaskListener<Params, Progress, Result> listener) {
        this.listener = listener;
    }

    @Override
    public void onPreExecute() {
        listener.onPreExecute();
    }

    @Override
    public void onPostExecute(Result o) {
        listener.onPostExecute(o);
    }

    @Override
    public void onProgressUpdate(Progress[] values) {
        listener.onProgressUpdate(values);
    }

    @Override
    public void onCancelled(Result o) {
        listener.onCancelled(o);
    }

    @Override
    public void onCancelled() {
        listener.onCancelled();
    }

}
