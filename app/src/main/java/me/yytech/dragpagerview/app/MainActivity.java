package me.yytech.dragpagerview.app;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

import me.yytech.dragpagerview.DragPagerView;
import me.yytech.dragpagerview.QueeAdapter;


public class MainActivity extends ActionBarActivity {

    private QueeAdapter mQueeAdapter;

    public int count = 5;
    private DragPagerView mDragPagerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDragPagerView = (DragPagerView) findViewById(R.id.dragPagerView);
        mDragPagerView.setOnEmptyListener(new DragPagerView.OnEmptyListener() {
            @Override
            public void onEmpty() {
                Toast.makeText(MainActivity.this, "empty", Toast.LENGTH_LONG).show();
            }
        });
        mDragPagerView.setOnMoreListener(new DragPagerView.OnMoreListener() {
            @Override
            public void onMore() {
                count = 10;
                mDragPagerView.notifyDataChange();
            }
        });
        mQueeAdapter = new QueeAdapter() {
            public int i = 0;

            @Override
            public View getNewView() {
                if (i != getCount()) {
                    View view = new View(MainActivity.this);
                    view.setBackgroundColor(Color.rgb(new Random().nextInt(255), 0, 0));
                    i++;
                    return view;
                }
                return null;
            }

            @Override
            public int getCount() {
                return count;
            }
        };
        mDragPagerView.setQueeAdapter(mQueeAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    public void onTurnLeft(View view) {
        mDragPagerView.turnPageLeft();
    }
}
