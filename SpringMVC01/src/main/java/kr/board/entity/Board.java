package kr.board.entity;

import lombok.Data;

@Data //- Lombok API
public class Board {
  private int idx; //
  private String title; // 
  private String content; // 
  private String writer; // 
  private String indate; // 
  private int count; 
  // setter , getter

  // lombok jar 사용하면 getter setter 필요없음
  
}