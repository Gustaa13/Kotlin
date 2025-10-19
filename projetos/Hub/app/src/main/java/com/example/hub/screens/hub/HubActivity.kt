package com.example.hub.screens.hub

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.hub.R
import com.example.hub.databinding.ActivityHubBinding
import com.example.hub.screens.calculator.CalculatorActivity
import com.example.hub.screens.marketPlanning.MarketPlanningActivity
import com.example.hub.screens.scoreBoard.ScoreBoardActivity
import com.example.hub.utils.LogHelper

class HubActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHubBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        LogHelper.i("HubActivity iniciada com sucesso")

        try {
            val apps = listOf(
                AppItem("Placar de Basquete", "Abra o placar", R.drawable.ic_scoreboard) {
                    LogHelper.i("Abrindo ScoreBoardActivity")
                    startActivity(Intent(this, ScoreBoardActivity::class.java))
                },
                AppItem("Calculadora", "Faça contas", R.drawable.ic_calculator) {
                    LogHelper.i("Abrindo CalculatorActivity")
                    startActivity(Intent(this, CalculatorActivity::class.java))
                },
                AppItem("Planejamento de compras", "Liste suas compras", R.drawable.ic_marketlist) {
                    LogHelper.i("Abrindo MarketPlanningActivity")
                    startActivity(Intent(this, MarketPlanningActivity::class.java))
                }
            )

            val adapter = AppCardAdapter(apps)
            binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
            binding.recyclerView.adapter = adapter

            LogHelper.d("Lista de apps carregada no RecyclerView")
        } catch (e: Exception) {
            LogHelper.e("Erro ao inicializar HubActivity", e)
        }
    }
}
