package com.ifsul.jogodavelha2;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DEBUG";
    private static final String PLAYER_SYMBOL = "X";
    private static final String JARVIS_SYMBOL = "O";
    private static final String CLEAR = "";

    // contador de jogadas
    private static int jogadas;
    // contador de vitorias
    private static int vitorias;
    // contador de derrotas
    private static int derrotas;
    // contador de empates
    private static int empates;
    // contador de jogos
    private static int jogos;


    private Button[][] campos = new Button[3][3];
    private TextView textViewVitorias;
    private TextView textViewDerrotas;
    private TextView textViewEmpates;
    private TextView textViewJogos;
    private TextView textViewJogadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init components
        jogadas = 0;
        jogos = 1;
        vitorias = 0;
        derrotas = 0;
        empates = 0;

        textViewDerrotas   = findViewById(R.id.text_view_total_derrotas);
        textViewEmpates    = findViewById(R.id.text_view_total_empates);
        textViewVitorias   = findViewById(R.id.text_view_total_vitorias);
        textViewJogadas    = findViewById(R.id.text_view_jogadas);
        textViewJogos      = findViewById(R.id.text_view_total_jogos);
        updateGameStats();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "campos_" + i + "_" + j;
                int id = getResources().getIdentifier(buttonID, "id", getPackageName());
                campos[i][j] = findViewById(id);
                campos[i][j].setOnClickListener(this);
            }
        }

        //set reset listener
        (findViewById(R.id.button_reset)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jogos++;
                jogadas =0;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        campos[i][j].setText(CLEAR);
                        campos[i][j].setEnabled(true);
                    }
                }
                // aleatoriamente decide quem começa
                if (new Random().nextBoolean()) {
                    randomJarvisMove();
                    Toast.makeText(getApplicationContext(), R.string.jarvis_comeca, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.voce_comeca, Toast.LENGTH_SHORT).show();
                }
                updateGameStats();
            }
        });

        // aleatoriamente decide quem começa
        if (new Random().nextBoolean()) {
            randomJarvisMove();
            Toast.makeText(this, R.string.jarvis_comeca, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.voce_comeca, Toast.LENGTH_SHORT).show();
        }
        updateGameStats();
    }

    private void updateGameStats() {
        textViewDerrotas.setText("" + derrotas);
        textViewEmpates.setText("" + empates);
        textViewVitorias.setText("" + vitorias);
        textViewJogadas.setText("" + jogadas);
        textViewJogos.setText("" + jogos);
    }

    /**
     * seta o simbolo passado como texto do botão passado
     *
     * @param button Botao a ser setado
     * @param symbol simbolo a ser setado
     */
    private void setButton(Button button, String symbol) {
        button.setText(symbol);
        button.setEnabled(false);
    }

    private void enableAllButtons(boolean b) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                campos[i][j].setEnabled(b);
            }
        }
    }

    @Override
    public void onClick(View view) {
        // jogador
        setButton((Button) view, PLAYER_SYMBOL);
        jogadas++;
        updateGameStats();

        //verifica se o jogador ganhou
        if (checkWinner(PLAYER_SYMBOL)) {
            enableAllButtons(false);
            Snackbar.make(findViewById(R.id.root_parent_layout), R.string.jogador_ganhou,
                    Snackbar.LENGTH_LONG).show();
            vitorias++;
            updateGameStats();
            return;
        }
        //verifica se houve empate
        if (checkTie()) {
            Snackbar.make(findViewById(R.id.root_parent_layout), "Deu velha",
                    Snackbar.LENGTH_LONG).show();
            empates++;
            updateGameStats();
            return;
        }



        //jarvis
        MoveIndexes attackMove = bestAttackMove();
        MoveIndexes defenseMove = bestDefenseMove();
        if (attackMove.isValid()) {
            setButton(campos[attackMove.getRow()][attackMove.getCol()], JARVIS_SYMBOL);
        } else if (defenseMove.isValid()) {
            setButton(campos[defenseMove.getRow()][defenseMove.getCol()], JARVIS_SYMBOL);
        } else {
            // jogada aleatoria com verificação repetição caso campos estejam preenchidos
            randomJarvisMove();


        }
        jogadas++;
        updateGameStats();

        // verifica se o Jarvis ganhou
        if (checkWinner(JARVIS_SYMBOL)) {
            enableAllButtons(false);
            Snackbar.make(findViewById(R.id.root_parent_layout), getString(R.string.Jarvis_ganhou),
                    Snackbar.LENGTH_LONG).show();
            derrotas++;
            updateGameStats();
            return;
        }
        // verifica se houve empate
        if (checkTie()) {
            Snackbar.make(findViewById(R.id.root_parent_layout), R.string.velha,
                    Snackbar.LENGTH_LONG).show();
            empates++;
            updateGameStats();
            return;
        }

    }


    private void randomJarvisMove() {

        int row;
        int col;
        boolean foundFreePosition = false;
        do {
            Random r = new Random();
            row = r.nextInt(3);
            col = r.nextInt(3);

            if (campos[row][col].isEnabled()) foundFreePosition = true;

        } while (!foundFreePosition);

        setButton(campos[row][col], JARVIS_SYMBOL);

    }

    /**
     *
     * @return true se houve empate
     */
    private boolean checkTie() {
        boolean tie = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (campos[i][j].isEnabled()) {
                    tie = false;
                    break;
                }
            }
        }

        return tie;
    }

    /**
     *
     * @param symbol    simbolo a ser passado
     * @return          true caso o simbolo passado tenha fechado uma linha
     */
    private boolean checkWinner(String symbol) {
        // verificação de vencedor

        for (int i = 0; i < 3; i++) {
            // verificação das linhas
            if (campoEquals(campos[i][0], symbol) && campoEquals(campos[i][1], symbol) &&
                    campoEquals(campos[i][2], symbol)) return true;
            // verificação das colunas
            if (campoEquals(campos[0][i], symbol) && campoEquals(campos[1][i], symbol) &&
                    campoEquals(campos[2][i], symbol)) return true;


        }

        // verificação das diagonais
        if (campoEquals(campos[0][0], symbol) && campoEquals(campos[1][1], symbol) &&
                campoEquals(campos[2][2], symbol)) return true;

        if (campoEquals(campos[0][2], symbol) && campoEquals(campos[1][1], symbol) &&
                campoEquals(campos[2][0], symbol)) return true;


        return false;
    }

    /**
     * @return Objeto MoveIndexes com o melhor movimento de ataque
     */
    private MoveIndexes bestAttackMove() {
        return bestMove(JARVIS_SYMBOL);
    }

    /**
     * @return Objeto MoveIndexes com o melhor movimento de defesa
     */
    private MoveIndexes bestDefenseMove() {
        return bestMove(PLAYER_SYMBOL);
    }

    /**
     * @param symbol simbolo
     * @return melhor movimento de a se fazer levando em consideração somente o simbolo que foi
     * passado
     */
    private MoveIndexes bestMove(String symbol) {
        // verificar onde há campos que são os ultimos para fechar uma linha

        //verificar linhas
        for (int i = 0; i < 3; i++) {

            if (campoEquals(campos[i][0], symbol) && campoEquals(campos[i][1], symbol) &&
                    !campoIsSet(campos[i][2])) return new MoveIndexes(i, 2);

            if (campoEquals(campos[i][0], symbol) && campoEquals(campos[i][2], symbol) &&
                    !campoIsSet(campos[i][1])) return new MoveIndexes(i, 1);

            if (campoEquals(campos[i][1], symbol) && campoEquals(campos[i][2], symbol) &&
                    !campoIsSet(campos[i][0])) return new MoveIndexes(i, 0);

        }
        // verificar colunas
        for (int i = 0; i < 3; i++) {

            if (campoEquals(campos[0][i], symbol) && campoEquals(campos[1][i], symbol) &&
                    !campoIsSet(campos[2][i])) return new MoveIndexes(2, i);

            if (campoEquals(campos[0][i], symbol) && campoEquals(campos[2][i], symbol) &&
                    !campoIsSet(campos[1][i])) return new MoveIndexes(1, i);

            if (campoEquals(campos[1][i], symbol) && campoEquals(campos[2][i], symbol) &&
                    !campoIsSet(campos[0][i])) return new MoveIndexes(0, i);
        }

        // verificar diagonais
        if (campoEquals(campos[0][0], symbol) && campoEquals(campos[1][1], symbol) &&
                !campoIsSet(campos[2][2])) return new MoveIndexes(2, 2);

        if (campoEquals(campos[0][0], symbol) && campoEquals(campos[2][2], symbol) &&
                !campoIsSet(campos[1][1])) return new MoveIndexes(1, 1);

        if (campoEquals(campos[1][1], symbol) && campoEquals(campos[2][2], symbol) &&
                !campoIsSet(campos[0][0])) return new MoveIndexes(0, 0);


        if (campoEquals(campos[0][2], symbol) && campoEquals(campos[1][1], symbol) &&
                !campoIsSet(campos[2][0])) return new MoveIndexes(2, 0);

        if (campoEquals(campos[0][2], symbol) && campoEquals(campos[2][0], symbol) &&
                !campoIsSet(campos[1][1])) return new MoveIndexes(1, 1);

        if (campoEquals(campos[2][0], symbol) && campoEquals(campos[1][1], symbol) &&
                !campoIsSet(campos[0][2])) return new MoveIndexes(0, 2);


        return new MoveIndexes(-1, -1);
    }

    /**
     * @param button botao a ser verificado
     * @param symbol simbolo a ser verificado
     * @return true caso o o botão passado esteja marcado com o simbolo passado
     */
    private boolean campoEquals(Button button, String symbol) {
        return button.getText().toString().equals(symbol);
    }

    /**
     * @param button botao a ser verificado
     * @return true caso o botão tenha algum texto escrito
     */
    private boolean campoIsSet(Button button) {
        return button.getText().length() > 0;
    }
}
