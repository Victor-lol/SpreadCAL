package com.example.spreadcal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.content.Intent;
import org.opencv.core.Size;
import org.opencv.android.Utils;
import org.opencv.imgproc.Imgproc;
import android.graphics.Matrix;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import org.opencv.osgi.OpenCVNativeLoader;
import org.w3c.dom.Text;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button takephotoTV;
    private Button calculateTV;
    private ImageView imageIV;
    private TextView weight;
    private Bitmap mapPhoto = null;
    private final int CAMERA_REQUEST = 8888;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    100);
        }

        imageIV = (ImageView) findViewById(R.id.imageIV);
        takephotoTV = (Button) findViewById(R.id.takephotoTV);
        calculateTV = (Button) findViewById(R.id.calculateTV);
        weight = (TextView) findViewById(R.id.weight);

        OpenCVNativeLoader loader = new OpenCVNativeLoader();
        loader.init();

        Mat img = null;

        try{
            img = Utils.loadResource(getApplicationContext(), R.drawable.template);
        } catch(IOException e){
            e.printStackTrace();
        }
        Bitmap img_bitmap = Bitmap.createBitmap(img.cols(), img.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img, img_bitmap);
        imageIV.setImageBitmap(img_bitmap);

        takephotoTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });

        Mat finalImg = img;
        Mat finalImg1 = img;
        calculateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Mat grayMat = new Mat();
//                Utils.bitmapToMat(mapPhoto, img);
//
//                Imgproc.cvtColor(img, grayMat, Imgproc.COLOR_RGB2GRAY);
//                Imgproc.threshold(grayMat, grayMat, 120, 255, Imgproc.THRESH_BINARY);
//
//                Bitmap img_bitmap = Bitmap.createBitmap(grayMat.cols(), grayMat.rows(),Bitmap.Config.ARGB_8888);
//                Utils.matToBitmap(grayMat, img_bitmap);
//                imageIV.setImageBitmap(img_bitmap);

                Imgproc.cvtColor(finalImg, finalImg1, Imgproc.COLOR_RGB2GRAY);
                Size kernel = new Size(3,3);
                Imgproc.GaussianBlur(finalImg1, finalImg1, kernel ,1);
                Imgproc.adaptiveThreshold(finalImg1, grayMat, 255.0, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                        Imgproc.THRESH_BINARY, 5, -13.0);

                int nonZero = Core.countNonZero(grayMat);
                weight.setText(String.valueOf(nonZero));
                Bitmap img_bitmap = Bitmap.createBitmap(grayMat.cols(), grayMat.rows(),Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(grayMat, img_bitmap);
                imageIV.setImageBitmap(img_bitmap);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap result = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
            mapPhoto = result.copy(Bitmap.Config.ARGB_8888, true);
            imageIV.setImageBitmap(result);
        }
    }

}