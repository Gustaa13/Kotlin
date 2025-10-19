package com.example.hub.screens.scoreBoard

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.hub.R
import com.example.hub.utils.LogHelper

class ScoreBoardActivity : ComponentActivity() {

    private var pontuacaoTimeA: Int = 0
    private var pontuacaoTimeB: Int = 0

    private lateinit var pTimeA: TextView
    private lateinit var pTimeB: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)

        LogHelper.i("ScoreBoardActivity iniciada")

        try {
            pTimeA = findViewById(R.id.placarTimeA)
            pTimeB = findViewById(R.id.placarTimeB)

            val bTresPontosTimeA: Button = findViewById(R.id.tresPontosA)
            val bDoisPontosTimeA: Button = findViewById(R.id.doisPontosA)
            val bTLivreTimeA: Button = findViewById(R.id.tiroLivreA)
            val bTresPontosTimeB: Button = findViewById(R.id.tresPontosB)
            val bDoisPontosTimeB: Button = findViewById(R.id.doisPontosB)
            val bTLivreTimeB: Button = findViewById(R.id.tiroLivreB)
            val bReiniciar: Button = findViewById(R.id.reiniciarPartida)

            bTresPontosTimeA.setOnClickListener { adicionarPontos(3, "A") }
            bDoisPontosTimeA.setOnClickListener { adicionarPontos(2, "A") }
            bTLivreTimeA.setOnClickListener { adicionarPontos(1, "A") }

            bTresPontosTimeB.setOnClickListener { adicionarPontos(3, "B") }
            bDoisPontosTimeB.setOnClickListener { adicionarPontos(2, "B") }
            bTLivreTimeB.setOnClickListener { adicionarPontos(1, "B") }

            bReiniciar.setOnClickListener { reiniciarPartida() }

            findViewById<Button>(R.id.btnVoltar).setOnClickListener {
                LogHelper.i("Usuário clicou em 'Voltar' – encerrando ScoreBoardActivity")
                finish()
            }

            LogHelper.d("Layout e botões inicializados com sucesso")
        } catch (e: Exception) {
            LogHelper.e("Erro ao inicializar ScoreBoardActivity", e)
        }
    }

    fun adicionarPontos(pontos: Int, time: String) {
        LogHelper.v("adicionarPontos chamado: time=$time, pontos=$pontos")

        try {
            if(time == "A") {
                pontuacaoTimeA += pontos
                LogHelper.d("Time A: nova pontuação = $pontuacaoTimeA")
            } else {
                pontuacaoTimeB += pontos
                LogHelper.d("Time B: nova pontuação = $pontuacaoTimeB")
            }
            atualizarPlacar(time)
        } catch (e: Exception) {
            LogHelper.e("Erro ao adicionar pontos: ${e.message}", e)
        }
    }

    private fun animarPlacar(view: TextView, novoValor: Int) {
        LogHelper.v("Animação de placar iniciada para valor: $novoValor")

        try {
            view.animate()
                .rotationX(90f)
                .setDuration(150)
                .withEndAction {
                    view.text = novoValor.toString()
                    view.rotationX = -90f
                    view.animate()
                        .rotationX(0f)
                        .setDuration(150)
                        .start()
                }.start()
        } catch (e: Exception) {
            LogHelper.w("Falha ao animar placar: ${e.message}")
        }
    }

    fun atualizarPlacar(time: String) {
        LogHelper.v("Atualizando placar para o time $time")

        try {
            if (time == "A") {
                animarPlacar(pTimeA, pontuacaoTimeA)
            } else {
                animarPlacar(pTimeB, pontuacaoTimeB)
            }
        } catch (e: Exception) {
            LogHelper.e("Erro ao atualizar placar: ${e.message}", e)
        }
    }

    fun reiniciarPartida() {
        LogHelper.i("Reiniciando partida e zerando placares")
        try {
            pontuacaoTimeA = 0
            pTimeA.setText(pontuacaoTimeA.toString())
            pontuacaoTimeB = 0
            pTimeB.setText(pontuacaoTimeB.toString())
            Toast.makeText(this,"Placar reiniciado",Toast.LENGTH_SHORT).show()
            LogHelper.d("Placar reiniciado com sucesso")
        } catch (e: Exception) {
            LogHelper.e("Erro ao reiniciar partida: ${e.message}", e)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("pontuacaoA", pontuacaoTimeA)
        outState.putInt("pontuacaoB", pontuacaoTimeB)
        LogHelper.d("Estado salvo: A=$pontuacaoTimeA, B=$pontuacaoTimeB")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        pontuacaoTimeA = savedInstanceState.getInt("pontuacaoA", 0)
        pontuacaoTimeB = savedInstanceState.getInt("pontuacaoB", 0)
        atualizarPlacar("A")
        atualizarPlacar("B")
        LogHelper.d("Estado restaurado: A=$pontuacaoTimeA, B=$pontuacaoTimeB")
    }
}