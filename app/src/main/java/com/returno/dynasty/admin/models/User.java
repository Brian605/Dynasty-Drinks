package com.returno.dynasty.admin.models;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;

import com.returno.dynasty.R;

import java.util.Random;

public class User {
   public static int previousNumber=0;

    private String userName,phoneNumber,location,imageUrl;

    public User( String userName, String phoneNumber,String location,String imageUrl) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.location=location;
        this.imageUrl=imageUrl;
    }

    public static void setPreviousNumber(int previousNumber) {
        User.previousNumber = previousNumber;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLocation() {
        return location;
    }

    public int getRandomColorInt(){
       int random=new Random().nextInt(5);
       if (random==previousNumber){
          random= getRandomColorInt();
       }else {
           return random;
       }
       return random;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public ShapeDrawable getCircleAvatarBg(){
        ShapeDrawable shapeDrawable=new ShapeDrawable();
        shapeDrawable.setShape(new Shape() {
            @Override
            public void draw(Canvas canvas, Paint paint) {
                paint.setColor(getRandomColor());
                canvas.drawCircle(0f,0f,20f,paint);
            }
        });
        return shapeDrawable;
    }

    public int getRandomColor(){

        switch (getRandomColorInt()){
            case 0: return R.color.orange;
            case 1:return R.color.grey;
            case 2: return R.color.pink;
            case 3: return R.color.black;
            case 4:return R.color.dark_orange;
            case 5: return R.color.ripple_color;
            default: return R.color.material_on_primary_disabled;
        }


    }
}
