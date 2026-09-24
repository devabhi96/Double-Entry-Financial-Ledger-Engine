package com.devabhi96.Double_Entry_Financial_Ledger_Engine.dto;

public record ErrorResponse(ErrorBody error) {

    public record ErrorBody(String code,String message ,String txnId){}

    public static ErrorResponse of(String code,String message ){
        return new ErrorResponse(new ErrorBody(code,message,null));
    }
}
