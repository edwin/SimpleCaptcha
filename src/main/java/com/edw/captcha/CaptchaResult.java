package com.edw.captcha;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

/**
 * <pre>
 *  com.edw.captcha.CaptchaResult
 * </pre>
 *
 * @author edwin < edwinkun at gmail dot com >
 * Jan 8, 2016 11:06:32 AM
 *
 */
public class CaptchaResult {

    private final StringBuilder question = new StringBuilder(32);
    private String answer;
    private final BufferedImage image;
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);

    /**
     *
     * @param image
     */
    public CaptchaResult(BufferedImage image) {
        this.image = image;
    }

    /**
     *
     * @return
     */
    public String getAnswer() {
        return answer;
    }

    /**
     *
     * @param answer
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     *
     * @return
     */
    public StringBuilder getQuestion() {
        return question;
    }

    /**
     *
     * @return
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     *
     * @return
     */
    public ByteArrayOutputStream getBaos() {
        return baos;
    }

    /**
     *
     */
    public void reset() {
        answer = "";
        question.setLength(0);
        baos.reset();
    }
}
