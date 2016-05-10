package com.spouzee.server.api.util;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.Iterator;

/**
 * Created by Sagar on 4/21/2016.
 */
public class ImageUtils {

    public static byte[] compressImage(InputStream inputStream, String fileSuffix, float quality) throws IOException
    {
        BufferedImage image = ImageIO.read(inputStream);
        // Get a ImageWriter for jpeg format.
        Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix(fileSuffix);
        if (!writers.hasNext()) throw new IllegalStateException("No writers found");
        ImageWriter writer = (ImageWriter) writers.next();
        // Create the ImageWriteParam to compress the image.
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);
        // The output will be a ByteArrayOutputStream (in memory)
        ByteArrayOutputStream bos = new ByteArrayOutputStream(32768);
        ImageOutputStream ios = ImageIO.createImageOutputStream(bos);
        writer.setOutput(ios);
        writer.write(null, new IIOImage(image, null, null), param);
        ios.flush(); // otherwise the buffer size will be zero!
        return bos.toByteArray();
        // From the ByteArrayOutputStream create a RenderedImage.
        /*ByteArrayInputStream in = new ByteArrayInputStream(bos.toByteArray());
        RenderedImage out = ImageIO.read(in);
        int size = bos.toByteArray().length;
        //showImage("Compressed to " + quality + ": " + size + " bytes", out);
        // Uncomment code below to save the compressed files.
        File file = new File("C:/projects/better_place/Dumps/compressed."+quality+".jpeg");
        FileImageOutputStream output = new FileImageOutputStream(file);
        writer.setOutput(output); writer.write(null, new IIOImage(image, null,null), param);*/
    }

}
