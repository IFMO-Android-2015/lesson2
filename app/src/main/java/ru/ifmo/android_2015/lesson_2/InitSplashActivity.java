package ru.ifmo.android_2015.lesson_2;

import android.app.Activity;
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

        try {
            // ВНИМАНИЕ: это очень плохая идея -- выполнять сетевые запросы в основном потоке.
            // Обычно Android просто не дает это сделать -- бросает NetworkOnMainThreadException.
            // Чтобы продемонстировать, как тормозит UI, мы можем выключить проверку потока, которую
            // делает система при выполнении сетевых запросов. Для этого раскомментируйте эту строчку:
            //StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

            downloadFile();
            titleTextView.setText(R.string.done);

        } catch (Exception e) {
            Log.e(TAG, "Error downloading file: " + e, e);
            titleTextView.setText(R.string.error);
        }

        progressBarView.setVisibility(View.INVISIBLE);
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
