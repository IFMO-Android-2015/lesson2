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
        progressBarView.setVisibility(View.VISIBLE);

        new DownloadFileTask().execute();
    }

    /**
     * Таск, выполняющий скачивание файла в фоновом потоке.
     */
    class DownloadFileTask extends AsyncTask<Void, Void, Integer> {

        /**
         * Скачивание файла в фоновом потоке. Возвращает результат:
         *      0 -- если файл успешно скачался
         *      1 -- если произошла ошибка
         */
        @Override
        protected Integer doInBackground(Void... ignore) {
            try {
                downloadFile();
                return 0;

            } catch (Exception e) {
                Log.e(TAG, "Error downloading file: " + e, e);
                return 1;
            }
        }

        @Override
        protected void onPostExecute(Integer resultCode) {
            // Проверяем код, который вернул doInBackground и показываем текст в зависимости
            // от результата
            int textResId = resultCode == 0 ? R.string.done : R.string.error;
            titleTextView.setText(textResId);
            // Скрываем индикатор прогресса
            progressBarView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Скачивает список городов во временный файл.
     */
    void downloadFile() throws IOException {
        File destFile = FileUtils.createTempExternalFile(this, "gz");
        DownloadUtils.downloadFile(CITIES_GZ_URL, destFile);
    }

    private static final String TAG = "InitSplash";
}
