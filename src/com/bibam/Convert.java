package com.bibam;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;


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
        String[] progmem = new String[arrayFilesIn.length];
        int fileCount = 0;
        for (File fileIn :arrayFilesIn) {
            BufferedImage image = ImageIO.read(fileIn);
            int height = image.getHeight();
            int width = image.getWidth();
            int numBytes = height * width;
            FastRGB fastRGB = new FastRGB(image);
            ArrayList<Byte[]> palette = fastRGB.getRGBpaletteByt();
            out.print("// " + fileIn.getName() + " ------------------------------------------------------------\n\n");
            int paletteN;
            if (palette.size() < 256){
                if (palette.size() <= 2)
                    paletteN = 2;               //монохромное изображение
                else if (palette.size() <= 16)
                    paletteN = 4;               //не больше 16 цветов в палитре, один цвет 2 бита
                else paletteN = 8;              //не больше 255 цветов в палитре, один цвет 1 байт

                if (palette.size()<= 16) {
                    progmem[fileCount] = String.format("{ PALETTE%d , %4d, (const uint8_t *)palette%02d, pixels%02d }",
                            paletteN, width, fileCount, fileCount);
                    out.format("const uint8_t PROGMEM palette%02d[][3] = {", fileCount);
                    for (int i = 0; ; ) {
                        byte rgb[] = new byte[]{
                                palette.get(i)[0],
                                palette.get(i)[1],
                                palette.get(i)[2]};
                        out.print("\n\t{");
                        out.format(" %3d,", rgb[0] & 0b11111111);
                        out.format(" %3d,", rgb[1] & 0b11111111);
                        out.format(" %3d", rgb[2] & 0b11111111);
                        out.print(" }");
                        i++;
                        if (i >= palette.size())
                            break;
                        ;
                        out.print(",");
                    }
                    out.print("};\n\n");
                    out.format("const uint8_t PROGMEM pixels%02d[] = {", fileCount);
                    int bytNum = 0;
                    for (int x = 0; x < width; x++) {
                        out.print("\n    ");
                        for (int y = 0; y < height; y += 2) {
                            byte b = (byte) getPosition(palette, fastRGB.getRGBbyt(x, y));
                            bytNum++;
                            if (y + 1 < height)
                                b = (byte) (b * 16 + getPosition(palette, fastRGB.getRGBbyt(x, y + 1)));
                            out.format(" %3d", b);
                            if (bytNum < numBytes / 2)
                                out.print(",");
                        }
                    }
                    out.print(" };\n\n");
                    fileCount++;
                }
            }
            else {

            }


        }
        out.print("typedef struct \n" +
                "{\n" +
                "\tuint8_t\t\ttype;\t\t// PALETTE[1,4,8] or TRUECOLOR\n" +
                "\tuint16_t\tlines;\t\t// Length of image (in scan lines)\n" +
                "\tconst uint8_t *palette; // -> PROGMEM color table (NULL if truecolor)\n" +
                "\tconst uint8_t *pixels;  // -> Pixel data in PROGMEM\n" +
                "} Image;\n" +
                "\n" +
                "const Image PROGMEM images[] = {\n");
        for (int i = 0;;) {
            out.print("\t" + progmem[i]);
            i++;
            if (i>=fileCount)
                break;;
            out.println(",");
        }
        out.print("};\n" +
                "\n" +
                "#define NUM_IMAGES (sizeof(images) / sizeof(images[0])) \n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "#endif /* GRAPHICS_H_ */");
        out.close();


    }

    private static int getPosition (ArrayList<Byte[]> palette, Byte[] color){
        int i = 0;
        for (; i < palette.size(); i++) {
            if(Arrays.equals(palette.get(i), color))
                break;
        }
        return i;
    }

}
