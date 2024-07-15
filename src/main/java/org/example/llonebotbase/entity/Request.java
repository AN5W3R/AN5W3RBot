package org.example.llonebotbase.entity;

import lombok.Data;
 
@Data
public class Request<T> {
 
    private String action;//即api中说的终结点
    private T params;
    private String echo;
}