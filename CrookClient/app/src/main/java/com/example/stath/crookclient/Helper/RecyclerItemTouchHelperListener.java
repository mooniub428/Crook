package com.example.stath.crookclient.Helper;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;

public interface RecyclerItemTouchHelperListener {

    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);

    void  onChildDraw(RecyclerView.ViewHolder viewHolder, float dX);
}
