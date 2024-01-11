package org.nervousync.beans.barcode;

import java.awt.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.nervousync.commons.Globals;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jul 13, 2016 4:41:24 PM $
 */
public final class CodeOptions {

    private final BarcodeFormat barcodeFormat;
    private final int codeWidth;
    private final int codeHeight;
    private String fileFormat = "PNG";
    private String encoding = Globals.DEFAULT_ENCODING;
    private Color onColor = new Color(0, 0, 0);
    private Color offColor = new Color(255, 255, 255);
    private ErrorCorrectionLevel errorLevel = ErrorCorrectionLevel.L;

    public CodeOptions(int codeWidth, int codeHeight) {
        this.barcodeFormat = BarcodeFormat.QR_CODE;
        this.codeWidth = codeWidth;
        this.codeHeight = codeHeight;
    }

    public CodeOptions(BarcodeFormat barcodeFormat, int codeWidth, int codeHeight) {
        this.barcodeFormat = barcodeFormat;
        this.codeWidth = codeWidth;
        this.codeHeight = codeHeight;
    }

    /**
     * @return the barcodeFormat
     */
    public BarcodeFormat getBarcodeFormat() {
        return barcodeFormat;
    }

    /**
     * @return the codeWidth
     */
    public int getCodeWidth() {
        return codeWidth;
    }

    /**
     * @return the codeHeight
     */
    public int getCodeHeight() {
        return codeHeight;
    }

    /**
     * @return the fileFormat
     */
    public String getFileFormat() {
        return fileFormat;
    }

    /**
     * @param fileFormat the fileFormat to set
     */
    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    /**
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @param encoding the encoding to set
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * @return the onColor
     */
    public Color getOnColor() {
        return onColor;
    }

    /**
     * @param onColor the onColor to set
     */
    public void setOnColor(Color onColor) {
        this.onColor = onColor;
    }

    /**
     * @return the offColor
     */
    public Color getOffColor() {
        return offColor;
    }

    /**
     * @param offColor the offColor to set
     */
    public void setOffColor(Color offColor) {
        this.offColor = offColor;
    }

    /**
     * @return the errorLevel
     */
    public ErrorCorrectionLevel getErrorLevel() {
        return errorLevel;
    }

    /**
     * @param errorLevel the errorLevel to set
     */
    public void setErrorLevel(ErrorCorrectionLevel errorLevel) {
        this.errorLevel = errorLevel;
    }
}
