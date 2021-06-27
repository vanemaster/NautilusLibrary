package com.example.nautiluslibrary;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LivroActivity extends AppCompatActivity {

    private BDSQLiteHelper bd;
    private File arquivoFoto = null;
    private ImageView imagem;
    private Uri imagemUri;
    private EditText nome;
    private EditText autor;
    private Spinner genero;
    private EditText ano;
    Bitmap takenImage = null;
    private String mType = "Terror";
    private Uri mCurrentContactUri;
    private boolean mContactHasChanged = false;
    private final int GALERIA_IMAGENS = 1;
    private final int PERMISSAO_REQUEST = 2;
    private final int CAMERA = 3;
    public final String APP_TAG = "NautilusLibrary";

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mContactHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livro);
        Intent intent = getIntent();
        mCurrentContactUri = intent.getData();

        bd = new BDSQLiteHelper(this);
        nome = (EditText) findViewById(R.id.edNome);
        autor = (EditText) findViewById(R.id.edAutor);
        ano = (EditText) findViewById(R.id.edAno);
        imagem = findViewById(R.id.imageFoto);
        genero = findViewById(R.id.edGenero);

        nome.setOnTouchListener(mOnTouchListener);
        autor.setOnTouchListener(mOnTouchListener);
        ano.setOnTouchListener(mOnTouchListener);
        imagem.setOnTouchListener(mOnTouchListener);

        imagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySelector();
                mContactHasChanged = true;
            }
        });

        Button novo = (Button) findViewById(R.id.btnAdd);
        novo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(arquivoFoto != null){
                    takenImage = BitmapFactory.decodeFile(arquivoFoto.getAbsolutePath());
                }

                Livro livro = new Livro();
                livro.setTitulo(nome.getText().toString());
                livro.setAutor(autor.getText().toString());
                livro.setAno(Integer.parseInt(ano.getText().toString()));
                livro.setGenero(genero.getSelectedItem().toString());
                livro.setFoto(getBytesFromBitmap(takenImage));
                bd.addLivro(livro);
                Intent intent = new Intent(LivroActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        setUpSpinner();
    }

    private void setUpSpinner() {

        ArrayAdapter spinner = ArrayAdapter.createFromResource(this, R.array.arrayspinner, android.R.layout.simple_spinner_item);
        spinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        genero.setAdapter(spinner);

        genero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    mType = genero.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mType = "romance";

            }
        });
    }
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, timeStamp, null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    public void trySelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }
        openSelector();
    }

    private void openSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType(getString(R.string.intent_type));
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == GALERIA_IMAGENS) {
            if (data != null) {
                imagemUri = data.getData();
                imagem.setImageURI(imagemUri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagemUri);
                    Uri tempUri = getImageUri(getApplicationContext(), bitmap);
                    arquivoFoto = new File(getRealPathFromURI(tempUri));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (resultCode == RESULT_OK && requestCode == CAMERA) {
            Bitmap takenImage = BitmapFactory.decodeFile(arquivoFoto.getAbsolutePath());
            ImageView ivPreview = (ImageView) findViewById(R.id.imageFoto);
            ivPreview.setImageBitmap(takenImage);
        }
    }

    public void buscar(View view) {

        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, GALERIA_IMAGENS);
    }

    public void tirarFoto(View view) {
        Intent takePictureIntent = new
                Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            try {
                arquivoFoto = criaArquivo();
            } catch (IOException ex) {
                Log.v("erro_salvar_imagem",ex.toString());
            }

            if (arquivoFoto != null) {
                Uri photoURI = FileProvider.getUriForFile(getBaseContext(),
                        getBaseContext().getApplicationContext().getPackageName() +
                                ".provider", arquivoFoto);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA);
            }
        }
    }

    private File criaArquivo() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File pasta = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        arquivoFoto = new File(pasta.getPath() + File.separator
                + "JPG_" + timeStamp + ".jpg");

        return arquivoFoto;
    }

}
