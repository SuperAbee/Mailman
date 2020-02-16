package com.saviour.mailman.tool;

public enum Parameter {
    VIDEOHEIGHT(100),
    VIDEOWIDTH(100),
    STARTFLAG(0);

    private int value;

    Parameter(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }
}
