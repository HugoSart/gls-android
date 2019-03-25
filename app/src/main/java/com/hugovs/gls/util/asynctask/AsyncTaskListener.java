package com.hugovs.gls.util.asynctask;

/**
 * Interface that provide the observable methods of {@link ObservableAsyncTask}.
 *
 * @author Hugo Sartori
 */
public interface AsyncTaskListener<Params, Progress, Result> {
    void onPreExecute();
    void onPostExecute(Result o);
    void onProgressUpdate(Progress[] values);
    void onCancelled(Result o);
    void onCancelled();
}
