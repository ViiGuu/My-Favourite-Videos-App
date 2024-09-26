package com.example.a9;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a9.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DeleteDialog.DeleteDialogListener, AddDialog.AddDialogListener, ShareDialog.ShareDialogListener {
    private ConnectivityManager connectivityManager;
    private ActivityMainBinding binding;
    private Button addButton;
    private Button shareButton;
    private Button deleteButton;
    private ImageButton playButton;
    private ListView savedVideosListView;
    private TextView selectedVideoTextView;
    private Video selectedVideo;
    private List<Video> videosList = new ArrayList<>();
    private ArrayAdapter<Video> adapter;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUpConnectivityManager();

        initializeFields();

        savedVideosListView.setAdapter(adapter);

        setClickListeners();

    }

    //initierar alla instansvariabler
    private void initializeFields(){
        addButton = findViewById(R.id.buttonAdd);
        shareButton = findViewById(R.id.buttonShare);
        deleteButton = findViewById(R.id.buttonDelete);
        playButton = findViewById(R.id.buttonPlay);

        savedVideosListView = findViewById(R.id.listView);
        selectedVideoTextView = findViewById(R.id.selectedVideo);

        vibrator = getSystemService(Vibrator.class);

        videosList = new ArrayList<>();
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                videosList);
    }

    //diverse klickhanterare för knappar och listview
    private void setClickListeners(){
        addButton.setOnClickListener(view -> {
            AddDialog addDialog = new AddDialog();
            addDialog.show(getSupportFragmentManager(), "addDialog");
        });

        shareButton.setOnClickListener(view -> {
            if(selectedVideo != null){
                ShareDialog shareDialog = new ShareDialog();
                shareDialog.show(getSupportFragmentManager(), "shareDialog");
            } else
                Toast.makeText(this, "No video selected", Toast.LENGTH_SHORT).show();
        });

        deleteButton.setOnClickListener(view -> {
            if(selectedVideo != null) {
                DeleteDialog deleteDialog = new DeleteDialog();
                deleteDialog.show(getSupportFragmentManager(), "deleteDialog");
            } else
                Toast.makeText(this, "No video selected", Toast.LENGTH_SHORT).show();
        });

        playButton.setOnClickListener(view -> {
            if(selectedVideo != null)
                playVideo(selectedVideo.url);
            else
                Toast.makeText(this, "No video selected", Toast.LENGTH_SHORT).show();
        });

        savedVideosListView.setOnItemClickListener((parent, view, position, id) -> {
            String url = videosList.get(position).getUrl();
            assert url != null;

            selectedVideo = videosList.get(position);
            selectedVideoTextView.setText(selectedVideo.name);
        });
    }

    //sätter upp en ConnectivityManager och sätter upp funktionalitet för att registrera nätverket via
    //en NetworkRequest, så att användaren meddelas när nätverket är uppe eller nere.
    private void setUpConnectivityManager(){
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                showNetworkInfo(true);
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                showNetworkInfo(false);
            }
        };
        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        connectivityManager.registerNetworkCallback(request, networkCallback);
    }

    //återanvänd metod från 4.1.1, som helt enkelt spelar upp
    //en youtube-video via en intent
    private void playVideo(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            System.out.println("No way to play video");
        }
    }

    //återanvänd hjälpmetod från 7.3.1, som berättar om nätverket är uppe eller nere.
    public void showNetworkInfo(Boolean networkIsUp) {
        if(networkIsUp)
            Toast.makeText(this, "Network is up", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Network is down", Toast.LENGTH_SHORT).show();
    }

    //metod för lyssnaren i DeleteDialog, tar bort en vald video och gör en vibration
    @Override
    public void onDialogPositiveClick() {
        if (selectedVideo != null && videosList.contains(selectedVideo)) {
            videosList.remove(selectedVideo);
            adapter.notifyDataSetChanged();
            selectedVideo = null;
            selectedVideoTextView.setText("");
            if(vibrator != null && vibrator.hasVibrator())
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));
        }
    }

    //lyssnaren för AddDialog, lägger till en ny video.
    @Override
    public void onDialogPositiveClick(String name, String url) {
        Video video = new Video(name, url);
        videosList.add(video);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, video + " added as a favourite video", Toast.LENGTH_SHORT).show();
    }

    //lyssnaren för ShareDialog, delar genom intent en video via epost med förinställt meddelande.
    @Override
    public void onDialogPositiveClick(String email){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Look at " + selectedVideo.name + "!");
        intent.putExtra(Intent.EXTRA_TEXT, "I just shared a video with you, check it out: " + selectedVideo.url);
        intent.setType("message/rfc822");
        try {
            startActivity(Intent.createChooser(intent, "Choose an email client:"));
        } catch (ActivityNotFoundException e) {
            System.out.println("No app to send email. Error: " + e);
        }
    }

    //simpel inre klass för att hantera video-objekt.
    private class Video {
        String name;
        String url;

        public Video(String name, String url) {
            this.name = name;
            this.url = url;
        }

        @NonNull
        @Override
        public String toString() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }
}