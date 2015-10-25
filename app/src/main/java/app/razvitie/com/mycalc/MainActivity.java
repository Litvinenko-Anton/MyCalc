package app.razvitie.com.mycalc;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.EnumMap;


public class MainActivity extends ActionBarActivity {

    private EditText txtResult;

    private Button btnAddition;
    private Button btnSubtraction;
    private Button btnDivision;
    private Button btnMultiplication;

    private OperationType operationType;

    //  хранит введенные данные пользователя
    private EnumMap<Symbol, Object> commands = new EnumMap<Symbol, Object>(Symbol.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        txtResult = (EditText) findViewById(R.id.txtResult);

        btnAddition = (Button) findViewById(R.id.btnAddition);
        btnSubtraction = (Button) findViewById(R.id.btnSubtraction);
        btnMultiplication = (Button) findViewById(R.id.btnMultiplication);
        btnDivision = (Button) findViewById(R.id.btnDivision);

        // добавляем тип операции к кнопке, хранятся в OperationType.java
        btnAddition.setTag(OperationType.ADDITION);
        btnSubtraction.setTag(OperationType.SUBTRACTION);
        btnMultiplication.setTag(OperationType.MULTIPLICATION);
        btnDivision.setTag(OperationType.DIVISION);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void showToastMessage (int messageId) {
        Toast toastMessage = Toast.makeText(this, messageId, Toast.LENGTH_LONG);
        toastMessage.setGravity(Gravity.TOP, 0, 100);
        toastMessage.show();
    }

    private ActionType lastAction;                     // в эту переменную сохраняем последнюю операцию пользователя

    public void buttonClick (View view){

        switch (view.getId()){
            case R.id.btnAddition:
            case R.id.btnSubtraction:
            case R.id.btnMultiplication:
            case R.id.btnDivision: {  // кнопка - это одна из операций

                operationType = (OperationType) view.getTag();                  // получения типа операции из кнопки

                if (lastAction == ActionType.LAST_OPERATION) {
                    commands.put(Symbol.OPERATION, operationType);
                    return;
                }

                if (!commands.containsKey(Symbol.OPERATION)) {                  // если ранее пользователь не пытался нечего посчитать

                    if (!commands.containsKey(Symbol.FIRST_DIGIT)) {            // если не записанно первое чесло
                        commands.put(Symbol.FIRST_DIGIT, txtResult.getText());  // тогда записываем первое чесло
                    }

                    commands.put(Symbol.OPERATION, operationType);              // записываем тип операции
                } else if (!commands.containsKey(Symbol.SECOND_DIGIT)) {        // если у нас не записанно второе чесло
                    commands.put(Symbol.SECOND_DIGIT, txtResult.getText());     // тогда записываем второе чесло
                    doCalc();                                                   // проводим вычисления методом doCalc
                    commands.put(Symbol.OPERATION, operationType);              // записываем тип операции
                    commands.remove(Symbol.SECOND_DIGIT);                       // удоляется второе чесло для продолжания вычисления
                }

                lastAction = ActionType.LAST_OPERATION;

                break;

            }

            // кнопка очистки "Сброс"
            case R.id.btnClear: {
                txtResult.setText("0");  // обнуляет txtResult
                commands.clear();        // стирает все введенные команды
                lastAction = ActionType.CLEAR;
                break;
            }

            // кнопка  "Выход"
            case R.id.btnExit: {
                finish();  // инициализирует выход их приложения
                break;
            }

            // кнопка вычисления "="
            case R.id.btnResult: {

                if (lastAction == ActionType.CALCULATION) return;  // если кнопка "=" нажимается повторно то выходим
                // это исключает многократное увеличение результат при нажимании "="

                // если введено первое число и нажали кнопку операции
                if (commands.containsKey(Symbol.FIRST_DIGIT) && commands.containsKey(Symbol.OPERATION)){
                    commands.put(Symbol.SECOND_DIGIT, txtResult.getText());     // тогда записываем второе чесло
                    doCalc();                                                   // проводим вычисления методом doCalc
                    commands.clear();                                           // очищаем все введенные команды (сброс FIRST/SECOND_DIGIT чисел)

   /*                 commands.put(Symbol.OPERATION, operationType);              // записываем тип операции
                    commands.remove(Symbol.SECOND_DIGIT);                       // удоляется второе чесло для продолжания вычисления */
                }

                lastAction = ActionType.CALCULATION;
                break;
            }

            // кнопка вычисления "," для ввода дисятичного числа
            case R.id.btnComma: {
                if (commands.containsKey(Symbol.FIRST_DIGIT) && getDouble(txtResult.getText().toString())
                        == getDouble(commands.get(Symbol.FIRST_DIGIT).toString()) ){
                    txtResult.setText("0" + view.getContentDescription().toString());
                }
                if (!txtResult.getText().toString().contains(",")){
                    txtResult.setText(txtResult.getText() + ",");
                }
                lastAction = ActionType.COMMA;
                break;
            }

            // кнопка удаления последнего символа "<"
            case R.id.btnBackspace: {
                txtResult.setText(txtResult.getText().delete(
                        txtResult.getText().length() - 1,
                        txtResult.getText().length()));
                if (txtResult.getText().toString().trim().length() == 0) {
                    txtResult.setText("0");
                }
                lastAction = ActionType.DELETE;
                break;
            }

            // действие при нажатии все остальных кнопок (тоесть остальсь только цифры)
            default:{
                if (txtResult.getText().toString().equals("0") ||
                        (commands.containsKey(Symbol.FIRST_DIGIT) &&
                                getDouble(txtResult.getText()) ==
                                        getDouble(commands.get(Symbol.FIRST_DIGIT)))  // если вводится число, то нужно сбросить текстовое поле
                        || (lastAction == ActionType.CALCULATION)

                        ) {
                    txtResult.setText(view.getContentDescription().toString());

                } else {

                    txtResult.setText(txtResult.getText() + view.getContentDescription().toString());  // мтрока вывода чисел в txtResult

                }

                lastAction = ActionType.DIGIT;
            }
        }
    }

    // метод заменяет запятую на точку
    private double getDouble(Object value) {

        double result = 0;

        try {
            result = Double.valueOf(value.toString().replace(',', '.')).doubleValue(); // замена запятой на точку
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }

        return result;

    }

    private void doCalc() {
        OperationType operationTypeTemp = (OperationType) commands.get(Symbol.OPERATION); // сохраняем предыдущий тип операции в Temp 2 (Temp)+ 1 +..
        // 2 + 1 +

        double result = 0;

        try {                                                                     // отлавливаем ошибку в методе calc путем try
            result = calc(operationTypeTemp,                                      // делаем подсчетс с помощу метода calc
                    getDouble(commands.get(Symbol.FIRST_DIGIT)),
                    getDouble(commands.get(Symbol.SECOND_DIGIT)));
        } catch (DivisionByZeroException e) {
            showToastMessage(R.string.division_zero);                            // вывод Toast(метод showToastMessage) предупреждение при делении на ноль
            return;                                                              // возврат с сохранение первого числа и значения операции,
            // для повторного ввода второго числа
        }

        if (result % 1 == 0){                               // если при делении на единицу остаток равен нулю (нет цифер после запятой)
            txtResult.setText(String.valueOf((int)result)); // отсекает нули после запятой
        } else {
            txtResult.setText(String.valueOf(result));      // иначе оставляет число как есть
        }
        commands.put(Symbol.FIRST_DIGIT, result);  // записует полученный результат в первое число,
        // чтобы можно было выполнять следующие операции
    }

    private Double calc(OperationType operationType, double a, double b) {
        switch (operationType) {
            case ADDITION: {
                return CalcOperation.addition(a, b);
            }
            case SUBTRACTION: {
                return CalcOperation.subtraction(a, b);
            }
            case DIVISION: {
                return CalcOperation.division(a, b);
            }
            case MULTIPLICATION: {
                return CalcOperation.multiplication(a, b);
            }
        }

        return Double.parseDouble(null);
    }

//    Выходъ
    @Override
    protected void onDestroy() {
        super.onDestroy();

        Toast.makeText(getApplicationContext(), "Выход совершен!", Toast.LENGTH_SHORT).show(); // Выводит Toast сообщение перед выходом

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
