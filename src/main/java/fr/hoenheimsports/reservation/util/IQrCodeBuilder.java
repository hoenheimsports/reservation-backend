package fr.hoenheimsports.reservation.util;

import com.google.zxing.WriterException;

import java.io.IOException;

public interface IQrCodeBuilder {

    public String createQrCodeBase64(String data) throws WriterException, IOException;
}
