package com.hugovs.gls.util.asynctask;

public interface AsyncTaskListener<Params, Progress, Result> {
    void onPreExecute();
    void onPostExecute(Result o);
    void onProgressUpdate(Progress[] values);
    void onCancelled(Result o);
    void onCancelled();
}
