package com.example.tracker.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringToInt {

    public Long dateTime;

    public StringToInt() {
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public Integer convertToInt(Long input){
        Date date = new Date(input);

        SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
        String dateTime = sdf.format(date);

        Integer num = Integer.parseInt(dateTime);

        return num;
    }
}
