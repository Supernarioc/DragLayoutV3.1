package com.example.nario.draglayout.Activity;

import android.content.ClipData;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nario.draglayout.Adapter.ChatAdapter;
import com.example.nario.draglayout.ChatModel;
import com.example.nario.draglayout.FileSaveUtil;
import com.example.nario.draglayout.HeadIconSelectorView;
import com.example.nario.draglayout.ItemModel;
import com.example.nario.draglayout.KeyBoardUtils;
import com.example.nario.draglayout.Option_menu;
import com.example.nario.draglayout.R;
import com.example.nario.draglayout.Screen_info;
import com.example.nario.draglayout.TestData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private EditText et;
    private Button option;
    private String content;
    private TextView chatName;
    private ImageButton back_but;
    private Toast mToast;
    private Option_menu option_menu;
    private Uri uri;
    private boolean CAN_WRITE_EXTERNAL_STORAGE = true;
    private ArrayList<ItemModel> data = new ArrayList<>();
    public ListView mess_lv;
    public Screen_info screen_info;
    private static int REQUEST_THUMBNAIL = 11;//scaled image
    private static int REQUEST_ORIGINAL = 22;//original image
    public static final int MEDIA_TYPE_IMAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screen_info = new Screen_info(metrics.widthPixels, metrics.heightPixels);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mess_lv = (ListView) findViewById(R.id.mess_lv);
        option_menu = (Option_menu) findViewById(R.id.option_menu_view);
        et = (EditText) findViewById(R.id.et_input);
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                option_menu.setVisibility(View.GONE);
                mess_lv.setVisibility(View.GONE);
                option.setBackgroundResource(R.drawable.tb_more);
            }
        });
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEND:
                        send_msg();
                }
                return true;
            }
        });
        option = (Button) findViewById(R.id.option);
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (option_menu.getVisibility() == View.GONE
                        && mess_lv.getVisibility() == View.GONE) {
                    et.setVisibility(View.VISIBLE);
                    option.setFocusable(true);
                    option_menu.setVisibility(View.VISIBLE);
                    KeyBoardUtils.hideKeyBoard(ChatActivity.this,
                            et);
                    option.setBackgroundResource(R.drawable.chatting_setmode_keyboard_btn_normal);
                } else {
                    option_menu.setVisibility(View.GONE);
                    KeyBoardUtils.showKeyBoard(ChatActivity.this, et);
                    option.setBackgroundResource(R.drawable.tb_more);
                    if (mess_lv.getVisibility() != View.GONE) {
                        mess_lv.setVisibility(View.GONE);
                        KeyBoardUtils.showKeyBoard(ChatActivity.this, et);
                        option.setBackgroundResource(R.drawable.tb_more);
                    }
                }
            }
        });
        option_menu.setOnHeadIconClickListener(new HeadIconSelectorView.OnHeadIconClickListener() {
            @Override
            public void onClick(int from) {
                switch (from) {
                    case Option_menu.FROM_CAMERA:
                        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File picDirect = new File(Environment.getExternalStorageDirectory(),"NewImages");
                        picDirect.mkdirs();
                        String picname = "newiamge.jpg";
                        File imagefile = new File(picDirect,picname);
                        Uri picuri = Uri.fromFile(imagefile);
                        intent1.putExtra(MediaStore.EXTRA_OUTPUT,picuri);
                        startActivityForResult(intent1, REQUEST_ORIGINAL);
                        break;
                    case Option_menu.FROM_GALLERY:
                        if (!CAN_WRITE_EXTERNAL_STORAGE) {
                            Toast.makeText(ChatActivity.this, "Permission not granted\nGo to setting", Toast.LENGTH_SHORT).show();
                        } else {
                            String status = Environment.getExternalStorageState();
                            if (status.equals(Environment.MEDIA_MOUNTED)) {// sd card
                                Intent intent = new Intent();
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                } else {
                                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                    intent.putExtra("crop", "true");
                                    intent.putExtra("scale", "true");
                                    intent.putExtra("scaleUpIfNeeded", true);
                                }
                                intent.setType("image/*");
                                startActivityForResult(intent,
                                        option_menu.FROM_GALLERY);
                            } else {
                                showToast("no sd card found");
                            }
                        }
                        break;
//                    case option_menu.FROM_PHRASE:
//                        if (mess_lv.getVisibility() == View.GONE) {
//                            tbbv.setVisibility(View.GONE);
//                            emoji.setBackgroundResource(R.mipmap.emoji);
//                            voiceIv.setBackgroundResource(R.mipmap.voice_btn_normal);
//                            mess_lv.setVisibility(View.VISIBLE);
//                            KeyBoardUtils.hideKeyBoard(BaseActivity.this,
//                                    mEditTextContent);
//                            mess_iv.setBackgroundResource(R.mipmap.chatting_setmode_keyboard_btn_normal);
//                        }
                }
            }
        });
        chatName = (TextView) findViewById(R.id.Chat_name);
        back_but = (ImageButton) findViewById(R.id.back_but);
        back_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ChatAdapter(data);
        adapter.setOnItemClickLitener(new ChatAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(ChatActivity.this, position + " click", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(ChatActivity.this, position + " long click", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.replaceAll(TestData.getTestAdData());
        initData();
    }

    private void initData() {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                content = s.toString().trim();
            }
        });
    }

    @Override
    public void onActivityResult(int req, int res, Intent data) {
        //request code
        //take photo: 1
        //choose photo: 2
        if (res == RESULT_OK) {

            if (req == 22) {
                Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                ArrayList<ItemModel> datas = new ArrayList<>();
                ChatModel model = new ChatModel();
                model.setBitmap(bitmap);
                datas.add(new ItemModel(ItemModel.PHOTO, model));
                adapter.addAll(datas);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                et.setText("");
            }
            if (req == 2) {
                Uri uri = data.getData();
                String path = uri.getPath();
//              send_img(uri);
                test_send_g(uri);
            }
        } else if (res == RESULT_CANCELED) {
        }
    }

    public void send_msg() {
        ArrayList<ItemModel> data = new ArrayList<>();
        ChatModel model = new ChatModel();
        model.setIcon("http://img.my.csdn.net/uploads/201508/05/1438760758_6667.jpg");
        if (content == null || content.isEmpty()) {
        } else {
            model.setContent(content);
            data.add(new ItemModel(ItemModel.CHAT_A, model));
            adapter.addAll(data);
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            et.setText("");
        }
    }

    public void send_img(Uri pic_uri) {
        ArrayList<ItemModel> data = new ArrayList<>();
        ChatModel model = new ChatModel();
        model.setImg(pic_uri);
        data.add(new ItemModel(ItemModel.PHOTO, model));
        adapter.addAll(data);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        et.setText("");
    }

    public void test_send_g(Uri pic_uri) {
        ArrayList<ItemModel> data = new ArrayList<>();
        ChatModel model = new ChatModel();
        try {
            Bitmap bit;
            bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(pic_uri));
            if (bit.getWidth() > screen_info.getWid() / 2) {
//                decodeSampledBitmapFromResource(bit,);
            }
            model.setBitmap(bit);

            data.add(new ItemModel(ItemModel.PHOTO, model));
            adapter.addAll(data);
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            et.setText("");
        } catch (Exception ex) {
            Log.v("Exception", ex + "");
        }
    }

    public void test_send_c(Bitmap bit) {
        ArrayList<ItemModel> data = new ArrayList<>();
        ChatModel model = new ChatModel();
        try {
            if (bit.getWidth() > screen_info.getWid() / 2) {

            }
            model.setBitmap(bit);
            data.add(new ItemModel(ItemModel.PHOTO, model));
            adapter.addAll(data);
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            et.setText("");
        } catch (Exception ex) {
            Log.v("Exception", ex + "");
        }
    }

    private File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = null;
        try {
            // This location works best if you want the created images to be
            // shared
            // between applications and persist after your app has been
            // uninstalled.
            mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "MyCameraApp");

            Log.d("log msg", "Successfully created mediaStorageDir: "
                    + mediaStorageDir);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("log msg", "Error in Creating mediaStorageDir: "
                    + mediaStorageDir);
        }

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                // <uses-permission
                // android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
                Log.d("log msg",
                        "failed to create directory, check if you have the WRITE_EXTERNAL_STORAGE permission");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);

            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            // Anything more than 2x the requested pixels we'll sample down
            final float totalPixels = width * height;
            // further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    public void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

//    private String getSavePicPath() {
//        final String dir = FileSaveUtil.SD_CARD_PATH + "image_data/";
//        try {
//            FileSaveUtil.createSDDirectory(dir);
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        String fileName = String.valueOf(System.currentTimeMillis() + ".png");
//        return dir + fileName;
//    }
}
