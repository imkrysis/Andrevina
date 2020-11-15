package com.example.andrevina;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.net.HttpCookie;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class RankingActivity extends AppCompatActivity {

    final File filesDir = new File ("/data/user/0/com.example.andrevina/files");
    final File photoDir = new File ("/data/user/0/com.example.andrevina/files/photos");
    final File rankingFile = new File ("/data/user/0/com.example.andrevina/files/ranking.xml");
    final String photoExt = ".jpeg";

    DocumentBuilderFactory dbf;
    DocumentBuilder db;
    Document rankingDocument;

    ArrayList<Record> recordsList = new ArrayList<Record>();

    String nickname;
    int attempts;
    int gameTime;
    Bitmap photo;
    String timeInfo;

    RecyclerView recyclerViewRecords;
    RecyclerViewAdapter rvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        Intent intent = getIntent();

        try {

            prepareReader();
            readRankingXML();
            buildRecyclerView();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void buildRecyclerView() {

        recyclerViewRecords = findViewById(R.id.recyclerViewRecords);

        recyclerViewRecords.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        rvAdapter = new RecyclerViewAdapter(recordsList);

        recyclerViewRecords.setAdapter(rvAdapter);

    }

    public void prepareReader() {

        try {

            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();

        } catch (Exception e) {

            e.printStackTrace();

        }


    }

    public void readRankingXML() { // Parseamos el XML del ranking para leerlo y construir el ArrayList de records.

        File photoFile;

        Bitmap photoBitmap = null;

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

                recordsList.add(new Record(nickname, attempts, gameTime, photoBitmap, timeInfo));

            }


        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void returnToMain(View v) {

        Intent intent = new Intent(RankingActivity.this, MainActivity.class);

        startActivity(intent);

    }
}