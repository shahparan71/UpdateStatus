package com.bang.update.statusupdate.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bang.update.statusupdate.R;
import com.bang.update.statusupdate.adapter.ListAdapterRecycle;
import com.bang.update.statusupdate.model.UpdateInformation;
import com.bang.update.statusupdate.sqlite.SQLiteHelper;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    EditText edtName;
    Button btnList;
    LinearLayout btnChoose,btnAdd;
    ImageView imageView;

    final int REQUEST_CODE_GALLERY = 999;

    public static SQLiteHelper sqLiteHelper;

    //GridView gridView;
    ArrayList<UpdateInformation> list;
    //FoodListAdapter adapter = null;
    ListAdapterRecycle adapterRecycle;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        sqLiteHelper = new SQLiteHelper(this, "FoodDB.sqlite", null, 1);
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS STATUS(Id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, price VARCHAR, image BLOB)");

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtName.getText().toString().isEmpty()){
                    edtName.setError("Write something..");
                    return;
                }

                try{
                    sqLiteHelper.insertData(
                            edtName.getText().toString().trim(),
                            "20",
                            imageViewToByte(imageView)
                    );
                    Toast.makeText(getApplicationContext(), "Added successfully!", Toast.LENGTH_SHORT).show();
                    edtName.setText("");

                    imageView.setImageResource(R.mipmap.ic_launcher);
                    imageView.setVisibility(View.GONE);

                    Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM STATUS");
                    list.clear();

                    if(cursor!=null && cursor.getCount() > 0)
                    {
                        cursor.moveToFirst ();
                        if (cursor.moveToFirst())
                        {
                            do {
                                int id = cursor.getInt(0);
                                String name = cursor.getString(1);
                                String price = cursor.getString(2);
                                byte[] image = cursor.getBlob(3);

                                list.add(new UpdateInformation(name, price, image, id));
                            } while (cursor.moveToNext());
                        }

                    }

                    Collections.reverse(list);

                    adapterRecycle.notifyDataSetChanged();

                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


        /*gridView = (GridView) findViewById(R.id.gridView);*/
        list = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapterRecycle = new ListAdapterRecycle(list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapterRecycle);

        // get all data from sqlite
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM STATUS");
        list.clear();

        if(cursor!=null && cursor.getCount() > 0)
        {
            cursor.moveToFirst ();
            if (cursor.moveToFirst())
            {
                do {
                    int id = cursor.getInt(0);
                    String name = cursor.getString(1);
                    String price = cursor.getString(2);
                    byte[] image = cursor.getBlob(3);

                    list.add(new UpdateInformation(name, price, image, id));
                } while (cursor.moveToNext());
            }

            Collections.reverse(list);
        }

        /*while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String price = cursor.getString(2);
            byte[] image = cursor.getBlob(3);

            list.add(new Food(name, price, image, id));
        }*/

        adapterRecycle.notifyDataSetChanged();

        /*gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                CharSequence[] items = {"Update", "Delete"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            // update
                            Cursor c = MainActivity.sqLiteHelper.getData("SELECT id FROM STATUS");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            // show dialog update at here
                            showDialogUpdate(MainActivity.this, arrID.get(position));

                        } else {
                            // delete
                            Cursor c = MainActivity.sqLiteHelper.getData("SELECT id FROM STATUS");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });*/
    }

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if(requestCode == 888){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 888);
            }
            else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null){

            Uri uri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                imageView.setVisibility(View.VISIBLE);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if(requestCode == 888 && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageViewFood.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init(){
        edtName = (EditText) findViewById(R.id.edtName);
        btnChoose = (LinearLayout) findViewById(R.id.btnChoose);
        btnAdd = (LinearLayout) findViewById(R.id.btnAdd);
        /*btnList = (Button) findViewById(R.id.btnList);
        imageView = (ImageView) findViewById(R.id.imageView);
*/
        imageView = (ImageView) findViewById(R.id.imageView);

        if(edtName.getText().toString().isEmpty())
            btnAdd.setVisibility(View.GONE);
        else
            btnAdd.setVisibility(View.VISIBLE);

        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(edtName.getText().toString().isEmpty())
                    btnAdd.setVisibility(View.GONE);
                else
                    btnAdd.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    ImageView imageViewFood;

    private void showDialogDelete(final int idFood){

        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(MainActivity.this);

        dialogDelete.setTitle("Warning!!");
        dialogDelete.setMessage("Are you sure you want to this delete?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    MainActivity.sqLiteHelper.deleteData(idFood);
                    Toast.makeText(getApplicationContext(), "Delete successfully!!!",Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    Log.e("error", e.getMessage());
                }
                updateFoodList();
            }
        });

        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }

    private void updateFoodList(){
        // get all data from sqlite
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM STATUS");
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String price = cursor.getString(2);
            byte[] image = cursor.getBlob(3);

            list.add(new UpdateInformation(name, price, image, id));
        }
        /*adapter.notifyDataSetChanged();*/
    }


}
