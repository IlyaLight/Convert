package com.bibam;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;


public class Main {

    private static String text = "This new text \nThis new text2\nThis new text3\nThis new text4\n";
    private static String fileName = "a.txt";

    public static void main(String[] args) throws IOException {
	// write your code here
        //Запись в файл
        //Main.write(fileName, text);
        File [] filesin = new File[1];
        filesin[0] = new File("test4.gif");
        File fileout = new File("test.txt");


        try {
            Convert.convert(filesin, fileout, 18);
        } catch (UnableToEncodeException e) {
            System.out.println(e);
            e.printStackTrace();
        }


    }

    public static void write(String fileName, String text) {
        //Определяем файл
        File file = new File(fileName);

        try {
            //проверяем, что если файл не существует то создаем его
            if(!file.exists()){
                file.createNewFile();
            }

            //PrintWriter обеспечит возможности записи в файл
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

            try {
                //Записываем текст в файл
                out.print(text);
            } finally {
                //После чего мы должны закрыть файл
                //Иначе файл не запишется
                out.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
