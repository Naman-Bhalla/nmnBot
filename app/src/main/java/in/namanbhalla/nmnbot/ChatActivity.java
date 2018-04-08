package in.namanbhalla.nmnbot;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import java.util.LinkedList;

public class ChatActivity extends AppCompatActivity implements AIListener {
    private AIService mAIService;
    private Message sent_message;
    private Message received_message;
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private LinkedList<Message> message_list;
    private TextToSpeech mTextToSpeech; //NEW
    private FloatingActionButton mFloatingActionButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        message_list = new LinkedList<>();
        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, message_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mMessageRecycler.setLayoutManager(linearLayoutManager);
        mMessageRecycler.setAdapter(mMessageAdapter);
        mTextToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

            }
        });
        mTextToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {
                mFloatingActionButton.setEnabled(true);
            }

            @Override
            public void onError(String utteranceId) {

            }
        });
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        final AIConfiguration config =
                new AIConfiguration(BuildConfig.api_key,
                        AIConfiguration.SupportedLanguages.English,
                        AIConfiguration.RecognitionEngine.System);

        mAIService = AIService.getService(this, config);
        mAIService.setListener(this);
    }

    public void listenButtonOnClick(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1);
        }   else {
            Toast.makeText(this, "Listening...", Toast.LENGTH_SHORT).show();
            mFloatingActionButton.setEnabled(false);
            mAIService.startListening();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1 : {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Listening...", Toast.LENGTH_SHORT).show();
                    mFloatingActionButton.setEnabled(false);
                    mAIService.startListening();
                } else {
                    Toast.makeText(
                            this, "Sorry ! Can't work without permission.", Toast.LENGTH_SHORT
                    ).show();
                }
            }
        }

    }

    public void onResult(final AIResponse response) {
        Result result = response.getResult();

        sent_message = new Message(result.getResolvedQuery(), "SELF");
        received_message = new Message(result.getFulfillment().getSpeech(), "Naman Bot");

        mMessageAdapter.newMessage(sent_message);
        mMessageAdapter.newMessage(received_message);
        mMessageRecycler.scrollToPosition(message_list.size() - 1);

        mTextToSpeech.speak(received_message.getMessage(), TextToSpeech.QUEUE_FLUSH, new Bundle(), "DEFAULT");
    }

    @Override
    public void onError(final AIError error) {
    }

    @Override
    public void onListeningStarted() {}

    @Override
    public void onListeningCanceled() {}

    @Override
    public void onListeningFinished() {}

    @Override
    public void onAudioLevel(final float level) {}
}
