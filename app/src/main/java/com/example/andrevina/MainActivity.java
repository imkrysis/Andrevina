package com.example.andrevina;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class MainActivity extends AppCompatActivity {

    final File filesDir = new File ("/data/user/0/com.example.andrevina/files");
    final File photoDir = new File ("/data/user/0/com.example.andrevina/files/photos");
    final File rankingFile = new File ("/data/user/0/com.example.andrevina/files/ranking.xml");
    final String photoExt = ".jpeg";

    File photoFile;

    DocumentBuilderFactory dbf;
    DocumentBuilder db;
    DOMImplementation domi;
    Document rankingDocument;
    Transformer transformer;

    ArrayList<Record> recordsList = new ArrayList<Record>();
    Record newRecord;

    int randNum;

    String nickname;
    int attempts;
    int gameTime;
    Bitmap photoBitmap;
    String timeInfo;

    long startTime;
    long endTime;

    CharSequence toastText;

    AlertDialog.Builder adb;
    AlertDialog adRanking;

    EditText editTextNickname;

    public void restart() { // Restablecemos las variables correspondientes para volver a iniciar la partida.

        randNum = (int) (Math.random() * 100 + 1);
        attempts = 0;
        startTime = System.currentTimeMillis(); // Guardamos la hora de inicio de la ronda.
        endTime = 0;
        nickname = "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText editTextNumber = findViewById(R.id.editTextNumber);
        final Button buttonCheck = findViewById(R.id.buttonCheck);
        final Button buttonRanking = findViewById(R.id.buttonRanking);

        try {

            checkDir();
            checkXML();
            readRankingXML();
            printRecordsList();

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

                            toastText = "El número es menor que " + userNum + ". Llevas " + attempts + " intentos.";

                        } else if (userNum < randNum) {
                            attempts++;

                            toastText = "El número es mayor que " + userNum + ". Llevas " + attempts + " intentos.";

                        } else {

                            endTime = System.currentTimeMillis(); // Al acertar el numero guardamos la hora de final de la ronda.

                            attempts++;

                            setGameTime();

                            toastText = "Adivinaste el número. Era " + randNum + ". Necesitaste " + attempts + " intentos y " + timeInfo + " minutos/segundos.";

                            showRankingDialog(); // Mostramos el dialogo de ranking, para que el usuario pueda registrar su puntuacion.

                        }

                    } else {

                        toastText = "Introduce un numero entre 1 y 100, ambos incluídos.";
                    }

                } else {

                    toastText = "Introduce un número...";
                }

                editTextNumber.setText("");

                Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();

            }
        });

        buttonRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openRanking();

            }
        });

    }

    public void checkDir() throws IOException { // Comprobamos si existen los directorios necesarios para el almacenamiento y si no existen los creamos.

        if (!filesDir.exists()) {

            filesDir.mkdir();

            photoDir.mkdir();

            return;

        }

        if (!photoDir.exists()) {

            photoDir.mkdir();

        }

    }

    public void checkXML() throws IOException { // Comprobamos si ya hay un archivo de ranking creado, en caso de no haberlo creamos uno y le añadimos la etiqueta root "ranking".

        try {

            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();

            if (!rankingFile.exists()) {

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

    public void readRankingXML() { // Parseamos el XML del ranking para leerlo y construir el ArrayList de records.

        recordsList.clear();

        try {

            rankingDocument = db.parse(rankingFile);

            NodeList recordsNodeList = rankingDocument.getElementsByTagName("record"); // Obtenemos una lista de todos los "record" almacenados.

            for (int i = 0; i < recordsNodeList.getLength(); i++) {

                Node recordNode = recordsNodeList.item(i);

                NodeList recordDataNodesList = recordNode.getChildNodes(); // Por cada "record" obtenemos sus hijos.

                for (int j = 0; j < recordDataNodesList.getLength(); j++) {

                    Node recordDataNode = recordDataNodesList.item(j);

                    switch (recordDataNode.getNodeName()) { // Hacemos un switch para manejar cada caso de los posibles (cada elemento).

                        case "nickname":

                            nickname = recordDataNode.getTextContent();
                            break;

                        case "attempts":

                            attempts = Integer.valueOf(recordDataNode.getTextContent());
                            break;

                        case "gametime":

                            gameTime = Integer.valueOf(recordDataNode.getTextContent());
                            break;

                        case "photobitmap":

                            photoFile = new File(recordDataNode.getTextContent());

                            photoBitmap = BitmapFactory.decodeStream(new FileInputStream(photoFile));
                            break;

                        case "timeinfo":

                            timeInfo = recordDataNode.getTextContent();
                            break;

                    }

                }

                recordsList.add(new Record(nickname, attempts, gameTime, photoBitmap, timeInfo)); // Despues de obtener la informacion, creamos el objeto y lo añadimos al ArrayList.

            }


        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void printRecordsList() { // Esta funcion es simplemente para facilitar el saber que esta pasando en el programa leyendo la consola.


        for (int i = 0; i < recordsList.size(); i++) {

            System.out.println("Record " + (i+1));
            System.out.println("Nickname: " + recordsList.get(i).getNickname());
            System.out.println("Attempts: " + recordsList.get(i).getAttempts());
            System.out.println("GameTime: " + recordsList.get(i).getGameTime());
            System.out.println("PhotoURI: " + recordsList.get(i).getPhotoBitmap().toString());
            System.out.println("TimeInfo: " + recordsList.get(i).getTimeInfo());

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

        adb.setTitle("¿Quieres guardar tu puntuación?");
        adb.setMessage("Introduce tu nickname.");

        editTextNickname = new EditText(this);

        adb.setView(editTextNickname);

        adb.setPositiveButton("Sí", null);
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

                    editTextNickname.setText("");

                    adRanking.dismiss();

                    takePhoto();

                } else { // Si el usuario no escribe ningun nombre, mostramos un toast informativo y mantenemos el dialogo abierto.

                    Toast.makeText(getApplicationContext(), "Introduce un nombre...", Toast.LENGTH_SHORT).show();

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

            newRecord = new Record(nickname, attempts, gameTime, photoBitmap, timeInfo); // Creamos un objeto Record auxiliar con los datos del nuevo registro, utilizaremos este objeto para determinar su posicion correcta.

            recordsList.add(getRightIndex(), newRecord); // Metemos el nuevo registro en la posicion correspondiente para mantener siempre la lista ordenada.

            writeRankingXML();

            restart();

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

    public int getRightIndex() { // Utilizamos esta funcion para obtener el indice de la posicion final correspondiente al nuevo record en el ArrayList, de forma que la lista quede ordenada.

        int infLimit = 0;
        int supLimit = recordsList.size() - 1;
        int indexSearch;

        int infRecordCompare;
        int midRecordCompare;
        int supRecordCompare;

        while (infLimit <= supLimit) {

            indexSearch = (infLimit + supLimit) / 2;

            if (indexSearch == 0 || indexSearch == recordsList.size()-1) { // Si el indice a comparar es uno de los 2 limites, significa que el nuevo Record ha superado los demas filtros y debe ser esta su posicion.

                return indexSearch;

            }

            midRecordCompare = newRecordCompare(indexSearch); // Comparamos el nuevo record con el correspondiente del ArrayList.

            if (midRecordCompare == 0) { // Si son iguales cogemos el indice y le añadimos uno para colocarlo justo despues.

                return indexSearch++;

            } else { // Si no hemos encontrado el indice, necesitaremos comparar el nuevo Record con el siguiente y anterior, para saber en que mitad de la ArrayList hemos de colocarlo.

                infRecordCompare = newRecordCompare(indexSearch-1);
                supRecordCompare = newRecordCompare(indexSearch+1);

            }

            if (infRecordCompare >= 0 && supRecordCompare <= 0) { // Si la comparacion con el Record anterior es superior o igual a 0, y la comparacion con el Record siguiente es inferior o igual a 0, significa que el nuevo Record debe estar entre los 2.

                return indexSearch;

            } else if (infRecordCompare > 0) { // Si la comparacion con el Record anterior es superior a 0, significa que debemos buscar en la ultima mitad del ArrayList.

                infLimit = indexSearch++;

            } else if (supRecordCompare < 0) { // Si la comparacion con el Record siguiente es inferior a 0, significa que debemos buscar en la primera mitad del ArrayList.

                supLimit = indexSearch--;

            }

        }

        return 0;

    }

    public int newRecordCompare(int indexSearch) { // Con esta funcion comparamos el ultimo Record con un Record de la lista. Nos devuelve < 0 si deberia estar posicionado antes y > 0 si deberia estar posicionado despues.

        return newRecord.compareRecords(recordsList.get(indexSearch));

    }

    public void writeRankingXML() { // Reescribimos el XML ya ordenado, de forma que solo tengamos que definir la posicion del ultimo record introducido, en lugar de reordenar la lista cada vez que la generemos.

        rankingFile.delete();

        domi = db.getDOMImplementation();

        rankingDocument = domi.createDocument(null, "ranking", null); // Generamos la etiqueta root "ranking"
        rankingDocument.setXmlVersion("1.0");

        try {

            for (int i = 0; i < recordsList.size(); i++) { // Por cada objeto "Record", creamos los elementos con sus valores correspondientes.

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

            transformer.transform(source, result); // Creamos el XML ya actualizado.

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void openRanking() { // Comprobamos si hay alguna puntuacion guardada, en ese caso iniciamos la RankingActivity. Si no, notificamos al usuario.

        if (recordsList.size() > 0) {

            Intent intent = new Intent(MainActivity.this, RankingActivity.class);

            startActivity(intent);

        } else {

            Toast.makeText(getApplicationContext(), "De momento no hay puntuaciones guardadas, prueba a guardar alguna.", Toast.LENGTH_LONG).show();

        }

    }

}