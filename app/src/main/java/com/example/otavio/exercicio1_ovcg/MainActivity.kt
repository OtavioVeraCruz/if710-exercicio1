package com.example.otavio.exercicio1_ovcg

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_0.setOnClickListener{ text_calc.text = text_calc.text.append("0") }
        btn_1.setOnClickListener{ text_calc.text = text_calc.text.append("1") }
        btn_2.setOnClickListener{ text_calc.text = text_calc.text.append("2") }
        btn_3.setOnClickListener{ text_calc.text = text_calc.text.append("3") }
        btn_4.setOnClickListener{ text_calc.text = text_calc.text.append("4") }
        btn_5.setOnClickListener{ text_calc.text = text_calc.text.append("5") }
        btn_6.setOnClickListener{ text_calc.text = text_calc.text.append("6") }
        btn_7.setOnClickListener{ text_calc.text = text_calc.text.append("7") }
        btn_8.setOnClickListener{ text_calc.text = text_calc.text.append("8") }
        btn_9.setOnClickListener{ text_calc.text = text_calc.text.append("9") }
        btn_Dot.setOnClickListener{ text_calc.text = text_calc.text.append(".") }
        btn_0.setOnClickListener{ text_calc.text = text_calc.text.append("0") }
        btn_LParen.setOnClickListener{ text_calc.text = text_calc.text.append("(") }
        btn_RParen.setOnClickListener{ text_calc.text = text_calc.text.append(")") }
        btn_Divide.setOnClickListener{ text_calc.text = text_calc.text.append("/") }
        btn_Multiply.setOnClickListener{ text_calc.text = text_calc.text.append("*") }
        btn_Add.setOnClickListener{ text_calc.text = text_calc.text.append("+") }
        btn_Subtract.setOnClickListener{ text_calc.text = text_calc.text.append("-") }
        btn_Power.setOnClickListener{ text_calc.text = text_calc.text.append("^") }
        btn_Clear.setOnClickListener{
            if (text_calc.text.isNotEmpty()){
                val text:String=text_calc.text.substring(0,text_calc.text.length-1)//apaga o ultimo caracter
                text_calc.setText(text)
            }
            else{
                text_calc.setText("")
            }

        }
        btn_Clear.setOnLongClickListener{//Limpa os campos quando tem um evento de click longo
            text_calc.setText("")
            text_info.text=""
            return@setOnLongClickListener true}
        btn_Equal.setOnClickListener{
            try {//
                val tex:String=""+eval(text_calc.text.toString())//Pega o valor da função eval e seta nos widgets abaixo
                text_info.text = tex
                text_calc.setText(tex)
            }catch (e:RuntimeException){
                Toast.makeText(this,e.message,Toast.LENGTH_LONG).show()//Pega a exceção e exibe num toast
            }
        }


    }

    //Nesta função o conteúdo de text_info e text_calc é salvo após alterações no lifecycle
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (outState != null) {
            outState.putString("calc", text_calc.text.toString())
            outState.putString("info", text_info.text as String?)
        }
    }

    //Nesta função o conteúdo de text_info e text_calc é restaurado após alterações no lifecycle
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if(savedInstanceState!=null){
            val tex_info:String=savedInstanceState.getString("info")
            val tex_calc:String=savedInstanceState.getString("calc")
            text_info.setText(tex_info)
            text_calc.setText(tex_calc)
        }

    }

    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch: Char = ' '
            fun nextChar() {
                val size = str.length
                ch = if ((++pos < size)) str.get(pos) else (-1).toChar()
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Caractere inesperado: " + ch)
                return x
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            // | number | functionName factor | factor `^` factor
            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'))
                        x += parseTerm() // adição
                    else if (eat('-'))
                        x -= parseTerm() // subtração
                    else
                        return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'))
                        x *= parseFactor() // multiplicação
                    else if (eat('/'))
                        x /= parseFactor() // divisão
                    else
                        return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor() // + unário
                if (eat('-')) return -parseFactor() // - unário
                var x: Double
                val startPos = this.pos
                if (eat('(')) { // parênteses
                    x = parseExpression()
                    eat(')')
                } else if ((ch in '0'..'9') || ch == '.') { // números
                    while ((ch in '0'..'9') || ch == '.') nextChar()
                    x = java.lang.Double.parseDouble(str.substring(startPos, this.pos))
                } else if (ch in 'a'..'z') { // funções
                    while (ch in 'a'..'z') nextChar()
                    val func = str.substring(startPos, this.pos)
                    x = parseFactor()
                    if (func == "sqrt")
                        x = Math.sqrt(x)
                    else if (func == "sin")
                        x = Math.sin(Math.toRadians(x))
                    else if (func == "cos")
                        x = Math.cos(Math.toRadians(x))
                    else if (func == "tan")
                        x = Math.tan(Math.toRadians(x))
                    else
                        throw RuntimeException("Função desconhecida: " + func)
                } else {
                    throw RuntimeException("Caractere inesperado: " + ch.toChar())
                }
                if (eat('^')) x = Math.pow(x, parseFactor()) // potência
                return x
            }
        }.parse()
    }

}
