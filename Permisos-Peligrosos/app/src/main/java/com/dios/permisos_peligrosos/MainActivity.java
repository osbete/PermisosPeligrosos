package com.dios.permisos_peligrosos;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.support.v4.content.FileProvider.getUriForFile;

import java.io.File;
import java.net.URI;

import static com.dios.permisos_peligrosos.R.id.lblPeligro;


public class MainActivity extends AppCompatActivity {

    private static final int SOLICITUD_PERMISO_CALL_PHONE = 1;
    private static final int SOLICITUD_FOTO = 2;
    private Intent intentLLamada;
    private final String CARPETA_RAIZ = "misImagenesPrueba/";
    private final String RUTA_IMAGEN = CARPETA_RAIZ + "misFotos";
    final int COD_FOTO = 20;
    String path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intentLLamada = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "*333"));
        Button btnLlamada = (Button) findViewById(R.id.btnLlamar);
        Button btnFoto = (Button) findViewById(R.id.btnFoto);


        btnLlamada.setOnClickListener(new View.OnClickListener() { // hago clic en el botón
            @Override
            public void onClick(View v) {
                //solicitarPermisoHacerLlamada();
                pedirHacerllamada();

            }
        });

        btnFoto.setOnClickListener(new View.OnClickListener() { // hago clic en el botón
            @Override
            public void onClick(View v) {
                //validaPermisos();
                pedirHacerFoto();
                //solicitarPermisoCamara();
                //tomarFotografia();


            }
        });


    }

    private boolean validaPermisos() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if ((checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        }

        if ((shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))) {
            alertDialogBasico();
        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, 100);
        }

        return false;
    }


    private void tomarFotografia() {
        File fileImagen = new File(Environment.getExternalStorageDirectory(), RUTA_IMAGEN);
        boolean isCreada = fileImagen.exists();
        String nombreImagen = "";
        if (!isCreada) {
            isCreada = fileImagen.mkdirs();
        }

        if (isCreada) {
            nombreImagen = (System.currentTimeMillis() / 1000) + ".jpg";
        }


        path = Environment.getExternalStorageDirectory() +
                File.separator + RUTA_IMAGEN + File.separator + nombreImagen;

        File imagen = new File(path);

        Intent intent = null;
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ////
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authorities = getApplicationContext().getPackageName() + ".provider";
            Uri imageUri = FileProvider.getUriForFile(this, authorities, imagen);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
        }
        startActivityForResult(intent, COD_FOTO);

        ////
    }

    public void pedirHacerllamada() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

            TextView lblPeligro = (TextView) findViewById(R.id.lblPeligro);
            lblPeligro.setVisibility(View.INVISIBLE);
            startActivity(intentLLamada);

        } else {
            explicarUsoPermiso();
            //solicitarPermisoHacerLlamada();

        }

    }

    public void pedirHacerFoto() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            tomarFotografia();


        } else {
            explicarUsoPermiso();
            //solicitarPermisoCamara();

        }

    }

    private void explicarUsoPermiso() {

        //Para ver si se marco no volver a preguntar
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            alertDialogBasico();
            solicitarPermisoCamara();
        }else if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) && (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))) {
            alertDialogBasico();
            solicitarPermisoHacerLlamada();
        }

    }

    private void solicitarPermisoHacerLlamada() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CALL_PHONE},
                SOLICITUD_PERMISO_CALL_PHONE);


    }

    private void solicitarPermisoCamara() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE},
                SOLICITUD_FOTO);



    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SOLICITUD_PERMISO_CALL_PHONE) {

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                startActivity(intentLLamada);

            }
        }else if (requestCode == SOLICITUD_FOTO) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                tomarFotografia();

            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case COD_FOTO:

                    MediaScannerConnection.scanFile(this, new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("Ruta de almacenamiento", "Path: " + path);
                                }
                            });

                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    ImageView imagen = (ImageView) findViewById(R.id.imageView2);
                    imagen.setImageBitmap(bitmap);
                    break;
                default:
                    break;
            }


        }
    }

    public void alertDialogBasico() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_message);


        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });


        builder.show();

    }

}
