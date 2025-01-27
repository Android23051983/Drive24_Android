package com.example.drive24;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private final int dividerHeight;
    private final Paint paint;

    public DividerItemDecoration(int dividerHeight, int color) {
        this.dividerHeight = dividerHeight;

        paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(dividerHeight);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,@NonNull RecyclerView parent, @NonNull RecyclerView.State state){
        if(parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount()-1) {
            outRect.bottom = dividerHeight;
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(canvas, parent, state);

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) { // Пропускаем последний элемент
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            // Рисуем линию под каждым элементом
            int top = child.getBottom() + params.bottomMargin + dividerHeight / 2;
            int bottom = top + dividerHeight;

            // Рисуем линию посередине между элементами
            canvas.drawLine(child.getLeft(), top, child.getRight(), bottom, paint);
        }
    }
}
