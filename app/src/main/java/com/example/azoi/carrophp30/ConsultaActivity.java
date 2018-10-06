package com.example.azoi.carrophp30;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConsultaActivity extends Activity {
    private ListView lista;
    ProgressDialog PD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);
        lista = (ListView) findViewById(R.id.listView);

        PD = new ProgressDialog(this);
        PD.setMessage("Carregando lista de carros .....");
        PD.setCancelable(true);
        PD.show();

        carregarDados();
    }

    MatrixCursor matrixCursor = null;

    public void carregarDados() {
        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, BancoDeDadosPHP.urlListar, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int success = response.getInt("success");

                    if (success == 1) {
                        JSONArray ja = response.getJSONArray(BancoDeDadosPHP.TABELA);

                        String[] columnNames = {"_id", BancoDeDadosPHP.ID, BancoDeDadosPHP.MODELO, BancoDeDadosPHP.ANO, BancoDeDadosPHP.PLACA,BancoDeDadosPHP.KM,BancoDeDadosPHP.VALOR};
                        matrixCursor = new MatrixCursor(columnNames);

                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject jobj = ja.getJSONObject(i);
                            matrixCursor.addRow(new String[]{String.valueOf(i), jobj.getString(BancoDeDadosPHP.ID), jobj.getString(BancoDeDadosPHP.MODELO), jobj.getString(BancoDeDadosPHP.ANO), jobj.getString(BancoDeDadosPHP.PLACA)});
                        }

                        String[] nomeCampos = new String[]{BancoDeDadosPHP.ID, BancoDeDadosPHP.MODELO};
                        int[] idViews = new int[]{R.id.idLivro, R.id.nomeLivro};

                        SimpleCursorAdapter adaptador = new SimpleCursorAdapter(getBaseContext(), R.layout.linha_da_lista, matrixCursor, nomeCampos, idViews, 0);
                        lista.setAdapter(adaptador);

                        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                matrixCursor.moveToPosition(position);
                                String sID = matrixCursor.getString(matrixCursor.getColumnIndexOrThrow(BancoDeDadosPHP.ID));
                                String sAutor = matrixCursor.getString(matrixCursor.getColumnIndexOrThrow(BancoDeDadosPHP.ANO));
                                String sEditora = matrixCursor.getString(matrixCursor.getColumnIndexOrThrow(BancoDeDadosPHP.PLACA));
                                String sTitulo = matrixCursor.getString(matrixCursor.getColumnIndexOrThrow(BancoDeDadosPHP.MODELO));
                                String sKm = matrixCursor.getString(matrixCursor.getColumnIndexOrThrow(BancoDeDadosPHP.KM));
                                String sValor = matrixCursor.getString(matrixCursor.getColumnIndexOrThrow(BancoDeDadosPHP.VALOR));

                                Intent intent = new Intent(ConsultaActivity.this, CadastroActivity.class);
                                intent.putExtra(BancoDeDadosPHP.ID, sID);
                                intent.putExtra(BancoDeDadosPHP.ANO, sAutor);
                                intent.putExtra(BancoDeDadosPHP.PLACA, sEditora);
                                intent.putExtra(BancoDeDadosPHP.MODELO, sTitulo);
                                intent.putExtra(BancoDeDadosPHP.KM, sKm);
                                intent.putExtra(BancoDeDadosPHP.VALOR, sValor);

                                System.out.println("sID enviado " + sID);
                                System.out.println("sAutor enviado " + sAutor);
                                System.out.println("sEditora enviado " + sEditora);
                                System.out.println("sTitulo enviado " + sTitulo);
                                System.out.println("sTitulo enviado " + sKm);
                                System.out.println("sTitulo enviado " + sValor);

                                startActivity(intent);
                                finish();
                            }
                        });

                    }
                    else{
                        TextView textViewMensagem = (TextView) findViewById(R.id.textViewMensagem);
                        textViewMensagem.setText("Nenum registro encontrado");
                    }

                    PD.dismiss();

                } catch (JSONException e) {
                    PD.dismiss();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("onErrorResponse: " + error.getMessage() + " " + error.toString());
                error.printStackTrace();
                PD.dismiss();
            }
        });

        MyApplication.getInstance().addToReqQueue(jreq);
    }

}