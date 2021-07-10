package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DegreeAverageForm extends JFrame{

    private JPanel mainPanel;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JTextField scoreAndNzSum;
    private JLabel sumOfScoreAndNz;
    private JLabel numOfNz;
    private JLabel averageDegree;
    private JButton clearButton;
    private JButton calcButton;
    private JTextField nzSum;
    private JLabel degree;


    public DegreeAverageForm() {
        calcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String scoreAndNzSumNum = scoreAndNzSum.getText().trim();
                String numOfNzNum = nzSum.getText().trim();
                double ans = 0.0;
                if(Double.parseDouble(numOfNzNum) <= 0.0){
                   JOptionPane.showMessageDialog(null, "אין אפשרות לחלק ב0!");
                }
                else {
                    double num = eval(scoreAndNzSumNum);
                    ans = num / Double.parseDouble(numOfNzNum);
                    degree.setText(ans + "");
                }
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scoreAndNzSum.setText("");
                nzSum.setText("");
                degree.setText(0+"");
            }
        });
    }

    protected double eval(final String str) {
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
                    if      (eat('*')) x *= parseFactor(); // multiplication
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
                    x = switch (func) {
                        case "sqrt" -> Math.sqrt(x);
                        case "sin" -> Math.sin(Math.toRadians(x));
                        case "cos" -> Math.cos(Math.toRadians(x));
                        case "tan" -> Math.tan(Math.toRadians(x));
                        default -> throw new RuntimeException("Unknown function: " + func);
                    };
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("DegreeAverageForm");
        frame.setContentPane(new DegreeAverageForm().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
