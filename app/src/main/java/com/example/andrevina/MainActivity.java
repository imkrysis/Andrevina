package com.example.andrevina;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class MainActivity extends AppCompatActivity {

    final File filesDir = new File ("/data/user/0/com.example.pr21_capturaimatges/files");
    final File photoDir = new File ("/data/user/0/com.example.pr21_capturaimatges/files/photos");
    final File rankingFile = new File ("/data/user/0/com.example.pr21_capturaimatges/files/ranking.xml");
    final String photoExt = ".jpeg";

    File photoFile;

    DocumentBuilderFactory dbf;
    DocumentBuilder db;
    DOMImplementation domi;
    Document rankingDocument;
    Transformer transformer;

    ArrayList<Record> recordsList = new ArrayList<Record>();

    int randNum;

    int attempts;
    String nickname;
    int gameTime;
    Bitmap photoBitmap;
    String timeInfo;

    long startTime;
    long endTime;

    Toast toast;
    Context toastContext;
    CharSequence toastText;
    int toastDuration;

    AlertDialog.Builder adb;
    AlertDialog adRanking;

    EditText editTextNickname;

    public void restart() { // Restablecemos las variables correspondientes para volver a iniciar la partida.

        randNum = (int) (Math.random() * 100 + 1);
        randNum = 7;
        attempts = 0;
        startTime = System.currentTimeMillis(); // Guardamos la hora de inicio de la ronda.
        endTime = 0;
        nickname = "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toastContext = getApplicationContext();
        toastDuration = Toast.LENGTH_SHORT;

        final EditText editTextNumber = findViewById(R.id.editTextNumber);
        final Button buttonCheck = findViewById(R.id.buttonCheck);

        try {

            checkDir();
            checkXML();
            readRankingXML();

        } catch (IOException e) {

            e.printStackTrace();

        }

        setRankingDialog();
        restart();

        buttonCheck.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!editTextNumber.getText().toString().equalsIgnoreCase("")) {

                    int userNum = Integer.parseInt(editTextNumber.getText().toString());

                    if (userNum >= 1 && userNum <=100) {

                        if (userNum > randNum) {
                            attempts++;

                            toastText = "The number is smaller than " + userNum + ". You have made " + attempts + " attempts.";

                        } else if (userNum < randNum) {
                            attempts++;

                            toastText = "The number is bigger than " + userNum + ". You have made " + attempts + " attempts.";

                        } else {

                            endTime = System.currentTimeMillis(); // Al acertar el numero guardamos la hora de final de la ronda.

                            attempts++;

                            setGameTime();

                            toastText = "You guessed the number, it was " + randNum + ". You needed " + attempts + " attempts and " + timeInfo + " minutes/seconds.";

                            showRankingDialog(); // Mostramos el dialogo de ranking, para que el usuario pueda registrar su puntuacion.

                        }

                    } else {

                        toastText = "Introduce a number between 1 and 100, both included.";
                    }

                } else {

                    toastText = "Introduce a number.";
                }

                editTextNumber.setText("");

                toast = Toast.makeText(toastContext, toastText, toastDuration);
                toast.show();

            }
        });

    }

    public void checkDir() throws IOException {

        if (!filesDir.exists()) {

            filesDir.mkdir();

            photoDir.mkdir();

            return;

        }

        if (!photoDir.exists()) {

            photoDir.mkdir();

        }

    }

    public void checkXML() throws IOException {

        try {

            if (!rankingFile.exists()) {

                dbf = DocumentBuilderFactory.newInstance();
                db = dbf.newDocumentBuilder();
                domi = db.getDOMImplementation();

                rankingDocument = domi.createDocument(null, "ranking", null);
                rankingDocument.setXmlVersion("1.0");

                Source source = new DOMSource(rankingDocument);
                Result result = new StreamResult(rankingFile);

                transformer = TransformerFactory.newInstance().newTransformer();
                transformer.transform(source, result);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void readRankingXML() {

        try {

            rankingDocument = db.parse(rankingFile);

            NodeList recordsList = rankingDocument.getElementsByTagName("record");

            for (int i = 0; i < recordsList.getLength(); i++) {

                Node recordNode = recordsList.item(i);

                NodeList recordDataNodes = recordNode.getChildNodes();

                for (int j = 0; j < recordDataNodes.getLength(); j++) {

                    Node recordDataNode = recordDataNodes.item(j);

                    System.out.println("Propiedad: " + recordDataNode.getNodeName() + " Valor: " + recordDataNode.getTextContent());

                }
            }


        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void setGameTime() { // Comparamos la hora de inicio y final para determinar el tiempo que ha tardado en acertar el numero.

        gameTime = (int) (endTime - startTime) / 1000; // Dividimos entre 1000 porque la hora viene dada en ms.

        // Hacemos los calculos correspondientes para llevar la informacion a una String y poder mostrarsela al usuario visualmente.

        int seconds = gameTime % 60;
        int minutes = gameTime / 60;

        if (minutes < 10) {
            timeInfo = "0" + Integer.toString(minutes);
        } else {
            timeInfo = Integer.toString(minutes);
        }

        timeInfo += ":";

        if (seconds < 10) {
            timeInfo += "0" + Integer.toString(seconds);
        } else {
            timeInfo += Integer.toString(seconds);
        }

    }

    public void setRankingDialog() { // Establecemos el dialogo de final de partida.

        adb = new AlertDialog.Builder(this);

        adb.setTitle("Do you want to enter your score?");
        adb.setMessage("Introduce your nickname");

        editTextNickname = new EditText(this);

        adb.setView(editTextNickname);

        adb.setPositiveButton("Yes", null);
        adb.setNegativeButton("No", null);

        adRanking = adb.create();

        adRanking.setCancelable(false);
        adRanking.setCanceledOnTouchOutside(false);

    }

    public void showRankingDialog() {  // Mostramos el dialogo de final de partida.

        adRanking.show();

        Button positiveButton = adRanking.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!editTextNickname.getText().toString().equalsIgnoreCase("")) { // Si el usuario escribe un nombre, lo almacenamos, cerramos el dialogo, y llamamos al intent de la camara.

                    nickname = editTextNickname.getText().toString();

                    adRanking.dismiss();

                    takePhoto();

                } else { // Si el usuario no escribe ningun nombre, mostramos un toast informativo y mantenemos el dialogo abierto.

                    toastText = "Introduce a name.";

                    toast = Toast.makeText(toastContext, toastText, toastDuration);
                    toast.show();

                }

            }
        });

        Button negativeButton = adRanking.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { // Si el usuario no quiere guardar su puntuacion, hacemos un restart para que pueda volver a jugar y cerramos el dialogo.

                restart();

                adRanking.dismiss();

            }
        });

    }

    public void takePhoto() { // Hacemos un intent para sacar la foto.

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(takePictureIntent, 1);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) { // Si el resultado del intent es positivo, almacenamos el bitmap y llamamos a la funcion con el intent para abrir el ranking.

        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1 && resultCode == RESULT_OK) {

            Bundle extras = intent.getExtras();
            photoBitmap = (Bitmap) extras.get("data");

            savePhoto();

            recordsList.add(new Record(nickname, attempts, gameTime, photoBitmap, timeInfo));

            writeRankingXML();

            readRankingXML();

            openRanking();

        }

    }

    public void savePhoto() { // Guardamos la foto en el archivo correspondiente, segun el nombre del usuario.

        photoFile = new File(photoDir + "/" + nickname + photoExt);

        try {

            if (!photoFile.exists()) { // Si el archivo no existe, lo creamos.

                photoFile.createNewFile();

            }

            FileOutputStream fos = new FileOutputStream(photoFile);

            photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);


        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void writeRankingXML() {

        try {

            for (int i = 0; i < recordsList.size(); i++) {

                Element elementRecord = rankingDocument.createElement("record");

                Element elementNickname = rankingDocument.createElement("nickname");
                Text textNickname = rankingDocument.createTextNode(recordsList.get(i).getNickname());

                elementNickname.appendChild(textNickname);

                Element elementAttempts = rankingDocument.createElement("attempts");
                Text textAttempts = rankingDocument.createTextNode(String.valueOf(recordsList.get(i).getAttempts()));

                elementAttempts.appendChild(textAttempts);

                Element elementGameTime = rankingDocument.createElement("gametime");
                Text textGameTime = rankingDocument.createTextNode(String.valueOf(recordsList.get(i).getGameTime()));

                elementGameTime.appendChild(textGameTime);

                Element elementPhotoBitmap = rankingDocument.createElement("photobitmap");
                Text textPhotoBitmap = rankingDocument.createTextNode(photoDir + "/" + recordsList.get(i).getNickname() + photoExt);

                elementPhotoBitmap.appendChild(textPhotoBitmap);

                Element elementTimeInfo = rankingDocument.createElement("timeinfo");
                Text textTimeInfo = rankingDocument.createTextNode(recordsList.get(i).getTimeInfo());

                elementTimeInfo.appendChild(textTimeInfo);

                elementRecord.appendChild(elementNickname);
                elementRecord.appendChild(elementAttempts);
                elementRecord.appendChild(elementGameTime);
                elementRecord.appendChild(elementPhotoBitmap);
                elementRecord.appendChild(elementTimeInfo);

                rankingDocument.getDocumentElement().appendChild(elementRecord);

            }

            Source source = new DOMSource(rankingDocument);

            Result result = new StreamResult(rankingFile);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            transformer.transform(source, result);



        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void openRanking() { // Creamos un bundle en el que metemos toda la informacion necesaria para registrar en el ranking, y lanzamos el intent con el dentro.

        Intent intent = new Intent(MainActivity.this, RankingActivity.class);

        Bundle extras = new Bundle();

        extras.putString("NICKNAME", nickname);
        extras.putInt("ATTEMPTS", attempts);
        extras.putInt("GAME_TIME", gameTime);
        extras.putParcelable("PHOTO", photoBitmap);
        extras.putString("TIME_INFO", timeInfo);

        intent.putExtras(extras);

        startActivity(intent);
    }

}