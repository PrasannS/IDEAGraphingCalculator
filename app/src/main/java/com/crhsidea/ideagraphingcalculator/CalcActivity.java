package com.crhsidea.ideagraphingcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crhsidea.ideagraphingcalculator.Models.Expression;
import com.crhsidea.ideagraphingcalculator.Models.Expressions;

import java.util.ArrayList;

public class CalcActivity extends AppCompatActivity {

    private Button homebutton;
    private boolean isDecimal;
    private boolean firstform = false;
    private boolean onfunc = false;
    private TextView display;
    private String stringshown = "0";
    private int currentnum = 0;
    private int currentfunc = 0;
    Expressions e = new Expressions(new ArrayList<Expression>());
    private boolean betweenexpressions = false;
    private boolean formulamode = false;
    public ArrayList<Character> chars = new ArrayList<>();
    public ArrayList<Double> numbers = new ArrayList<>();
    String message = "";

    //TODO Here you need to add the IDs of all the buttons you just added
    int[] buttonIDList = {
            R.id.four,
            R.id.five,
            R.id.six,
            R.id.seven,
            R.id.eight,
            R.id.nine,
            R.id.add,
            R.id.subtract,
            R.id.divide,
            R.id.multiply,
            R.id.openbracket,
            R.id.closedbracket,
            R.id.exponent,
            R.id.decimal,
            R.id.pi,
            R.id.squared,
            R.id.sqrt,
            R.id.mod,
            R.id.E
    };

    //TODO Here you need to declare an array of buttons so that it works with buttonIDList no matter how many buttons are added/removed (Hint: Size)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        numbers.add(0.0);
        e.expressions.add(new Expression(chars, numbers));

        //TODO this is the toughest part, your job is to make a loop so that all of the buttons can be declared and have onClickListeners set
        // (Hint:Use while loop; Use the onValueInput([button_reference].getText().toString()) to enter the number into the calculator code)


        //TODO declare display, and declare/setOnClick for homebutton

    }

    //TODO make a method that will open Home Activity


    public void openThisActivity () {
        Intent intent1 = new Intent(this, CalcActivity.class);
        startActivity(intent1);
    }

    public void enternum (double i){
        Expression cur = e.expressions.get(e.currentExpression);
        if (i < 9 || i > -9 && !e.isDecimal(i)) {
            if (betweenexpressions) {
                e.expressions.add(new Expression(new ArrayList<Character>(), new ArrayList<Double>()));
                e.currentExpression++;
                currentfunc = 0;
                currentnum = 0;
                isDecimal = false;
                betweenexpressions = false;
            }

            if (onfunc) {
                currentnum++;
                cur.i.add(i);
                onfunc = false;
            } else if (cur.i.size() < 1) {
                cur.i.add(i);
            } else {
                if (firstform) {
                    if (isDecimal) {
                        firstform = false;
                        cur.setvar(cur.vars.get(0), i / Math.pow(10, e.getDecimalPlaces(cur.i.get(currentnum)) + 1));
                    } else {
                        firstform = false;
                        cur.setvar(cur.vars.get(0), i);
                    }
                } else if (isDecimal) {
                    cur.i.set(currentnum, cur.i.get(currentnum) + (i / Math.pow(10, e.getDecimalPlaces(cur.i.get(currentnum)) + 1)));
                } else
                    cur.i.set(currentnum, cur.i.get(currentnum) * 10 + i);

            }
            show(false);
        } else {
            cur.i.add(i);
        }
    }

    public void enterfunc ( char c){
        Expression cur = e.expressions.get(e.currentExpression);
        if (betweenexpressions) {
            e.interFuncs.add(c);
            onfunc = true;
        }
        if (!onfunc) {
            cur.c.add(currentfunc, c);
            currentfunc++;
            onfunc = true;
        } else {
            cur.c.set(currentfunc - 1, c);
        }
        show(false);

    }

    public void show ( boolean a){
        stringshown = e.toString();

        //TODO Set display's text to stringshown
    }

    public void onValueInput (String message){
        Expression cur = e.expressions.get(e.currentExpression);
        if (message.length() == 1 && Character.isDigit(message.charAt(0))) {
            enternum(Double.parseDouble(message));
        } else if (message.length() == 1) {
            switch (message.charAt(0)) {
                case 'π':
                    enternum(Math.PI);

                case '.':
                    isDecimal = true;
                    show(false);
                    break;
                case '(':
                    e.expressions.add(new Expression(new ArrayList<Character>(), new ArrayList<Double>(), '(', ')'));
                    if (cur.c.size() > cur.i.size()) {
                        e.interFuncs.add(cur.c.get(cur.c.size() - 1));
                        cur.c.remove(cur.c.size() - 1);
                    }
                    currentfunc = 0;
                    currentnum = 0;
                    isDecimal = false;
                    onfunc=false;
                    e.currentExpression++;
                    show(false);
                    break;
                case ')':
                    betweenexpressions = true;
                    show(false);
                    break;

                case '√':
                    e.expressions.add(new Expression(new ArrayList<Character>(), new ArrayList<Double>(), '√', ')'));
                    if (cur.c.size() > cur.i.size()) {
                        e.interFuncs.add(cur.c.get(cur.c.size() - 1));
                        cur.c.remove(cur.c.size() - 1);
                    }
                    currentfunc = 0;
                    currentnum = 0;
                    isDecimal = false;
                    e.currentExpression++;
                    break;
                case '=':
                    if (formulamode && cur.vars.size() > 0) {
                        firstform = true;
                        currentnum = cur.i.indexOf(cur.varcodes[0]);
                    } else {
                        try {
                            solveExpressions();
                        } catch (Exception e) {
                            display.setText("ERR");
                            Log.d("There is an error", "ERROR", e);
                        }
                        currentfunc = 0;
                        currentnum = 0;
                    }
                    break;
                case 'C':
                    openThisActivity();
                default:
                    enterfunc(message.charAt(0));
                    break;
            }
        }else if (message.equals("^2")) {
            enterfunc('^');
            enternum(2);
        }
    }

    public void solveExpressions () {
        e.solveAll();
        show(false);
        currentnum = 0;
        currentfunc = 0;
        onfunc = false;
    }
}
