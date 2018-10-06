package com.example.azoi.carrophp30;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CadastroActivity extends Activity {

    EditText editTexTitulo;
    EditText editTextAutor;
    EditText editTextEditora;
    EditText editTextKm;
    EditText editTextValor;

    String sID;
    String sAutor;
    String sEditora;
    String sTitulo;
    String sKm;
    String sValor;

    ProgressDialog PD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        Button buttonCadastrar = (Button)findViewById(R.id.buttonCadastrar);
        Button buttonAlterar = (Button)findViewById(R.id.buttonAlterar);
        Button buttonDeletar = (Button)findViewById(R.id.buttonDeletar);

        editTexTitulo = (EditText) findViewById(R.id.editTexTitulo);
        editTextAutor = (EditText) findViewById((R.id.editTextAutor));
        editTextEditora = (EditText) findViewById(R.id.editTextEditora);
        editTextKm = (EditText) findViewById(R.id.editTextKm);
        editTextValor = (EditText) findViewById(R.id.editTextValor);

        PD = new ProgressDialog(this);
        PD.setCancelable(true);

        sID = this.getIntent().getStringExtra(BancoDeDadosPHP.ID);
        sAutor = this.getIntent().getStringExtra(BancoDeDadosPHP.MODELO);
        sEditora = this.getIntent().getStringExtra(BancoDeDadosPHP.ANO);
        sTitulo = this.getIntent().getStringExtra(BancoDeDadosPHP.PLACA);
        sKm = this.getIntent().getStringExtra(BancoDeDadosPHP.KM);
        sValor = this.getIntent().getStringExtra(BancoDeDadosPHP.VALOR);

        if (sID == null){
            //está abrindo a tela direto do menu principal para cadastro
            buttonCadastrar.setEnabled(true);
            buttonAlterar.setEnabled(false);
            buttonDeletar.setEnabled(false);

        }else {
            //está abrindo a tela depois da lista, então vem com os dados por parâmetro
            editTexTitulo.setText(sTitulo);
            editTextAutor.setText(sAutor);
            editTextEditora.setText(sEditora);
            editTextKm.setText(sKm);
            editTextValor.setText(sValor);

            //ajusta botões
            buttonCadastrar.setEnabled(false);
            buttonAlterar.setEnabled(true);
            buttonDeletar.setEnabled(true);
        }

        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inserir();
            }
        });

        buttonDeletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletar();
            }
        });

        buttonAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alterar();
            }
        });
    }

//--------------------- Métodos de conexão com servidor ---------------------------------------

    public void alterar() {
        PD.setMessage("Alterando registro .....");
        PD.show();
        sTitulo = editTexTitulo.getText().toString();
        sAutor = editTextAutor.getText().toString();
        sEditora = editTextEditora.getText().toString();
        sKm = editTextKm.getText().toString();
        sValor = editTextValor.getText().toString();

        Map<String, String> params = new HashMap<String, String>();
        params.put(BancoDeDadosPHP.ID, sID);
        params.put(BancoDeDadosPHP.MODELO, sTitulo);
        params.put(BancoDeDadosPHP.ANO, sAutor);
        params.put(BancoDeDadosPHP.PLACA, sEditora);
        params.put(BancoDeDadosPHP.KM, sKm);
        params.put(BancoDeDadosPHP.VALOR, sValor);

        CustomRequest update_request = new CustomRequest(Request.Method.POST, BancoDeDadosPHP.urlAlterar, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    System.out.println("JSON recebido: " + response.toString());
                    int success = response.getInt("success");

                    Toast.makeText(getApplicationContext(), response.getString("mensagem"), Toast.LENGTH_SHORT).show();

                    if (success == 1) {
                        Intent intent = new Intent(CadastroActivity.this, ConsultaActivity.class);
                        startActivity(intent);
                        finish();
                    } else
                        System.out.println("Erro na alteração. SQL executado: " + response.getString("sql"));
                    PD.dismiss();
                } catch (Exception e) {
                    System.out.println("Exception ");
                    e.printStackTrace();
                    PD.dismiss();
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

        MyApplication.getInstance().addToReqQueue(update_request);
    }


    public void deletar() {
        PD.setMessage("Deletando registro .....");
        PD.show();
        String delete_url = BancoDeDadosPHP.urlDeletar + sID;

        JsonObjectRequest delete_request = new JsonObjectRequest(delete_url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            System.out.println("JSON recebido: " + response.toString());
                            int success = response.getInt("success");

                            Toast.makeText(getApplicationContext(),response.getString("mensagem"),Toast.LENGTH_SHORT).show();

                            if (success == 1) {
                                Intent intent = new Intent(CadastroActivity.this, ConsultaActivity.class);
                                startActivity(intent);
                                finish();
                            } else
                                System.out.println("Erro na deleção. SQL executado: " + response.getString("sql"));

                            PD.dismiss();

                        } catch (Exception e) {
                            System.out.println("Exception ");
                            e.printStackTrace();
                            PD.dismiss();
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

        MyApplication.getInstance().addToReqQueue(delete_request);

    }


    public void inserir() {
        PD.setMessage("Inserindo registro .....");
        PD.show();
        sTitulo = editTexTitulo.getText().toString();
        sAutor = editTextAutor.getText().toString();
        sEditora = editTextEditora.getText().toString();
        sKm = editTextKm.getText().toString();
        sValor = editTextValor.getText().toString();
        System.out.println(sTitulo);
        System.out.println(sAutor);
        System.out.println(sEditora);
        System.out.println(sKm);
        System.out.println(sValor);

        Map<String, String> params = new HashMap<String, String>();
        params.put(BancoDeDadosPHP.ID, sID);
        params.put(BancoDeDadosPHP.MODELO, sTitulo);
        params.put(BancoDeDadosPHP.ANO, sAutor);
        params.put(BancoDeDadosPHP.PLACA, sEditora);
        params.put(BancoDeDadosPHP.KM, sKm);
        params.put(BancoDeDadosPHP.VALOR, sValor);

        CustomRequest update_request = new CustomRequest(Request.Method.POST, BancoDeDadosPHP.urlInserir, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                System.out.println("foi ");
                try{
                    System.out.println("JSON recebido: " + response.toString());
                    int success = response.getInt("success");

                    Toast.makeText(getApplicationContext(), response.getString("mensagem"), Toast.LENGTH_SHORT).show();

                    if (success == 1){
                        editTexTitulo.setText("");
                        editTextAutor.setText("");
                        editTextEditora.setText("");
                        editTextKm.setText("");
                        editTextValor.setText("");
                    }else
                        System.out.println("Erro na inserção. SQL executado: ");
                    PD.dismiss();
                }
                catch (Exception e){
                    System.out.println("Exception ");
                    e.printStackTrace();
                    PD.dismiss();
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

        MyApplication.getInstance().addToReqQueue(update_request);
    }
}
