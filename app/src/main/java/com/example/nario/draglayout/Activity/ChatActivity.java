package com.example.nario.draglayout.Activity;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
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
    private static int REQUEST_ORIGINAL = 22;//original image
    private static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0;//original image
    public static final int MEDIA_TYPE_IMAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);

        requestPermission();

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
                        startActivityForResult(intent1, REQUEST_ORIGINAL);
                        break;
                    case Option_menu.FROM_GALLERY:
                        if (!CAN_WRITE_EXTERNAL_STORAGE) {
                            Toast.makeText(ChatActivity.this, "Permission not granted\nGo to setting", Toast.LENGTH_SHORT).show();
                        } else {
                            String status = Environment.getExternalStorageState();
                            if (status.equals(Environment.MEDIA_MOUNTED)) {// sd card
                                Intent intent = new Intent();
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
        String name = getIntent().getStringExtra("chatfragment");
        chatName.setText(name);
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
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                test_send_c(bitmap);
            }
            if (req == 2) {
                Uri uri = data.getData();
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
            data.add(new ItemModel(ItemModel.CHAT_B, model));
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
            model.setBitmap(scaled(getRealPathFromURI(pic_uri)));

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

    private Bitmap scaled(String pathname) {
        int targetW = 300;
        int targetH = 300;

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathname, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(pathname, bmOptions);
        return bitmap;
    }

    public String getRealPathFromURI(Uri contentUri) {
        String wholeID = DocumentsContract.getDocumentId(contentUri);

        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

        String filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(ChatActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChatActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(getApplicationContext(), "deny", Toast.LENGTH_SHORT).show();
            }
        }
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

}
