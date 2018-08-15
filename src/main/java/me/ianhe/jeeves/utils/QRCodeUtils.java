package me.ianhe.jeeves.utils;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import me.ianhe.jeeves.domain.BufferedImageLuminanceSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;

/**
 * @author iHelin
 * @since 2018/8/13 20:15
 */
public class QRCodeUtils {

    private static final Logger logger = LoggerFactory.getLogger(QRCodeUtils.class);

    /**
     * 解析二维码中的文字信息
     *
     * @param input
     * @return
     * @throws IOException
     * @throws NotFoundException
     */
    public static String decode(InputStream input, String uuid)
            throws IOException {
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(
                        ImageIO.read(input))));
        Result qrCodeResult;
        try {
            qrCodeResult = new MultiFormatReader().decode(binaryBitmap);
            return qrCodeResult.getText();
        } catch (NotFoundException e) {
            logger.error("解析二维码中的文字信息出错", e);
            return "https://login.weixin.qq.com/l/" + uuid;
        }
    }

    public static String generateQR(String text, Boolean ide, int width, int height) throws WriterException {
        Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);
        hintMap.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8);
        hintMap.put(EncodeHintType.MARGIN, 1);
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hintMap);
        return toAscii(bitMatrix, ide);
    }

    private static String toAscii(BitMatrix bitMatrix, Boolean ide) {
        StringBuilder builder = new StringBuilder();
        for (int r = 0; r < bitMatrix.getHeight(); r++) {
            for (int c = 0; c < bitMatrix.getWidth(); c++) {
                if (ide) {
                    if (bitMatrix.get(r, c)) {
                        builder.append("\033[47m  \033[0m");
                    } else {
                        builder.append("\033[40m  \033[0m");
                    }
                } else {
                    if (!bitMatrix.get(r, c)) {
                        builder.append("\033[47m  \033[0m");
                    } else {
                        builder.append("\033[40m  \033[0m");
                    }
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
