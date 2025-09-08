package com.example.firtsproject

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    private var pontuacaoTimeA: Int = 0
    private var pontuacaoTimeB: Int = 0

    private lateinit var pTimeA: TextView
    private lateinit var pTimeB: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_main)

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
    }

    fun adicionarPontos(pontos: Int, time: String) {
        if(time == "A") {
            pontuacaoTimeA += pontos
        } else {
            pontuacaoTimeB += pontos
        }
        atualizarPlacar(time)
    }

    private fun animarPlacar(view: TextView, novoValor: Int) {
        // Anima metade da rotação
        view.animate()
            .rotationX(90f) // gira até metade
            .setDuration(150) // velocidade
            .withEndAction {
                // Atualiza o texto no meio da animação
                view.text = novoValor.toString()

                // Continua a rotação até 0 (completa o flip)
                view.rotationX = -90f
                view.animate()
                    .rotationX(0f)
                    .setDuration(150)
                    .start()
            }.start()
    }

    fun atualizarPlacar(time: String){
        if (time == "A") {
            animarPlacar(pTimeA, pontuacaoTimeA)
        } else {
            animarPlacar(pTimeB, pontuacaoTimeB)
        }
    }

    fun reiniciarPartida() {
        pontuacaoTimeA = 0
        pTimeA.setText(pontuacaoTimeA.toString())
        pontuacaoTimeB = 0
        pTimeB.setText(pontuacaoTimeB.toString())
        Toast.makeText(this,"Placar reiniciado",Toast.LENGTH_SHORT).show()
    }
}