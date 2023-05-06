package fr.hoenheimsports.reservation.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class QrCodeBuilder implements IQrCodeBuilder {

    public String createQrCodeBase64(String data) throws WriterException, IOException {

        return Base64.getEncoder().encodeToString(this.createQrCode(data));
    }

    public byte[] createQrCode(String data) throws WriterException, IOException {
        BitMatrix bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, 300, 300);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // Convertir l'image en une chaine Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "png", baos);
        byte[] bytes = baos.toByteArray();

        return bytes;
    }

}

