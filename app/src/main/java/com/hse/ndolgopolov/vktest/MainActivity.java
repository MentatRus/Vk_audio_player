package com.hse.ndolgopolov.vktest;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VkAudioArray;
import com.vk.sdk.util.VKUtil;

import java.io.IOException;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
        for (String fingerprint : fingerprints) {
            Log.i("fp", fingerprint);
        }

        //VKSdk.initialize(this);

        if(!VKSdk.isLoggedIn()){
            VKSdk.login(this, VKScope.AUDIO, VKScope.FRIENDS);

        }
        loadMusic();
//        VKRequest currentRequest = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "id,first_name,last_name,bdate"));
//        currentRequest.executeWithListener(new VKRequest.VKRequestListener() {
//            @Override
//            public void onComplete(VKResponse response) {
//                super.onComplete(response);
//                Log.i("VkDemoApp", response.json.toString());
//
//
//            }
//
//            @Override
//            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
//                super.attemptFailed(request, attemptNumber, totalAttempts);
//                Log.d("VkDemoApp", "attemptFailed " + request + " " + attemptNumber + " " + totalAttempts);
//            }
//
//            @Override
//            public void onError(VKError error) {
//                super.onError(error);
//                Log.d("VkDemoApp", "onError: " + error);
//            }
//
//            @Override
//            public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
//                super.onProgress(progressType, bytesLoaded, bytesTotal);
//                Log.d("VkDemoApp", "onProgress " + progressType + " " + bytesLoaded + " " + bytesTotal);
//            }
//        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                startTestActivity();
                // User passed Authorization
            }
            @Override
            public void onError(VKError error) {
                // User didn't pass Authorization
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startTestActivity() {

    }
    private void loadMusic(){
        VKRequest request = VKApi.audio().get();
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                Log.i("response", response.json.toString());
                final VkAudioArray audios = (VkAudioArray)response.parsedModel;
                super.onComplete(response);
                ListView list = (ListView)findViewById(R.id.audios);
                list.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return audios.getCount();
                    }

                    @Override
                    public Object getItem(int position) {
                        return audios.get(position);
                    }

                    @Override
                    public long getItemId(int position) {
                        return 0;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        LayoutInflater inflater =(LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View item = inflater.inflate(R.layout.audio_item, parent, false);
                        TextView artist = (TextView) item.findViewById(R.id.textView_artist);
                        TextView track = (TextView)item.findViewById(R.id.textView_track);
                        final VKApiAudio audio = audios.get(position);
                        artist.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MediaPlayer mediaPlayer = new MediaPlayer();
                                try {
                                    mediaPlayer.setDataSource(audio.url);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }

                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mp.start();
                                    }
                                });
                                mediaPlayer.prepareAsync();
                            }
                        });

                        artist.setText(audio.artist);
                        track.setText(audio.title);
                        return item;
                    }
                });
            }
        });
    }
}
