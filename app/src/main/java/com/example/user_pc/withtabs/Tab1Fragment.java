package com.example.user_pc.withtabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.system.ErrnoException;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImageView;

public class Tab1Fragment extends Fragment {
    public static final String PACKAGE_NAME = "com.example.user_pc.tess6";
    public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/tess6/";
    public static final String lang = "eng";
    public static final String EXTRA_MESSAGE = "Hello";
    private static final String TAG = "SimpleAndroidOCR.java";
    protected static final String PHOTO_TAKEN = "photo_taken";
    private static String appid="9HH8PL-88HVLXW5KE"; // App ID

    private final static int BUFFER_SIZE = 1024;

    protected Button imgbtn;
    protected Button cropbtn;
    protected EditText _field;
    protected String _path;
    protected boolean _taken;
    private CropImageView mCropImageView;
    private Uri mCropImageUri;
    private ViewPager viewPager;


    private SendMessage SM;
    private String m_Text;
    private String inputText="";
    private String res="";
    private String result;
    private DbHelper dbHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.fragment_one, container, false);
        requestMultiplePermissions();
        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

        dbHelper = new DbHelper(getContext());
        cropbtn=(Button)view.findViewById(R.id.cropbtn);
        imgbtn = (Button)view.findViewById(R.id.img);
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewPager);



        //--------------------------------add btn onclick listner for select image button and crop button
        imgbtn.setOnClickListener(new View.OnClickListener() //open gallery section
        {
            @Override
            public void onClick(View v)
            {
                startActivityForResult(getPickImageChooserIntent(), 200);
            }
        });
        cropbtn.setOnClickListener(new View.OnClickListener() //set cropped image
        {
            @Override
            public void onClick(View v)
            {
                Bitmap cropped = mCropImageView.getCroppedImage(500, 500);
                if (cropped != null)
                    mCropImageView.setImageBitmap(cropped);
                    new OCR().execute(cropped,view);//-----------------------implement background thread for OCR
            }
        });
        //---------------------------------------------------------------check availability of trained data in device
        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return view;
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }
        }

        //---------------------------save eng.traindata and equ1.traineddata file in mobile device if these files not already exist -----------------------------

        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            try {

                AssetManager assetManager = getActivity().getAssets();
                InputStream in = assetManager.open( lang + ".traineddata");
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + lang + ".traineddata");
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }
        }
        //-----------------------------------------------------------------------------------

        //-----------------------------------------------------equtraineddata-------------------------

        if (!(new File(DATA_PATH + "tessdata/" + "equ1.traineddata")).exists()) {
            try {

                AssetManager assetManager = getActivity().getAssets();
                InputStream in = assetManager.open("equ1.traineddata");
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/"  + "equ1.traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();

                Log.v(TAG, "Copied equ1.traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy  equ1.traineddata " + e.toString());
            }
        }
        mCropImageView = (CropImageView)view.findViewById(R.id.CropImageView);
        _path = DATA_PATH + "/ocr";
        return view;
    }

    //----------------------------------------------------Background task for Optical Character Recognition

    private class OCR extends AsyncTask<Object, Void, View> {
        String recognizedText;
        @Override
        protected View doInBackground(Object... params) {
            TessBaseAPI baseApi = new TessBaseAPI();
            baseApi.setDebug(true);
            baseApi.init(DATA_PATH, lang);
            Bitmap b = (Bitmap) params[0];
            View v = (View) params[1];
            baseApi.setImage(b);
            recognizedText = baseApi.getUTF8Text();
            Log.v(TAG, "OCRED TEXT: " + recognizedText);
            baseApi.end();

            return v;
        }

        @Override
        protected void onPostExecute(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Edit Input");
            View viewInflated = LayoutInflater.from(getActivity()).inflate(R.layout.dialogbox, (ViewGroup)view.findViewById(android.R.id.content), false);
            final EditText input = (EditText) viewInflated.findViewById(R.id.input);
            input.setText(recognizedText);
            builder.setView(viewInflated);

            //--------------------------------------- Set up the buttons within dialog box
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();
                    Pattern p = Pattern.compile("[!@#$&_<>?{}\\\\[\\\\]~]");
                    Matcher m = p.matcher(m_Text);
                    boolean b = m.find();

                    // ---------------------------------------------validate user input in dialog box

                    if (m_Text == null || m_Text.trim().isEmpty()) {
                        Toast.makeText(getActivity(), "Incorrect format of string", Toast.LENGTH_SHORT).show();

                    }

                    else if (m_Text.length()>20){
                        Toast.makeText(getActivity(), "Enter valid expression!", Toast.LENGTH_SHORT).show();

                    }

                    else if (b){
                        Toast.makeText(getActivity(), "Enter valid symbols!", Toast.LENGTH_SHORT).show();

                    }
                    else{
                        SM.sendData(m_Text);
                        if(dbHelper.insertPerson(m_Text)){
                            Log.v("bundle","enter ti db");
                        }
                        dialog.dismiss();
                        viewPager.setCurrentItem(1); // change to result page
                    }
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();


        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    //---------------------------------------------------------------------------------------------------------


    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            Log.v(TAG, "Starting Camera app");
            startCameraActivity();
        }
    }

    //-------------------------------------------------------------------- Simple android photo capture methods

    protected void startCameraActivity() {
        File file = new File(_path);
        Uri outputFileUri = FileProvider.getUriForFile(getActivity(),
                BuildConfig.APPLICATION_ID + ".provider",
                file);
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(this.PHOTO_TAKEN, _taken);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState()");
        if(savedInstanceState!=null) {
            if (savedInstanceState.getBoolean(PHOTO_TAKEN)) {
                onPhotoTaken();
            }
        }
    }

    protected void onPhotoTaken() {

        _taken = true;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeFile(_path, options);

        try {
            ExifInterface exif = new ExifInterface(_path);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            Log.v(TAG, "Orient: " + exifOrientation);

            int rotate = 0;

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            Log.v(TAG, "Rotation: " + rotate);

            if (rotate != 0) {

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
            }
            // Convert to ARGB_8888, required by tess
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        } catch (IOException e) {
            Log.e(TAG, "Couldn't correct orientation: " + e.toString());
        }

        // _image.setImageBitmap( bitmap );

        Log.v(TAG, "Before baseApi");

        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(DATA_PATH, lang);
        baseApi.setImage(bitmap);

        String recognizedText = baseApi.getUTF8Text();

        baseApi.end();

        // You now have the text in recognizedText var, you can do anything with it.
        // We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
        // so that garbage doesn't make it to the display.

        Log.v(TAG, "OCRED TEXT: " + recognizedText);

        if ( lang.equalsIgnoreCase("eng") ) {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
        }

        recognizedText = recognizedText.trim();

        if ( recognizedText.length() != 0 ) {
            _field.setText(_field.getText().toString().length() == 0 ? recognizedText : _field.getText() + " " + recognizedText);
            _field.setSelection(_field.getText().toString().length());
        }

        // Cycle done.
    }
    private void  requestMultiplePermissions(){
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                           // Toast.makeText(getActivity(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getActivity(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    public Intent getPickImageChooserIntent() {

        //--------------------------------------------------- Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getActivity().getPackageManager();

        // --------------------------------------------------------------------------------------collect all camera intents of the device
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // ---------------------------------------------------------------------------------------collect all gallery intents of the device
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list  so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));


        return chooserIntent;
    }
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getActivity().getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "onActivityResult");
        Log.v(TAG, "resultCode: " + resultCode + Activity.RESULT_OK);


        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = getPickImageResultUri(data);
            Log.v(TAG, "imageUri" +imageUri );

            if (imageUri != null){
                cropbtn.setVisibility(View.VISIBLE);
            }

            // For API >= 23 we need to check specifically that we have permissions to read external storage,
            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
            boolean requirePermissions = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    isUriRequiresPermissions(imageUri)) {

                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }

            if (!requirePermissions) {
                Log.v(TAG, "yeppp");
                mCropImageView.setImageUriAsync(imageUri);
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.v(TAG, "onRequestPermissionsResult");
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mCropImageView.setImageUriAsync(mCropImageUri);
        } else {
            Log.v(TAG, "Required permissions are not granted");
            Toast.makeText(getActivity(), "Required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }


     //Test if we can open the given Android URI to test if permission required error is thrown.

    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = getActivity().getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (e.getCause() instanceof ErrnoException) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }
    interface SendMessage {
        void sendData(String message);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            SM = (SendMessage) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }
}
