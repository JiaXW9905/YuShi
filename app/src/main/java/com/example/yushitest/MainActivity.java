package com.example.yushitest;//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;

import static java.security.AccessController.getContext;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import android.os.Bundle;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtm.IRtmEventHandler;
import io.agora.rtm.JoinChannelOptions;
import io.agora.rtm.JoinTopicOptions;
import io.agora.rtm.MessageEvent;
import io.agora.rtm.PresenceEvent;
import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmConfig;
import io.agora.rtm.RtmConstants;
import io.agora.rtm.StreamChannel;
import io.agora.rtm.TopicOptions;
import io.agora.rtm.UserList;


//import io.agora.rtm.StreamChannel.*


public class MainActivity extends AppCompatActivity {

//    static {
//        System.loadLibrary("agora-rtc-sdk");
//        System.loadLibrary("agora-fdkaac");
//        System.loadLibrary("agora-ffmpeg");
//        System.loadLibrary("agora-soundtouch");
//        System.loadLibrary("agora-core");
//    }


    // 填写项目的 App ID，可在声网控制台中生成
    private final String appId = "4d5de4c258de4ab18e169279c0b31425";
    private String rtmToken = "4d5de4c258de4ab18e169279c0b31425";
    private String rtmChannel = "rtmwjx";
    private String rtmUserId = "888";
    private String rtcChannel = "rtcwjx";
    private  int rtcUid = 999;

    private RtcEngine mRtcEngine;
    private RtmClient mRtmClient;
    private StreamChannel mStreamChannel;
    private String topicName = "topicTest";
    private Timer  timer = new Timer();
    private int cnt = 0;
    private boolean topicIn = false;
    private boolean streamChannelIn = false;
    private boolean sendMsg = false;

    protected Handler handler;




//    protected final void showLongToast(final String msg) {
//        runOnUIThread(() -> {
//            AccessControlContext context = getContext();
//            if (context == null) {
//                return;
//            }
//            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
//        });
//    }

//    protected final void runOnUIThread(Runnable runnable) {
//        this.runOnUIThread(runnable, 0);
//    }
//
//    protected final void runOnUIThread(Runnable runnable, long delay) {
//        if (handler != null && runnable != null && getContext() != null) {
//            if (delay <= 0 && handler.getLooper().getThread() == Thread.currentThread()) {
//                runnable.run();
//            } else {
//                handler.postDelayed(() -> {
//                    if (getContext() != null) {
//                        runnable.run();
//                    }
//                }, delay);
//            }
//        }
//    }

    private void showToast(String msg){
//        Toast toast = new Toast(getApplicationContext(),msg,Toast.LENGTH_SHORT);
//        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG);
//        Toast toast = new Toast(getApplicationContext());
//        toast.setText(msg);

        runOnUIThread(() -> {
            Context context = getApplicationContext();
            if (context == null) {
                return;
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        });
//        Toast.
    }

    protected final void runOnUIThread(Runnable runnable) {
        this.runOnUIThread(runnable, 0);
    }



    private void initializeAndJoinChannel() {
        try {
            // 创建 RtcEngineConfig 对象，并进行配置
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            // 创建并初始化 RtcEngine
            mRtcEngine = RtcEngine.create(config);
        } catch (Exception e) {
            throw new RuntimeException("Check the error.");
        }

        //初始化RTM
        setupRTM();
        // 启用视频模块
        mRtcEngine.enableVideo();
        // 开启本地预览
        mRtcEngine.startPreview();

        // 创建一个 SurfaceView 对象，并将其作为 FrameLayout 的子对象
        FrameLayout container = findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = new SurfaceView (getBaseContext());
        container.addView(surfaceView);
        // 将 SurfaceView 对象传入声网实时互动 SDK，设置本地视图
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));




//        joinRtmChannel();
//        setPrivatizingServerRTC();
//        setPrivatizingServerRTM();
//        joinRtcChannel();
        joinRtmChannel();






    }

    private void joinRtcChannel(){

        setPrivatizingServerRTC();
        // 创建 ChannelMediaOptions 对象，并进行配置
        ChannelMediaOptions rtcOptions = new ChannelMediaOptions();
        // 根据场景将用户角色设置为 BROADCASTER (主播) 或 AUDIENCE (观众)
        rtcOptions.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
        // 直播场景下，设置频道场景为 BROADCASTING (直播场景)
        rtcOptions.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
        rtcOptions.publishMicrophoneTrack = false;
        rtcOptions.publishCameraTrack = true;
        rtcOptions.autoSubscribeAudio = true;
        rtcOptions.autoSubscribeVideo = true;
        int res = mRtcEngine.joinChannel(null, rtcChannel, rtcUid, rtcOptions);
        Log.i("wjx", "joinchannel result : " + res);
    }


    private void joinRtmChannel(){

        setPrivatizingServerRTM();
        //加入RTM频道
        mStreamChannel = mRtmClient.createStreamChannel(rtmChannel);
//        setPrivatizingServerRTM();
        JoinChannelOptions rtmOptions = new JoinChannelOptions();
        rtmOptions.token = rtmToken;
        int err = mStreamChannel.join(rtmOptions);
        Log.e("wjx", "join stream channel result : " + err );
    }




    private void setPrivatizingServerRTM(){
        //RTM私有化配置
        mRtcEngine.setParameters("{\"rtc.vos_aut_use_old_sync\":false}");
        mRtcEngine.setParameters("{\"rtc.force_local\":true}");
        mRtcEngine.setParameters("{\"rtc.local_domain\":\"ap.1078949.agora.local\"}");
        mRtcEngine.setParameters("{\"rtc.local_ap_list\":[\"113.137.52.160\"]}");
        mRtcEngine.setParameters("{\"rtc.enable_nasa2\":true}");

    }


    private void setPrivatizingServerRTC(){
        //RTC私有化配置
        mRtcEngine.setParameters("{\"rtc.vos_list\":[\"113.137.52.150:4701\"]}");
    }

    private void execSend(){
        TimerTask task  = new TimerTask() {
            @Override
            public void run() {
                String msg = "test msg " + cnt;
                byte[] message = msg.getBytes();
                if (topicIn&&streamChannelIn&&sendMsg){
                    int errorCode = mStreamChannel.publishTopicMessage(topicName, message);
                    if(errorCode == 0){
                        cnt ++;
                        Log.i("wjx", "pbulish success,message: " + msg);
                    }
                }


            }
        };
        timer.schedule(task,2000,5000);
    }

    private void subscribeRTM(){
        ArrayList<String> mSubscribeUserList = new ArrayList<>();
        mSubscribeUserList.add("222");


        TopicOptions options = new TopicOptions();
        options.users = mSubscribeUserList;

// subscribe topic
        int errorCode = mStreamChannel.subscribeTopic(topicName, options);
        Log.e("wjx", "RTM-----subscribeRTM: topicName--- " + topicName + ",err: " + errorCode );


//// get subscribed user list
//        UserList userList = new UserList();
//        Editable topicName = this.topicName;
//         errorCode = mStreamChannel.getSubscribedUserList(topicName.toString(), userList);
    }

    private void joinTopic(){
        JoinTopicOptions topicOptions = new JoinTopicOptions();
        topicOptions.messageQos = RtmConstants.RtmMessageQos.RTM_MESSAGE_QOS_ORDERED;
        // join topic
        int errorCode = mStreamChannel.joinTopic(topicName, topicOptions);
        if(errorCode == 0){
            //发送消息，定时5s一次
            execSend();
        }else {
            Log.e("wjx", "joinTopic failed: " + errorCode);
        }
    }

//    private void subscribeUser(){
//        ArrayList<String> mSubscribeUserList = new ArrayList<>();
//        mSubscribeUserList.add("222");
//
//
//        TopicOptions options = new TopicOptions();
//        options.users = mSubscribeUserList;
//
//        // subscribe topic
//        int errorCode = mStreamChannel.subscribeTopic(topicName, options);
//
//
//        // get subscribed user list
//        UserList userList = new UserList();
//        errorCode = mStreamChannel.getSubscribedUserList(topicName, userList);
//    }

    private void setupRTM(){
       try {
           mRtmClient = RtmClient.create();
           RtmConfig rtmConfig = new RtmConfig();
           rtmConfig.mUserId = rtmUserId;
//           rtmConfig.mUserId = "clientRtmA";
           rtmConfig.mEventHandler = mRtmEventHandler;
           rtmConfig.mAppId = appId;

           //使用融合版的时候，RTC和RTm的uid类型应该保持一致，要么全用String要么全用int
           rtmConfig.mUseStringUserId = false;
           rtmConfig.mLogConfig.level = 1;
           mRtmClient.initialize(rtmConfig);
       }catch (Exception e){
           e.printStackTrace();
           Log.e("wjx", "setupRTM failed" );
       }
    }


    private void setupRemoteVideo(int uid) {
        FrameLayout container = findViewById(R.id.remote_video_view_container);
        SurfaceView surfaceView = new SurfaceView (getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        // 将 SurfaceView 对象传入声网实时互动 SDK，设置远端视图
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
    }

    private static final int PERMISSION_REQ_ID = 22;

    // 获取权限
    private String[] getRequiredPermissions(){
        // 判断 targetSDKVersion 31 及以上时所需的权限
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            return new String[]{
                    Manifest.permission.RECORD_AUDIO, // 录音权限
                    Manifest.permission.CAMERA, // 摄像头权限
                    Manifest.permission.READ_PHONE_STATE, // 读取电话状态权限
                    Manifest.permission.BLUETOOTH_CONNECT // 蓝牙连接权限
            };
        } else {
            return new String[]{
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA
            };
        }
    }

    private boolean checkPermissions() {
        for (String permission : getRequiredPermissions()) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 如果已经授权，则初始化 RtcEngine 并加入频道
        if (checkPermissions()) {
            initializeAndJoinChannel();
        } else {
            ActivityCompat.requestPermissions(this, getRequiredPermissions(), PERMISSION_REQ_ID);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 停止本地视频预览
        mRtcEngine.stopPreview();

        // 离开频道
        mRtcEngine.leaveChannel();

        //取消定时发送消息的计时器
        timer.cancel();
        //退出topic，销毁RTM频道
        mStreamChannel.leaveTopic(topicName);
        mStreamChannel.leave();
        mStreamChannel.release();
        //先销毁RTM client, 再销毁RTC Engine
        mRtmClient.release();
        mRtcEngine.destroy();
    }

    protected final void runOnUIThread(Runnable runnable, long delay) {
        if (handler != null && runnable != null && getContext() != null) {
            if (delay <= 0 && handler.getLooper().getThread() == Thread.currentThread()) {
                runnable.run();
            } else {
                handler.postDelayed(() -> {
                    if (getContext() != null) {
                        runnable.run();
                    }
                }, delay);
            }
        }
    }

    //RTC监听
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // 监听频道内的远端用户，获取用户的 uid 信息
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
                }
            });
        }

        @Override
        public void onConnectionStateChanged(int state, int reason) {
            super.onConnectionStateChanged(state, reason);
            Log.i("wjx", "RTC***onConnectionStateChanged: state---" + state + ", reason----" + reason);
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            Log.e("wjx", "RTC---onJoinChannelSuccess,channel--- " + channel + ", uid---" + uid );

//            joinRtmChannel();
        }
    };

    //RTM监听
    private final IRtmEventHandler mRtmEventHandler = new IRtmEventHandler() {
        @Override
        public void onMessageEvent(MessageEvent event) {
            String message = new String(event.message);
            Log.i("wjx", "on message event, channel: " + event.channelName + ", channelType:" + event.channelType +
                    ", topicName: " + event.topicName + ", publisher: " + event.publisher + ", message: " + message);
//            showToast("message:" + message);

        }

        @Override
        public void onPresenceEvent(PresenceEvent event) {
            Log.i("wjx", "on presence event, channel: " + event.channelName + ", channelType:" + event.channelType +
                    ", userId: " + event.userId + ", presenceType: " + event.presenceType);

            switch (event.presenceType) {
                case RTM_PRESENCE_TYPE_REMOTE_JOIN_CHANNEL: {
                    Log.i("wjx", "receive remote user(" + event.userId + ") join channel event");
                    break;
                }
                case RTM_PRESENCE_TYPE_REMOTE_LEAVE_CHANNEL: {
                    Log.i("wjx", "receive remote user("+ event.userId +") leave channel event");
                    break;
                }
                case RTM_PRESENCE_TYPE_REMOTE_JOIN_TOPIC: {
                    Log.i("wjx", "receive remote user join topic event");
                    break;
                }
                case RTM_PRESENCE_TYPE_REMOTE_LEAVE_TOPIC: {
                    Log.i("wjx", "receive remote user leave topic event");
                    break;
                }
                case RTM_PRESENCE_TYPE_REMOTE_CONNECTION_TIMEOUT: {
                    break;
                }
                case RTM_PRESENCE_TYPE_SELF_JOIN_CHANNEL: {
                    Log.i("wjx", "receive all topic info, topic size：" + (event.topicInfos == null ? 0: event.topicInfos.length));
                    break;
                }
            }

        }

        @Override
        public void onJoinResult(String channelName, String userId, int errorCode) {
            joinTopic();
            Log.e("wjx", "on join stream channel result, channel: " + channelName + ", userId: " + userId + ", errorCode: " + errorCode);
            if (errorCode == 0){
                streamChannelIn = true;
                joinRtcChannel();
            }
        }

        @Override
        public void onLeaveResult(String channelName, String userId, int errorCode) {
            Log.e("wjx", "on leave result, channel: " + channelName + ", userId: " + userId + ", errorCode: " + errorCode);
            streamChannelIn = false;
        }

        @Override
        public void onJoinTopicResult(String channelName, String userId, String topicName, String meta, int errorCode) {
            Log.e("wjx", "RTM---onjointopicresult, channel: " + channelName + ", userId: " + userId +
                    ", topicName: " + topicName + ", meta: " + meta + ", errorCode: " + errorCode);
            if (errorCode == 0){
                topicIn = true;
            }
//            joinRtcChannel();

            subscribeRTM();

        }

        @Override
        public void onLeaveTopicResult(String channelName, String userId, String topicName, String meta, int errorCode) {
            Log.i("wjx", "on leave topic result, channel: " + channelName + ", userId: " + userId +
                    ", topicName: " + topicName + ", meta: " + meta + ", errorCode: " + errorCode);
            topicIn = false;
        }

        @Override
        public void onTopicSubscribed(String channelName, String userId, String topicName, UserList successUsers, UserList failedUsers, int errorCode) {
            Log.i("wjx", "on topic subscribe result, channel: " + channelName + ", topicName: " + topicName);
            for (String user: successUsers.users) {
                Log.i("wjx", "subscribed user: " + user);
            }
        }

        @Override
        public void onTopicUnsubscribed(String channelName, String userId, String topicName, UserList successUsers, UserList failedUsers, int errorCode) {
            Log.i("wjx", "on topic unsubscribe result, channel: " + channelName + ", topicName: " + topicName);
            for (String user: successUsers.users) {
                Log.i("wjx", "unsubscribed user: " + user);
            }
        }

        @Override
        public void onConnectionStateChange(String channelName, int state, int reason) {
            Log.i("wjx", "RTM---onconnectionstatechange: " + channelName + ", state: " + state + ", reason: " + reason);
        }


    };
}
