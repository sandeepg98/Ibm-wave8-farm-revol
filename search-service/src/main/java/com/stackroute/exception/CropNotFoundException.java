package com.stackroute.exception;

public class CropNotFoundException extends Exception{
    private String message;
    public CropNotFoundException(){
    }
    public CropNotFoundException(String message){
        super(message);
        this.message=message;
    }

}
