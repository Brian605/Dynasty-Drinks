package com.returno.dynasty.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

import com.returno.dynasty.R;

public class IndicatedViewFlipper extends ViewFlipper {
Paint paint=new Paint();

    public IndicatedViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int width=getWidth();
        float margin=15.0f, radius=15.0f;
        float cx=(width/2.0f)-((radius+margin)*2*getChildCount()/2);
        float cy=getHeight()-15;
        canvas.save();
        for (int i=0; i<getChildCount();i++){
            if (i==getDisplayedChild()){
                paint.setColor(getResources().getColor(R.color.orange));
            }else{
                paint.setColor(getResources().getColor(R.color.grey));
            }
            canvas.drawCircle(cx,cy,radius,paint);
            cx+=2*(radius+margin);
        }
canvas.restore();
    }
}
