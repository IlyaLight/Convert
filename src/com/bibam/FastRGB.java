package com.bibam;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.LinkedHashSet;

//**FastRGB
// используется для быстрого доступа к пикселям изображения BufferedImage,
// при создании объекта класса все пиксели из image копируются байтовый массив pixels
// и дальнейшая работа происходит через обращения к этому массиву*/
public class FastRGB
{

    private int width;
    private int height;
    private boolean hasAlphaChannel;
    private int pixelLength;
    private byte[] pixels;

    FastRGB(BufferedImage image)
    {

        pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        width = image.getWidth();
        height = image.getHeight();
        hasAlphaChannel = image.getAlphaRaster() != null;
        pixelLength = 3;
        if (hasAlphaChannel)
        {
            pixelLength = 4;
        }

    }

    int getRGB(int x, int y)
    {
        int pos = (y * pixelLength * width) + (x * pixelLength);

        int argb = -16777216; // 255 alpha
        if (hasAlphaChannel)
        {
            argb = (((int) pixels[pos++] & 0xff) << 24); // alpha
        }

        argb += ((int) pixels[pos++] & 0xff); // blue
        argb += (((int) pixels[pos++] & 0xff) << 8); // green
        argb += (((int) pixels[pos++] & 0xff) << 16); // red
        return argb;
    }

    byte[][] getRGBarray(){
        byte[][] argb = new byte[width*height][pixelLength];
        for (int i = 0; i < pixels.length;) {
            if (hasAlphaChannel)
            {
                argb[i][3] = pixels[i];     // alpha
                argb[i][2] = pixels[i+1];   // blue
                argb[i][1] = pixels[i+2];   // green
                argb[i][0] = pixels[i+3];   // red
            }
            else {
                argb[i][2] = pixels[i];     // blue
                argb[i][1] = pixels[i+1];   // green
                argb[i][0] = pixels[i+2];   // red
            }
        }
        return argb;
    }


    //возвращает экземпляр LinkedHashSet с палитрой
    LinkedHashSet getRGBpalette(){
        LinkedHashSet palette = new LinkedHashSet();
        for (int pixel : pixels) {

        }
        return palette;
    }
}
