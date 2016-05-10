package com.spouzee;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by Sagar on 3/7/2016.
 */
public class ImageResizeTest {

    private static byte[] resizeImage(byte[] originalImage, String imageType, int width, int height) throws IOException {
        BufferedImage originalBufferedImage = ImageIO.read(new ByteArrayInputStream(originalImage));
        int type = originalBufferedImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalBufferedImage.getType();

        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalBufferedImage, 0, 0, width, height, null);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write( resizedImage, imageType, baos );
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();

        return imageInByte;
    }

    public static void main1(String[] args) throws IOException {
        FileInputStream fis = new FileInputStream("C:\\Users\\Sagar\\Desktop\\out.jpg");
        byte[] arig = new byte[fis.available()];
        fis.read(arig);
        byte[] modified = resizeImage(arig, "jpg", 100, 100);
        FileOutputStream fos = new FileOutputStream("C:\\Users\\Sagar\\Desktop\\out2");
        fos.write(modified);
        fos.flush();
        fos.close();
    }

    public static void main(String[] args) {
        String in = "image/jpeg";
        System.out.println(in.split("/")[1]);
    }
}
