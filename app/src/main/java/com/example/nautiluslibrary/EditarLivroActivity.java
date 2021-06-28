package com.example.nautiluslibrary;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EditarLivroActivity extends AppCompatActivity {

    private BDSQLiteHelper bd;
    private File arquivoFoto = null;
    private ImageView imagem;
    private Uri imagemUri;
    private EditText nome;
    private EditText autor;
    private Spinner genero;
    private EditText ano;
    Bitmap takenImage = null;
    byte[] save_image = null;
    private String mType = "Terror";
    private Uri mCurrentContactUri;
    private boolean mContactHasChanged = false;
    private final int GALERIA_IMAGENS = 1;
    private final int PERMISSAO_REQUEST = 2;
    private final int CAMERA = 3;
    public final String APP_TAG = "NautilusLibrary";
    private ArrayAdapter spinner;

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
        setContentView(R.layout.activity_editar_livro);
        Intent intent = getIntent();

        final int id = intent.getIntExtra("ID", 0);

        bd = new BDSQLiteHelper(this);

        Livro livro = bd.getLivro(id);

        nome = findViewById(R.id.edNome);
        autor = findViewById(R.id.edAutor);
        ano = findViewById(R.id.edAno);
        genero = findViewById(R.id.edGenero);
        imagem = findViewById(R.id.imageFoto);

        nome.setText(livro.getTitulo());
        autor.setText(livro.getAutor());
        ano.setText(String.valueOf(livro.getAno()));

        //Select Generos
        setUpSpinner();
        genero.setSelection(spinner.getPosition(livro.getGenero()));

        // Imagem do BD pro layout
        byte[] img = livro.getFoto();
        Bitmap bmp = BitmapFactory.decodeByteArray(img, 0, img.length);
        imagem.setImageBitmap(Bitmap.createBitmap(bmp));

        final Button alterar = (Button) findViewById(R.id.btnAlterar);
        alterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arquivoFoto != null){
                    takenImage = BitmapFactory.decodeFile(arquivoFoto.getAbsolutePath());
                    save_image = getBytesFromBitmap(takenImage);
                }else{
                    save_image = livro.getFoto();
                }

                Livro livro = new Livro();
                livro.setId(id);
                livro.setTitulo(nome.getText().toString());
                livro.setAutor(autor.getText().toString());
                livro.setAno(Integer.parseInt(ano.getText().toString()));
                livro.setGenero(genero.getSelectedItem().toString());
                livro.setFoto(save_image);
                bd.updateLivro(livro);
                Intent intent = new Intent(EditarLivroActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final Button remover = (Button) findViewById(R.id.btnRemover);
        remover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(EditarLivroActivity.this)
                        .setTitle(R.string.confirmar_exclusao)
                        .setMessage(R.string.quer_mesmo_apagar)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Livro livro = new Livro();
                                livro.setId(id);
                                bd.deleteLivro(livro);
                                Intent intent = new Intent(EditarLivroActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        final Button share = (Button) findViewById((R.id.btnShare));
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent shareIntent = new Intent();

                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, livro.getTitulo()+" - "+livro.getAutor());
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Compartilhe"));
            }
        });
    }


    private void setUpSpinner() {

        spinner = ArrayAdapter.createFromResource(this, R.array.arrayspinner, android.R.layout.simple_spinner_item);
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