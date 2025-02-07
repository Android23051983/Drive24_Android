package com.example.drive24;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.JsonReader;

import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yandex.authsdk.YandexAuthException;
import com.yandex.authsdk.YandexAuthLoginOptions;
import com.yandex.authsdk.YandexAuthOptions;
import com.yandex.authsdk.YandexAuthResult;
import com.yandex.authsdk.YandexAuthSdk;
import com.yandex.authsdk.YandexAuthToken;


import org.json.JSONException;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import java.util.Collections;

import java.util.List;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.MediaType;



public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private static final String CLIENT_ID = "ede5e27338c845e3bc120d785a00f511";
    private static final String CLIENT_SECRET = "d347b378d5f44a59b58e4c4f0620b7d7";
    YandexAuthSdk sdk;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    long userId = 0;

    TextView tvUserInfo;
    Button rent, exit, loginBtn, loginBtnLand;
    String role;
    UserProfile context;
    ClientCars clientCars;
    LandlordCars landlordCars;
    List<ClientCars> clientCarsList;
    List<LandlordCars> landlordCarsList;

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvUserInfo = findViewById(R.id.tv_user_info);
        loginBtn = findViewById(R.id.loginBtn);
        loginBtnLand = findViewById(R.id.loginBtnLand);
        rent = findViewById(R.id.userData);
        databaseHelper = new DatabaseHelper(this);
        sdk = YandexAuthSdk.create(new YandexAuthOptions(getApplicationContext()));
        ActivityResultLauncher<YandexAuthLoginOptions> launcher =
                registerForActivityResult(sdk.getContract(), result -> {
                    try {
                        handleResult(result);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        loginBtn.setOnClickListener(v -> {
            YandexAuthLoginOptions loginOptions = new YandexAuthLoginOptions();
            launcher.launch(loginOptions);
            role = "Клиент";
        });
        loginBtnLand.setOnClickListener(v -> {
            YandexAuthLoginOptions loginOptions = new YandexAuthLoginOptions();
            launcher.launch(loginOptions);
            role = "Владелец";
        });

        rent.setOnClickListener(v -> {
            Intent intent = new Intent(this, RentActivity.class);
            intent.putExtra("role", role);
            startActivity(intent);
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        exit = findViewById(R.id.exit);
        if (TextUtils.isEmpty(tvUserInfo.getText())) {
            rent.setEnabled(false);
            exit.setEnabled(false);
        }

        exit.setOnClickListener(v -> {
            databaseHelper = new DatabaseHelper(this);
            Runnable runnable1 = new Runnable() {
                @Override
                public void run() {
                    databaseHelper.deleteUser();
                }
            };
            Thread thread1 = new Thread(runnable1);
            thread1.start();

            Runnable runnable2 = new Runnable() {
                @Override
                public void run() {
                    databaseHelper.deleteClientCars();
                }
            };
            Thread thread2 = new Thread(runnable2);
            thread2.start();

            Runnable runnable3 = new Runnable() {
                @Override
                public void run() {
                    databaseHelper.deleteLandlordCars();
                }
            };
            Thread thread3 = new Thread(runnable3);
            thread3.start();
            databaseHelper.close();
            finish();
        });
    }


    private void handleResult(YandexAuthResult result) throws ExecutionException, InterruptedException, JSONException, IOException {
        if (result instanceof YandexAuthResult.Success) {
            onSuccessAuth(((YandexAuthResult.Success) result).getToken());
        } else if (result instanceof YandexAuthResult.Failure) {
            onProcessError(((YandexAuthResult.Failure) result).getException());
        } else {
            onCancel();
        }
    }

    private void onCancel() {
    }

    private void onProcessError(YandexAuthException exception) {
    }

    private void onSuccessAuth(YandexAuthToken token) throws ExecutionException, InterruptedException, JSONException, IOException {
        String tokenValue = token.getValue();
        Gson gson = new Gson();
        // Создаем FutureTask для ожидания результата от getContent()
        FutureTask<UserProfile> futureTask = new FutureTask<>(new Callable<UserProfile>() {
            @Override
            public UserProfile call() throws Exception {
                return getContent("https://login.yandex.ru/info?&oauth_token=" + tokenValue, role);
            }
        });

        new Thread(futureTask).start();
        context = futureTask.get();
        databaseHelper = new DatabaseHelper(getApplicationContext());
        databaseHelper.deleteUser();
        databaseHelper.addUser(context);
        UserProfile user = databaseHelper.getUser("1");
        String contextResult = role + " " + user.getFirst_name() + " " + user.getLast_name() + " авторизован";
        tvUserInfo.setText(contextResult);
        rent.setEnabled(true);
        exit.setEnabled(true);
        loginBtn.setEnabled(false);
        loginBtnLand.setEnabled(false);
        if(Objects.equals(role, "Клиент")) {
            FutureTask<List<ClientCars>> futureTaskClientCars = new FutureTask<>(new Callable<List<ClientCars>>() {
                @Override
                public List<ClientCars> call() throws Exception {
                    return getClient(context);
                }
            });

            new Thread(futureTaskClientCars).start();
            clientCarsList = futureTaskClientCars.get();
            int countCar = 0;
            if (clientCarsList != null && !clientCarsList.isEmpty()) {
                databaseHelper = new DatabaseHelper(this);
                Log.d("MainActivity", "Лист с сервера получен");
                databaseHelper.deleteClientCars();
                for (ClientCars car : clientCarsList) {
                    FutureTask<Long> saveCar = new FutureTask<>(new Callable<Long>() {
                        @Override
                        public Long call() throws Exception {
                            return databaseHelper.addClientCars(car);
                        }
                    });
                    new Thread(saveCar).start();
                    Long saveId = saveCar.get();
                    if (saveId != null) {
                        countCar++;
                    }
                }
            } else {
                Log.d("MainActivity", "Лист с сервера не получен");
            }
            Log.d("countCar", String.valueOf(countCar));
        } else if (Objects.equals(role, "Владелец")) {
            FutureTask<List<LandlordCars>> futureTaskLandlordCars = new FutureTask<>(new Callable<List<LandlordCars>>() {
                @Override
                public List<LandlordCars> call() throws Exception {
                    return getLandlord(context);
                }
            });

            new Thread(futureTaskLandlordCars).start();
            landlordCarsList = futureTaskLandlordCars.get();
            int countCar = 0;
            if (landlordCarsList != null && !landlordCarsList.isEmpty()) {
                databaseHelper = new DatabaseHelper(this);
                Log.d("MainActivity", "Лист с сервера получен");
                databaseHelper.deleteLandlordCars();
                for (LandlordCars car : landlordCarsList) {
                    FutureTask<Long> saveCar = new FutureTask<>(new Callable<Long>() {
                        @Override
                        public Long call() throws Exception {
                            return databaseHelper.addLandlordCar(car);
                        }
                    });
                    new Thread(saveCar).start();
                    Long saveId = saveCar.get();
                    if (saveId != null) {
                        countCar++;
                    }
                }
            } else {
                Log.d("MainActivity", "Лист с сервера не получен");
            }
            Log.d("countCar", String.valueOf(countCar));
        }

    }

    private UserProfile getContent(String path, String role) throws IOException {
        BufferedReader reader = null;
        InputStream stream = null;
        InputStreamReader responseBodyReader = null;
        HttpsURLConnection connection = null;
        StringBuilder first_name = new StringBuilder();
        StringBuilder last_name = new StringBuilder();
        StringBuilder phone = new StringBuilder();
        StringBuilder email = new StringBuilder();

        try {
            URL url = new URL(path);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.connect();
            stream = connection.getInputStream();
            responseBodyReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            JsonReader jsonReader = new JsonReader(responseBodyReader);
            jsonReader.beginObject(); // Начало обработки объекта JSON
            while (jsonReader.hasNext()) { // Перебор всех ключей
                String key = jsonReader.nextName(); // Обращение к конкретному ключу
                switch (key) {
                    case "first_name":
                        first_name.append(jsonReader.nextString());
                        break;
                    case "last_name":
                        last_name.append(jsonReader.nextString());
                        break;
                    case "default_email":
                        email.append(jsonReader.nextString());
                        break;
                    case "default_phone":
                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            String subKey = jsonReader.nextName(); // чткние внутреннего ключа, при условии что объект внутри объекта
                            if (subKey.equals("number")) {
                                phone.append(jsonReader.nextString());
                            } else {
                                jsonReader.skipValue(); // Пропуск всех остальных значений
                            }
                        }
                        break;
                    default:
                        jsonReader.skipValue(); // Пропуск всех остальных значений
                        break;
                }
            }
            return (new UserProfile(first_name.toString(), last_name.toString(), phone.toString(), email.toString(), role));
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private List<ClientCars> getClient(UserProfile user) throws IOException, JSONException {
        BufferedReader reader = null;
        InputStream stream = null;
        InputStreamReader responseBodyReader = null;
        HttpsURLConnection connection = null;
        List<ClientCars> carsList = Collections.emptyList();
        int responseCode;
        Gson gson = new Gson();
        String jsonData = gson.toJson(user);
        byte[] postData = jsonData.getBytes(StandardCharsets.UTF_8); // Преобразуем JSON-объект в byte
        Log.d("MainActivity", jsonData);
        try {
            URL url = new URL("https://www.websitedevel.ru/server/user/read_client_data.php");
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(10000); // Устанавливаем таймаут подключения
            connection.setReadTimeout(15000); // Устанавливаем таймаут чтения ответа
            // Указываем заголовки
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("Accept", "application/json; charset=utf-8");
            // Отправка JSON-данных
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.write(postData); // отправляем поток byte данных
            outputStream.flush();
            outputStream.close();
            responseCode = connection.getResponseCode();
            Log.d("MainActivity", "Ответ от сервера: " + String.valueOf(responseCode));

            if (responseCode == HttpURLConnection.HTTP_OK) {
                stream = connection.getInputStream();
                responseBodyReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                reader = new BufferedReader(responseBodyReader);
                StringBuilder response = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null){
                    response.append(line);
                }
                Log.d("MainActivity", response.toString());

                Type listType = new TypeToken<List<ClientCars>>() {}.getType();
                carsList = gson.fromJson(response.toString(), listType);

                return carsList;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }


    private List<LandlordCars> getLandlord(UserProfile user)  throws IOException, JSONException {
        BufferedReader reader = null;
        InputStream stream = null;
        InputStreamReader responseBodyReader = null;
        HttpsURLConnection connection = null;
        List<LandlordCars> carsList = Collections.emptyList();
        int responseCode;
        Gson gson = new Gson();
        String jsonData = gson.toJson(user);
        byte[] postData = jsonData.getBytes(StandardCharsets.UTF_8); // Преобразуем JSON-объект в byte
        Log.d("MainActivity", jsonData);
        try {
            URL url = new URL("https://www.websitedevel.ru/server/user/read_landlord_data.php");
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(10000); // Устанавливаем таймаут подключения
            connection.setReadTimeout(15000); // Устанавливаем таймаут чтения ответа
            // Указываем заголовки
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("Accept", "application/json; charset=utf-8");
            // Отправка JSON-данных
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.write(postData); // отправляем поток byte данных
            outputStream.flush();
            outputStream.close();
            responseCode = connection.getResponseCode();
            Log.d("MainActivity", "Ответ от сервера: " + String.valueOf(responseCode));
            if (responseCode == HttpURLConnection.HTTP_OK) {
                stream = connection.getInputStream();
                responseBodyReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                reader = new BufferedReader(responseBodyReader);
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                Log.d("MainActivity", response.toString());

                Type listType = new TypeToken<List<LandlordCars>>() {
                }.getType();
                carsList = gson.fromJson(response.toString(), listType);
                return carsList;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}