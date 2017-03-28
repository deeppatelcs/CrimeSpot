package com.example.dp863.crimespot;

import android.view.View;

class MyClickListener implements View.OnClickListener {

    private int position;
    private boolean[] isChecked;

    public MyClickListener(int position, boolean isChecked[]) {
        this.position = position;
        this.isChecked = isChecked;
    }

    public void onClick(View v) {
        System.out.println("position " + getPosition() + " clicked.");
        System.out.println("Before " + getPosition() + " " + getIsChecked(position));
        if(this.isChecked[position]){
            this.isChecked[position] = false;
        }else{
            this.isChecked[position] = true;
        }
        System.out.println("After " + getPosition() + " " + getIsChecked(position));
    }

    public int getPosition() {
        return position;
    }

    public boolean getIsChecked(int position) {
        return isChecked[position];
    }

    public void setIsChecked(boolean[] isChecked) {
        this.isChecked = isChecked;
    }

}
