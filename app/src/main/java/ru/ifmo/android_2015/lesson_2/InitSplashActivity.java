package ru.ifmo.android_2015.lesson_2;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

/**
 * Экран, выполняющий инициализацию при первом запуске приложения. В процессе инициализации
 * скачивается файл с данными, нужными для работы приложения. Пока идет инициализация, показывается
 * сплэш-скрин с индикатором прогресса.
 */
public class InitSplashActivity extends Activity {

    // Урл для скачивания файла с данными, нужными для инициализации приложения при первом запуске.
    // GZIP-архив, содержащий список городов в формате JSON.
    private static final String CITIES_GZ_URL =
            "https://www.dropbox.com/s/d99ky6aac6upc73/city_array.json.gz?dl=1";

    // Индикатор прогресса
    private ProgressBar progressBarView;
    // Заголовок
    private TextView titleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_splash);

        titleTextView = (TextView) findViewById(R.id.title_text);
        progressBarView = (ProgressBar) findViewById(R.id.progress_bar);

        titleTextView.setText(R.string.downloading);
        progressBarView.setProgress(0);
        progressBarView.setMax(100);
        progressBarView.setVisibility(View.VISIBLE);

        new DownloadFileTask().execute();
    }

    /**
     * Таск, выполняющий скачивание файла в фоновом потоке.
     */
    class DownloadFileTask extends AsyncTask<Void, Integer, Integer>
            implements ProgressCallback {

        /**
         * Скачивание файла в фоновом потоке. Возвращает результат:
         *      0 -- если файл успешно скачался
         *      1 -- если произошла ошибка
         */
        @Override
        protected Integer doInBackground(Void... ignore) {
            try {
                downloadFile(this /*progressCallback*/);
                return 0;

            } catch (Exception e) {
                Log.e(TAG, "Error downloading file: " + e, e);
                return 1;
            }
        }

        // Метод ProgressCallback, вызывается в фоновом потоке из downloadFile
        @Override
        public void onProgressChanged(int progress) {
            publishProgress(progress);
        }

        // Метод AsyncTask, вызывается в UI потоке в результате вызова publishProgress
        @Override
        protected void onProgressUpdate(Integer... values) {
            if (values.length > 0) {
                int progress = values[values.length - 1];
                progressBarView.setProgress(progress);
            }
        }

        @Override
        protected void onPostExecute(Integer resultCode) {
            // Проверяем код, который вернул doInBackground и показываем текст в зависимости
            // от результата
            if (resultCode == 0) {
                titleTextView.setText(R.string.done);
                progressBarView.setProgress(100);
            } else {
                titleTextView.setText(R.string.error);
            }
        }
    }

    /**
     * Скачивает список городов во временный файл.
     */
    void downloadFile(ProgressCallback progressCallback) throws IOException {
        File destFile = FileUtils.createTempExternalFile(this, "gz");
        DownloadUtils.downloadFile(CITIES_GZ_URL, destFile, progressCallback);
    }

    private static final String TAG = "InitSplash";
}
