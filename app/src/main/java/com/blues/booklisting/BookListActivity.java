package com.blues.booklisting;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity {

    /** Tag for the log messages */
    public static final String LOG_TAG = BookListActivity.class.getSimpleName();

    //test for request
    private final String REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?maxResults=10&q=";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String searchText = extras.getString(MainActivity.urlKey);
            BookListAsyncTask task = new BookListAsyncTask();

            task.execute(REQUEST_URL + searchText);
        }
    }


    private class BookListAsyncTask extends AsyncTask<String, Void, List<Book>> {

        @Override
        protected List<Book> doInBackground(String... urlStr) {
            // Create URL object
            URL url = createUrl(urlStr[0]);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem making the HTTP request.", e);
            }

            return extractFeatureFromJson(jsonResponse);
        }

        @Override
        protected void onPostExecute(List<Book> books) {
            if (books == null) {
                Intent nextActivity = new Intent(BookListActivity.this, EmptyDataActivity.class);
                startActivity(nextActivity);
                return;
            }

            BookAdapter bookAdapter = new BookAdapter(BookListActivity.this,books);
            ListView bookListView = (ListView) findViewById(R.id.activity_book_list);

            bookListView.setAdapter(bookAdapter);

        }

        private URL createUrl(String stringUrl) {
            URL url;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e){
                    Log.e(LOG_TAG, "Problem closing the inputStream.", e);
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private List<Book> extractFeatureFromJson(String bookJSON) {

            if (TextUtils.isEmpty(bookJSON)) {
                return null;
            }


            List<Book> books = new ArrayList<>();

            try {
                JSONObject baseJsonResponse = new JSONObject(bookJSON);

                if (!baseJsonResponse.has("items")){
                    return null;
                }

                JSONArray bookArray = baseJsonResponse.getJSONArray("items");
                for (int i = 0; i < bookArray.length(); i++) {
                    JSONObject currentBook = bookArray.getJSONObject(i);
                    JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                    String title = volumeInfo.getString("title");

                    String authors = "no authors";
                    if (volumeInfo.has("authors")) {
                        JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                        authors = authorsArray.getString(0);
                    }

                    String date = "no date";
                    if (volumeInfo.has("publishedDate")){
                        date = volumeInfo.getString("publishedDate");
                    }

                    books.add(new Book(title,authors,date));
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
            }

            return books;
        }

    }
}
