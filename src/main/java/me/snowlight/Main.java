package me.snowlight;

import me.snowlight.config.db.MyBatisConfig;

public class Main {
    public static void main(String[] args) {

        MyBatisConfig myBatisConfig = new MyBatisConfig();
        myBatisConfig.config();

//        Sync sync = new Sync();
//        sync.sync();
    }
}

//파일로 재처리 대상 관리
//방식
//* 복제에 실패한 데이터를 JSON 형식으로 1차 재처리 파일에 기록
//* 5분 간격으로 1차 재처리 파일을 읽어와 재처리 시도
//  * 데이터를 읽은 뒤 파일 삭제
//* 1차 재처리에 실패한 데이터를 2차 재처리 파일에 기록
//* 30분 간격으로 2차 재처리 파일을 읽어와 재처리 시도
//  * 데이터를 읽은 뒤 파일 삭제
//* 3시간 간격으로 3차 재처리 파일을 읽어와 재처리 시도
//  * 데이터를 읽은 뒤 파일 삭제