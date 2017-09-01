package com.bibam;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.applet.Applet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class Convert {
    private static String head = "// Don't edit this file!  It's software-generated.\n" +
            "\n" +
            "#define PALETTE1  0\n" +
            "#define PALETTE4  1\n" +
            "#define PALETTE8  2\n" +
            "#define TRUECOLOR 3\n" +
            "\n";
    private static String bodi;
    private static String foot;

     /**convert*/
    public static void convertGIF (File[] arrayFilesIn, File fileOut, int pixelNumber)
            throws UnableToEncodeException,  NullPointerException,  IOException {
        if (arrayFilesIn == null) {
            throw new NullPointerException("Input files is null");
        }
        if (fileOut == null) {
            throw new NullPointerException("Output file is null");
        }
        if (pixelNumber == 0) {
            throw new UnableToEncodeException("pixelNumber must be > 0");
        }

        //
        PrintWriter out = new PrintWriter(fileOut);
        out.print(head);
        out.print("#define NUM_LEDS " + pixelNumber + "\n\n");

        int fileCount = 0;
        for (File fileIn :arrayFilesIn) {
            // read bytes from input file
            byte[] bytes = new byte[(int)fileIn.length()];
            InputStream is = new FileInputStream(fileIn);
            is.read(bytes);
            is.close();

            // check format
            if (!(new String(bytes, 0, 6)).equals("GIF89a")) {
                throw new UnableToEncodeException("Input file has wrong GIF format");
            }

            // check palette
            if(Binary.toBitArray(bytes[10])[7] == 1) {  //The palette is used
                // read palette size property from first three bits in the 10-th byte from the file
                byte[] b10 = Binary.toBitArray(bytes[10]);
                int palette = 3 * (int)Math.pow(2, (1+Binary.toByte(new byte[]{b10[0], b10[1], b10[2]}))); //palette size, byte

//                for (int i = paletteColors; i !=0 ; i--) {
//                    if(bytes[13+(i*3)-0] != 0 & bytes[13+(i*3)-1] != 0 & bytes[13+(i*3)-2] != 0)
//                        break;
//                    paletteColors--;
//                }

                if (palette <= 24){
                    out.print("// " + fileIn.getName() + " ------------------------------------------------------------\n");
                    out.print("const uint8_t PROGMEM palette0" + fileCount + "[][3] = {");
                    for (int i = 0; i < palette;) {
                        out.print("\n\t{");
                        out.format(" %3d,", bytes[13 + i + 0] & 0b11111111);
                        out.format(" %3d,", bytes[13 + i + 1] & 0b11111111);
                        out.format(" %3d,", bytes[13 + i + 2] & 0b11111111);
                        out.print(" }");
                        i += 3;
                        if (bytes[13 + i + 0] == 0 && bytes[13 + i + 1] == 0 && bytes[13 + i + 2] == 0) {   //пропускаем пустые места в палитре
                            break;
                        }
                        if (i != palette) out.print(",");
                    }
                    out.print(" };\n");
                    out.print("const uint8_t PROGMEM pixels0" + fileCount + "[] = {");
                    
                }


            }
            /*если выста картинки не совподает с количеством пикселей вызываем исключение*/
            //if (bufImage.getHeight() != pixelNumber){}
            //for (int j = 0; j < bufImage.getHeight(); j++) {

            //}
            //try {
            //    bufImage = ImageIO.read(new File(element));
            //}
              //  catch(IOException e) {
                //    e.printStackTrace();
                //}




        }

        //save out put file
//        PrintWriter out = new PrintWriter(fileOut);
//        try {
//            //Записываем текст у файл
//            out.print(outputTxt);
//            out.print("\n test");
//            out.format("\n test |%8d", 3);
//
//        } finally {
//            //После чего мы должны закрыть файл
//            //Иначе файл не запишется
//            out.close();
//        }
        out.close();
    }

    public static void convert (File[] arrayFilesIn, File fileOut, int pixelNumber)
         throws UnableToEncodeException,  NullPointerException,  IOException {
            if (arrayFilesIn == null) {
                throw new NullPointerException("Input files is null");
            }
            if (fileOut == null) {
                throw new NullPointerException("Output file is null");
            }
            if (pixelNumber == 0) {
                throw new UnableToEncodeException("pixelNumber must be > 0");
            }

            PrintWriter out = new PrintWriter(fileOut);
            out.print(head);
            out.print("#define NUM_LEDS " + pixelNumber + "\n\n");
        int fileCount = 0;
        for (File fileIn :arrayFilesIn) {

            BufferedImage image = ImageIO.read(fileIn);
            FastRGB fastRGB = new FastRGB(image);
            ArrayList<Byte[]> palette = fastRGB.getRGBpaletteByt();
            if (palette.size()<= 16){
                out.print("// " + fileIn.getName() + " ------------------------------------------------------------\n");
                out.print("const uint8_t PROGMEM palette0" + fileCount + "[][3] = {");
                for (int i = 0; i < palette.size(); ) {
                    byte rgb[] = new byte[]{
                            palette.get(i)[0],
                            palette.get(i)[1],
                            palette.get(i)[2]};
                    if (rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 0) {
                        i++;
                        continue;
                    }
                    out.print("\n\t{");
                    out.format(" %3d,", rgb[0] & 0b11111111);
                    out.format(" %3d,", rgb[1] & 0b11111111);
                    out.format(" %3d,", rgb[2] & 0b11111111);
                    out.print(" }");
                    if (i != palette.size())
                        out.print(",");
                    i++;
                }
                out.print(" };\n");
                out.print("const uint8_t PROGMEM pixels0" + fileCount + "[] = {");
            }


        }
        out.close();

    }



}
