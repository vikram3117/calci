package com.example.calci;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView text;
    String exp="";
    ArrayList<String> op=new ArrayList<String>();
    ListView lv;
    ArrayAdapter<String> ad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text=findViewById(R.id.input);
        lv=findViewById(R.id.lview);
        ad=new ArrayAdapter<String>(MainActivity.this,R.layout.listlayout,R.id.history,op);
        lv.setAdapter(ad);
    }

    public String getstr(String exp,View v)
    {
        String s=((Button)v).getText().toString();
        int n=exp.length();
        if(n==0)
        {
            if(s.equals("+")||s.equals("-")||s.equals("X")||s.equals("/")||s.equals("%"))
                return "";
            else
                return s;
        }
        if(exp.charAt(n-1)=='+'||exp.charAt(n-1)=='-'||exp.charAt(n-1)=='X'||exp.charAt(n-1)=='/'||exp.charAt(n-1)=='%')
        {
            if(s.equals("+")||s.equals("-")||s.equals("X")||s.equals("/")||s.equals("%"))
                exp=exp.substring(0,exp.length()-1);

        }
        exp+=s;
        return exp;
    }
    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('X')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
    double ans=0;
    @Override
    public void onClick(View v) {
        exp=text.getText().toString();
        switch(v.getId())
        {
            case R.id.ans: exp=ans+"";break;
            case R.id.clearall: exp="";break;
            case R.id.clear: if(exp.equals("Infinity")||exp.equals("NaN")) exp="";
                else if(exp.length()>0)exp=exp.substring(0,exp.length()-1);break;
            case R.id.equal:

                if(exp.charAt(exp.length()-1)=='+'||exp.charAt(exp.length()-1)=='-'||exp.charAt(exp.length()-1)=='X'||exp.charAt(exp.length()-1)=='/'||exp.charAt(exp.length()-1)=='%')
                    break;
                op.add(exp+"  ");
                exp=(exp.length()==0?0:(ans=eval(exp)))+"";
                op.add("="+exp+"  " );
                ad.notifyDataSetChanged();break;
            default:
                exp=getstr(exp,v);

        }
        text.setText(exp);

    }
}
