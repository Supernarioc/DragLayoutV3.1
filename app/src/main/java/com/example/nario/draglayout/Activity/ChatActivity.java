package com.example.nario.draglayout.Activity;

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
import com.example.nario.draglayout.HeadIconSelectorView;
import com.example.nario.draglayout.ItemModel;
import com.example.nario.draglayout.KeyBoardUtils;
import com.example.nario.draglayout.Option_menu;
import com.example.nario.draglayout.R;
import com.example.nario.draglayout.Screen_info;
import com.example.nario.draglayout.TestData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private EditText et;
    private Button option;
    private String content;
    private TextView chatName;
    private ImageButton back_but;
    private Toast mToast;
    private String camPicPath;
    private Option_menu option_menu;
    private boolean CAN_WRITE_EXTERNAL_STORAGE = true;
    private ArrayList<ItemModel> data = new ArrayList<>();
    public ListView mess_lv;
    public Screen_info screen_info;

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
//                    case option_menu.FROM_CAMERA:
//                        if (!CAN_WRITE_EXTERNAL_STORAGE) {
//                            Toast.makeText(ChatActivity.this, "权限未开通\n请到设置中开通相册权限", Toast.LENGTH_SHORT).show();
//                        } else {
//                            final String state = Environment.getExternalStorageState();
//                            if (Environment.MEDIA_MOUNTED.equals(state)) {
//                                camPicPath = getSavePicPath();
//                                Intent openCameraIntent = new Intent(
//                                        MediaStore.ACTION_IMAGE_CAPTURE);
//                                Uri uri = Uri.fromFile(new File(camPicPath));
//                                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//                                startActivityForResult(openCameraIntent,
//                                        option_menu.FROM_CAMERA);
//                            } else {
//                                showToast("Check memory card");
//                            }
//                        }
//                        break;
                    case Option_menu.FROM_GALLERY:
                        if (!CAN_WRITE_EXTERNAL_STORAGE) {
                            Toast.makeText(ChatActivity.this, "权限未开通\n请到设置中开通相册权限", Toast.LENGTH_SHORT).show();
                        } else {
                            String status = Environment.getExternalStorageState();
                            if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
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
                                showToast("没有SD卡");
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
        chatName.setText(getIntent().getExtras().getString("chatfragment"));
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
        Uri uri = data.getData();
        String path = uri.getPath();

//        send_img(uri);
        test_send(uri);
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

    public void test_send(Uri pic_uri) {
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

    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;    final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width,计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高一定都会大于等于目标的宽和高。
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            // Anything more than 2x the requested pixels we'll sample down
            final float totalPixels = width * height;
            // further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }    return inSampleSize;
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
