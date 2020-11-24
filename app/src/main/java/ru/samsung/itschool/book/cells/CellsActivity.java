package ru.samsung.itschool.book.cells;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;

import javax.swing.*;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class CellsActivity extends AppCompatActivity {

    private final Context context = this;

    public int WIDTH = 9;
    public int HEIGHT = 9;
    public int NUMBER_OF_MINES = 10;
    public boolean first_click_made=false;
    public int number_of_flags=0;
    public int number_of_opened_cells=0;

    public int values[][] = new int[HEIGHT][WIDTH]; // массив карты {-1 - мина, i>=0 - количество мин-соседей}
    private Button[][] cells = new Button[HEIGHT][WIDTH];
    public boolean flags[][] = new boolean[HEIGHT][WIDTH];
    public boolean opened[][] = new boolean[HEIGHT][WIDTH];

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_interface);
        makeCells();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    // перезапустить карту в приложении
    public void resetMap (View v) {
        final GridLayout cellsLayout = (GridLayout) findViewById(R.id.CellsLayout);

        for (int i = 0; i < HEIGHT; i++)
            for (int j = 0; j < WIDTH; j++) {
                values[i][j] = 0;
                flags[i][j] = false;
                //cells[i][j]=0;
                opened[i][j] = false;
            }
        first_click_made = false;
        number_of_flags = 0;
        number_of_opened_cells = 0;
        makeCells();
    }

    // входит ли клетка в двумерный массив
    public boolean cellExists(int i, int j){
        return (i>-1 && j>-1 && i<HEIGHT && j<WIDTH ? true : false);
    }

    // подсчёт количества соседних мин
    int countNumberNearbyMines(int i, int j){
        int y=i, x=j, s=0;
        if (cellExists(y - 1, x - 1) && values[y-1][x-1]==-1) s++;
        if (cellExists(y - 1, x) && values[y-1][x]==-1) s++;
        if (cellExists(y - 1, x + 1) && values[y-1][x+1]==-1) s++;
        if (cellExists(y, x - 1) && values[y][x-1]==-1) s++;
        if (cellExists(y, x + 1) && values[y][x+1]==-1) s++;
        if (cellExists(y + 1, x - 1) && values[y+1][x-1]==-1) s++;
        if (cellExists(y + 1, x) && values[y+1][x]==-1) s++;
        if (cellExists(y + 1, x + 1) && values[y+1][x+1]==-1) s++;
        return s;
    }

    void generate() {

        // заполняем массив карты минами и количеством соседних мин
        int k = 1;
        while (k < NUMBER_OF_MINES) {
            Double yd = Math.random() * HEIGHT, xd = Math.random() * WIDTH;
            int y = yd.intValue() % HEIGHT, x = xd.intValue() % WIDTH;
            if (values[y][x] != -1 && values[y][x] != 777) { // не мина и не заризервировання под ноль клетку - первое нажатие
                values[y][x] = -1;
                k++;
            }
        }

        // добавление количества соседей
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (values[i][j]==-1) continue; // дальше, если в ячейке мина
                if (values[i][j]==777){ // заменить резервацию на стандартный 0 и продолжить
                    values[i][j]=0;
                    continue;
                }
                values[i][j]=countNumberNearbyMines(i,j);
            }
        }

        // вывод всех значений в клетки
        /*
        for (int i = 0; i < HEIGHT; i++){
            for (int j = 0; j < WIDTH; j++) {
                String val;
                switch (values[i][j]){
                    case -1:
                        val="*";
                        break;
                    case 0:
                        val="";
                        break;
                    default:
                        val=String.valueOf(values[i][j]);
                        break;
                }
                cells[i][j].setText(val);
            }
        }

         */

    }

    int getX(View v) {
        return Integer.parseInt(((String) v.getTag()).split(",")[1]);
    }

    int getY(View v) {
        return Integer.parseInt(((String) v.getTag()).split(",")[0]);
    }

    // выполняет прекращение игры
    void endGame(){

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void upWave(int i, int j){ // открывает мины, если выполены необходимые условия// погашение волны, если клетка не входит или уже открыта или имеет мины по-соседству
        if (!cellExists(i,j) || opened[i][j] || flags[i][j]) return;
        opened[i][j]=true;

        final GridLayout cellsLayout = (GridLayout) findViewById(R.id.CellsLayout);
        Drawable drawable;
        switch(values[i][j]){
            case -1:
                drawable = cellsLayout.getResources().getDrawable(R.drawable.end_mine);
                cells[i][j].setBackground(drawable);
                endGame();
                break;
            case 0:
                drawable = cellsLayout.getResources().getDrawable(R.drawable.zero);
                cells[i][j].setBackground(drawable);
                break;
            case 777:
                drawable = cellsLayout.getResources().getDrawable(R.drawable.zero);
                cells[i][j].setBackground(drawable);
                break;
            case 1:
                drawable = cellsLayout.getResources().getDrawable(R.drawable.one);
                cells[i][j].setBackground(drawable);
                break;
            case 2:
                drawable = cellsLayout.getResources().getDrawable(R.drawable.two);
                cells[i][j].setBackground(drawable);
                break;
            case 3:
                drawable = cellsLayout.getResources().getDrawable(R.drawable.three);
                cells[i][j].setBackground(drawable);
                break;
            case 4:
                drawable = cellsLayout.getResources().getDrawable(R.drawable.four);
                cells[i][j].setBackground(drawable);
                break;
        }

        // погашение волны, если клетка имеет мины по-соседству
        if(countNumberNearbyMines(i,j)>0) return;
        // запрос в соседние клетки
        upWave(i-1,j-1);
        upWave(i-1,j);
        upWave(i-1,j+1);
        upWave(i,j-1);
        upWave(i,j+1);
        upWave(i+1,j-1);
        upWave(i+1,j);
        upWave(i+1,j+1);
    }
/*
    // вызывает окно выбора сложности (по умолчанию новичёк) сложности: {амёба - 3х3 1, новичёк - 9х9 10, бывалый - 16х16 40}
    void showDufficultlySelection(){
        JOptionPane panel = new JOptionPane()
        Object[] possibleValues = { "First", "Second", "Third" };
        Object selectedValue = JOptionPane.showInputDialog(null,
                "Choose one", "Input",
                JOptionPane.INFORMATION_MESSAGE, null,
                possibleValues, possibleValues[0]);
    }
*/
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void makeCells() {
        // post action надо, потому что дальше в коде наше поле делаем квадратным
        final GridLayout cellsLayout = (GridLayout) findViewById(R.id.CellsLayout);
        cellsLayout.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams fieldParams = cellsLayout.getLayoutParams();
                //квадрирует рамку
                int size = Math.min(cellsLayout.getWidth(), cellsLayout.getHeight());
                fieldParams.width = size;
                fieldParams.height = size;

                cellsLayout.setLayoutParams(fieldParams);
                cellsLayout.setColumnCount(WIDTH);
                cellsLayout.setRowCount(HEIGHT);
                Drawable drawable = cellsLayout.getResources().getDrawable(R.drawable.hide);

                for (int i = 0; i < HEIGHT; i++) {
                    for (int j = 0; j < WIDTH; j++) {
                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        cells[i][j] = (Button) inflater.inflate(R.layout.cell, cellsLayout, false);

                        cells[i][j].setTag(i + "," + j);

                        GridLayout.LayoutParams lp = new GridLayout.LayoutParams(); // создаем параметры для кнопки
                        lp.width = 0;
                        lp.height = 0;
                        lp.columnSpec = GridLayout.spec(j, 1f); // вес и позиция кнопки по горизонтали
                        lp.rowSpec = GridLayout.spec(i, 1f); // по вертикали

                        cells[i][j].setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Button tappedCell = (Button) v;

                                int tappedX = getX(tappedCell);
                                int tappedY = getY(tappedCell);
                                if (first_click_made && !flags[tappedY][tappedX]) {

                                    if (values[tappedY][tappedX] == -1) {
                                        endGame();
                                        cells[tappedY][tappedX].setText("*");

                                    } else {
                                        upWave(tappedY,tappedX);
                                    }
                                }else{
                                    first_click_made=true;
                                    values[tappedY][tappedX]=777; // добавляет в массив значение первого нажатого элемента и его окружения
                                    if (cellExists(tappedY-1,tappedX)) values[tappedY-1][tappedX]=777;
                                    if (cellExists(tappedY+1,tappedX))values[tappedY+1][tappedX]=777;
                                    if (cellExists(tappedY,tappedX-1))values[tappedY][tappedX-1]=777;
                                    if (cellExists(tappedY+1,tappedX+1))values[tappedY+1][tappedX+1]=777;
                                    if (cellExists(tappedY-1,tappedX-1)) values[tappedY-1][tappedX-1]=777;
                                    if (cellExists(tappedY+1,tappedX-1)) values[tappedY+1][tappedX-1]=777;
                                    if (cellExists(tappedY-1,tappedX+1))values[tappedY-1][tappedX+1]=777;
                                    if (cellExists(tappedY,tappedX+1))values[tappedY][tappedX+1]=777;
                                    if (cellExists(tappedY+2,tappedX+1))values[tappedY+2][tappedX+1]=-1;
                                    generate(); // генерирует оставшееся поле
                                    upWave(tappedY,tappedX); // вызывает волну проверок соседей
                                }
                            }
                        });

                        // ставим флажок на долгое нажатие
                        cells[i][j].setOnLongClickListener(new OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                Button tappedCell = (Button) v;

                                int tappedX = getX(tappedCell);
                                int tappedY = getY(tappedCell);
                                if (!flags[tappedY][tappedX]) {
                                    Drawable drawable = cellsLayout.getResources().getDrawable(R.drawable.flag);
                                    cells[tappedY][tappedX].setBackground(drawable);
                                }else{
                                    Drawable drawable = cellsLayout.getResources().getDrawable(R.drawable.hide);
                                    cells[tappedY][tappedX].setBackground(drawable);
                                }
                                return false;
                            }
                        });
                        cells[i][j].setBackground(drawable);
                        cellsLayout.addView(cells[i][j], lp); // добавляем кнопку на поле
                    }
                }
            }
        });
    }


}