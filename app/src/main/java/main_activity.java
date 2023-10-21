import static android.os.Build.VERSION_CODES.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

class MainActivity extends AppCompatActivity {

    private TextView resultTextView;
    private StringBuilder currentInput = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.resultTextView);

        // Numeric buttons
        int[] numericButtons = {R.id.b0, R.id.b1, R.id.b2, R.id.b3, R.id.b4, R.id.b5, R.id.b6, R.id.b7, R.id.b8, R.id.b9};
        for (int id : numericButtons) {
            Button btn = findViewById(id);
            btn.setOnClickListener(v -> {
                currentInput.append(btn.getText().toString());
                resultTextView.setText(currentInput.toString());
            });
        }

        // Operation buttons
        int[] operationButtons = {R.id.bplus, R.id.bmin, R.id.bmul, R.id.bdiv, R.id.bmod, R.id.bdot, R.id.bexpo};
        for (int id : operationButtons) {
            Button btn = findViewById(id);
            btn.setOnClickListener(v -> {
                currentInput.append(btn.getText().toString());
                resultTextView.setText(currentInput.toString());
            });
        }

        findViewById(R.id.bac).setOnClickListener(v -> {
            currentInput = new StringBuilder();
            resultTextView.setText("");
        });

        findViewById(R.id.bc).setOnClickListener(v -> {
            if (currentInput.length() > 0) {
                currentInput.deleteCharAt(currentInput.length() - 1);
                resultTextView.setText(currentInput.toString());
            }
        });

        findViewById(R.id.bequal).setOnClickListener(v -> evaluateExpression());
    }

    @SuppressLint("SetTextI18n")
    private void evaluateExpression() {
        double result;
        try {
            // Using eval for simplicity, but in production, consider using a proper math library or parser
            result = eval(currentInput.toString());
        } catch (Exception e) {
            resultTextView.setText("Error");
            return;
        }
        resultTextView.setText(String.valueOf(result));
        currentInput = new StringBuilder();
    }

    // A simple eval function that supports basic operations. Please consider a proper math library for production apps.
    private double eval(final String str) {
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
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            // Simplified parser for basic operations
            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }
                return x;
            }
        }.parse();
    }
}
