package ru.samsung.itschool.book.cells;

import android.content.Context;
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
import android.widget.RadioGroup;
import android.widget.TextView;

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

    public int WIDTH_MAX = 40; // переменные для динамического пользовательского изменения параметров поля
    public int HEIGHT_MAX  = 40;

    public int WIDTH_NEW = 9; // переменные для динамического пользовательского изменения параметров поля
    public int HEIGHT_NEW  = 9;
    public int NUMBER_OF_MINES_NEW  = 10;

    public int values[][] = new int[HEIGHT_MAX][WIDTH_MAX]; // массив карты {-1 - мина, i>=0 - количество мин-соседей}
    private Button[][] cells = new Button[HEIGHT_MAX][WIDTH_MAX];
    public boolean flags[][] = new boolean[HEIGHT_MAX][WIDTH_MAX];
    public boolean opened[][] = new boolean[HEIGHT_MAX][WIDTH_MAX];

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

        for (int i = 0; i < HEIGHT_MAX; i++)
            for (int j = 0; j < WIDTH_MAX; j++) {
                values[i][j] = 0;
                flags[i][j] = false;
                //cells[i][j]=0;
                opened[i][j] = false;
            }
        WIDTH = WIDTH_NEW ;
        HEIGHT  = HEIGHT_NEW;
        NUMBER_OF_MINES  = NUMBER_OF_MINES_NEW;
        first_click_made = false;
        number_of_flags = 0;
        TextView text = findViewById(R.id.mines_score);
        text.setText("Флагов осталось: " + String.valueOf(NUMBER_OF_MINES));
        number_of_opened_cells = 0;
        text = findViewById(R.id.game_result);
        text.setText("");
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
        int k = 0;
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
    }

    int getX(View v) {
        return Integer.parseInt(((String) v.getTag()).split(",")[1]);
    }

    int getY(View v) {
        return Integer.parseInt(((String) v.getTag()).split(",")[0]);
    }

    // выполняет прекращение игры - поражение
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void endGame(int y, int x){
        final GridLayout cellsLayout = (GridLayout) findViewById(R.id.CellsLayout);
        Drawable drawable;
        drawable = cellsLayout.getResources().getDrawable(R.drawable.end_mine);
        cells[y][x].setBackground(drawable);
        values[y][x]=0;

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                opened[i][j] = true;
                if (values[i][j] == -1 && !flags[i][j]) {
                    drawable = cellsLayout.getResources().getDrawable(R.drawable.mine);
                    cells[i][j].setBackground(drawable);
                }
                if (values[i][j] != -1 && flags[i][j]) {
                    drawable = cellsLayout.getResources().getDrawable(R.drawable.mine_false);
                    cells[i][j].setBackground(drawable);
                }

            }
        }
        TextView text = findViewById(R.id.game_result);
        text.setText("DEFEAT");
    }

    // выполняет прекращение игры - победа
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void winGame() {
        final GridLayout cellsLayout = (GridLayout) findViewById(R.id.CellsLayout);
        for (int i = 0; i < HEIGHT; i++){
            for (int j = 0; j < WIDTH; j++) {
                opened[i][j] = true;
                if (values[i][j] == -1) {
                    Drawable drawable = cellsLayout.getResources().getDrawable(R.drawable.flag);
                    cells[i][j].setBackground(drawable);
                }
            }
        }
        TextView text = findViewById(R.id.game_result);
        text.setText("VICTORY");
    }

    // рекурсивная обработка и отражение на экране соседей у клетки, на которю нажали
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void upWave(int i, int j){ // открывает мины, если выполены необходимые условия// погашение волны, если клетка не входит или уже открыта или имеет мины по-соседству
        if (!cellExists(i,j) || opened[i][j] || flags[i][j]) return;
        opened[i][j]=true;
        number_of_opened_cells++;

        final GridLayout cellsLayout = (GridLayout) findViewById(R.id.CellsLayout);
        Drawable drawable;
        switch(values[i][j]){
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

        // проверка на победу
        if (number_of_opened_cells==HEIGHT*WIDTH-NUMBER_OF_MINES){
            winGame();
            return;
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

    // смена режима игры
    public void acceptDifficult(View v){
        final GridLayout cellsLayout = (GridLayout) findViewById(R.id.CellsLayout);
        RadioGroup radio = findViewById(R.id.radio);
        switch (radio.getCheckedRadioButtonId()){
            case R.id.difficult_mode0:
                WIDTH_NEW = 4;
                HEIGHT_NEW  = 4;
                NUMBER_OF_MINES_NEW  = 1;
                break;
            case R.id.difficult_mode1:
                WIDTH_NEW  = 9;
                HEIGHT_NEW  = 9;
                NUMBER_OF_MINES_NEW  = 10;
                break;
            case R.id.difficult_mode2:
                WIDTH_NEW  = 16;
                HEIGHT_NEW  = 16;
                NUMBER_OF_MINES_NEW  = 40;
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void makeCells() {
        // post action надо, потому что дальше в коде наше поле делаем квадратным
        final GridLayout cellsLayout = (GridLayout) findViewById(R.id.CellsLayout); // очистить поле клеток
        cellsLayout.removeAllViews();

        TextView text = findViewById(R.id.mines_score); // установить счёт флагов
        text.setText("Флагов осталось: " + String.valueOf(NUMBER_OF_MINES));

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
                                if (opened[tappedY][tappedX] || flags[tappedY][tappedX]) return; // перестать реагировать на нажатие, если клавиши уже отображена или на ней флаг
                                if (first_click_made ) {

                                    if (values[tappedY][tappedX] == -1) {
                                        endGame(tappedY,tappedX);
                                    } else {
                                        upWave(tappedY,tappedX);
                                    }
                                }else{
                                    first_click_made=true;
                                    values[tappedY][tappedX]=777; // добавляет в массив значение первого нажатого элемента и его окружения
                                    if (HEIGHT>0) {
                                        if (cellExists(tappedY - 1, tappedX))
                                            values[tappedY - 1][tappedX] = 777;
                                        if (cellExists(tappedY + 1, tappedX))
                                            values[tappedY + 1][tappedX] = 777;
                                        if (cellExists(tappedY, tappedX - 1))
                                            values[tappedY][tappedX - 1] = 777;
                                        if (cellExists(tappedY + 1, tappedX + 1))
                                            values[tappedY + 1][tappedX + 1] = 777;
                                        if (cellExists(tappedY - 1, tappedX - 1))
                                            values[tappedY - 1][tappedX - 1] = 777;
                                        if (cellExists(tappedY + 1, tappedX - 1))
                                            values[tappedY + 1][tappedX - 1] = 777;
                                        if (cellExists(tappedY - 1, tappedX + 1))
                                            values[tappedY - 1][tappedX + 1] = 777;
                                        if (cellExists(tappedY, tappedX + 1))
                                            values[tappedY][tappedX + 1] = 777;
                                    }
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
                                if(opened[tappedY][tappedX]) return true;
                                TextView text = findViewById(R.id.mines_score);
                                if (!flags[tappedY][tappedX]) {
                                    Drawable drawable = cellsLayout.getResources().getDrawable(R.drawable.flag);
                                    cells[tappedY][tappedX].setBackground(drawable);
                                    flags[tappedY][tappedX]=true;
                                    number_of_flags++;
                                    text.setText("Флагов осталось: " + String.valueOf(NUMBER_OF_MINES-number_of_flags));
                                }else{
                                    Drawable drawable = cellsLayout.getResources().getDrawable(R.drawable.hide);
                                    cells[tappedY][tappedX].setBackground(drawable);
                                    flags[tappedY][tappedX]=false;
                                    number_of_flags--;
                                    text.setText("Флагов осталось: " + String.valueOf(NUMBER_OF_MINES-number_of_flags));
                                }
                                return true;
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