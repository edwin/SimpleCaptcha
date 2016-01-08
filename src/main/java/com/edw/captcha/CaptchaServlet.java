/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edw.captcha;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author edwin < edwinkun at gmail dot com >
 */
@WebServlet(name = "Captcha", urlPatterns = {"/Captcha"})
public class CaptchaServlet extends HttpServlet {

    private final SecureRandom random = new SecureRandom();
    private final Color colorFrom=Color.WHITE;
    private final Color colorTo=Color.WHITE;
    private Color noiseColor;
    private float noiseWidth;
    private final int imgWidth = 100;
    private final int imgHeight = 32;

    private final List<Color> randomColorFont = new ArrayList<Color>();
    private final List<Color> randomColorNoise = new ArrayList<Color>();
    private final List<Color> randomColorDotNoise = new ArrayList<Color>();
    
    private final List<Font> randomFont = new ArrayList<Font>();
    private final RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
    private final GradientPaint ytow = new GradientPaint(0, 0, colorFrom, this.imgWidth, this.imgHeight, colorTo);
    private final Rectangle2D rectangle2D=new Rectangle2D.Double(0, 0, this.imgWidth, this.imgHeight);
    private static final double YOFFSET = 0.25;
    private static final double XOFFSET = 0.02;
    private final char[] RANDOM_CHAR="CDEFHJKLMNPQRUVWXZabcdefhijkmnpruvwxz2347".toCharArray();
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("image/png");
        
        try {
            HttpSession session = request.getSession();
        
            CaptchaResult result=(CaptchaResult)session.getAttribute("captcha");
            if(result==null){
                result=creataResult();
                session.setAttribute("captcha", result);
            }
            else{
                result.reset();
                generateResult(result);
            }

            OutputStream out = new BufferedOutputStream(response.getOutputStream());
            out.write(result.getBaos().toByteArray());
            
            out.close();
            out.flush();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    @Override
    public void init() {
        randomColorNoise.add(Color.WHITE);
        
        randomColorDotNoise.add(Color.YELLOW);
        randomColorDotNoise.add(Color.ORANGE);
        randomColorDotNoise.add(Color.BLUE);
        randomColorDotNoise.add(Color.GREEN);
        randomColorDotNoise.add(Color.GRAY);
        randomColorDotNoise.add(Color.LIGHT_GRAY);
        
        randomColorFont.add(Color.BLACK);
        randomColorFont.add(Color.RED);

        randomFont.add(new Font("Arial", Font.BOLD | Font.ITALIC, 23));
        randomFont.add(new Font("Arial", Font.BOLD, 23));
        randomFont.add(new Font("Arial", Font.ITALIC, 24));

        randomFont.add(new Font("Courier", Font.BOLD, 23));
        randomFont.add(new Font("Courier", Font.BOLD | Font.ITALIC, 24));
        randomFont.add(new Font("Courier", Font.ITALIC, 23));

        randomFont.add(new Font("Verdana", Font.BOLD, 21));
        randomFont.add(new Font("Verdana", Font.BOLD | Font.ITALIC, 21));
        randomFont.add(new Font("Verdana", Font.ITALIC, 21));
        
        hints.add(new RenderingHints(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY));
    }

    /**
     *
     * @return
     * @throws Throwable
     */
    public CaptchaResult creataResult()throws Throwable {
        CaptchaResult res = new CaptchaResult(getBackground());
        generateResult(res);
        return res;
    }

    /**
     *
     * @param res
     * @throws Throwable
     */
    public void generateResult(CaptchaResult res) throws Throwable{
        StringBuilder sb = res.getQuestion();

        int loop=random.nextBoolean()?(random.nextBoolean()?4:5):6;
        for(int i=0;i<loop;i++){
            sb.append(RANDOM_CHAR[random.nextInt(RANDOM_CHAR.length)]);
        }
        String generated = sb.toString();
        res.setAnswer(sb.toString());

        BufferedImage img=res.getImage();
        Graphics2D g = (Graphics2D)img.createGraphics();
        g.setRenderingHints(hints);                
        getBackground(res.getImage(),g);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        
        for(int i=0; i <4; i++)
            makeDotNoise(img, g);
        render(generated, img , g);
        for(int i=0; i <6; i++)
            makeNoise(img,g);
        
        g.dispose();
        ImageIO.write(img, "png", res.getBaos());
    }


    private BufferedImage getBackground() {
        BufferedImage img = new BufferedImage(this.imgWidth, this.imgHeight,
                BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g=img.createGraphics();
        g.setRenderingHints(hints);        
        getBackground(img,g);
        g.dispose();
        return img;
    }

    private BufferedImage getBackground(BufferedImage img,Graphics2D g) {
        g.setPaint(ytow);
        g.fill(rectangle2D);
        g.drawImage(img, 0, 0, null);
        return img;
    }
    
    private void makeDotNoise(BufferedImage image, Graphics2D g) {
        noiseWidth = (float)Math.random();

        g.setColor(randomColorDotNoise.get(random.nextInt(randomColorDotNoise.size())));
        g.fillOval((int)(imgWidth*Math.random()), (int)(imgHeight*Math.random()), (int)(15*noiseWidth), (int)(15*noiseWidth));
    }
    
    private void makeNoise(BufferedImage image,Graphics2D g) {
        noiseColor = randomColorNoise.get(random.nextInt(randomColorNoise.size()));
        noiseWidth = random.nextFloat();

        CubicCurve2D cc = new CubicCurve2D.Float(imgWidth * .1f, imgHeight
                * random.nextFloat(), imgWidth * .1f, imgHeight
                * random.nextFloat(), imgWidth * .25f, imgHeight
                * random.nextFloat(), imgWidth * .9f, imgHeight
                * random.nextFloat());

        PathIterator pi = cc.getPathIterator(null, 2);
        Point2D tmp[] = new Point2D[200];
        int i = 0;

        while (!pi.isDone()) {
            float[] coords = new float[6];
            switch (pi.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO:
                case PathIterator.SEG_LINETO:
                    tmp[i] = new Point2D.Float(coords[0], coords[1]);
            }
            i++;
            pi.next();
        }

        Point2D[] pts = new Point2D[i];
        System.arraycopy(tmp, 0, pts, 0, i);


        g.setColor(noiseColor);
        for (i = 0; i < pts.length - 1; i++) {
            if (i < 3) {
                g.setStroke(new BasicStroke(noiseWidth));
            }
            g.drawLine((int) pts[i].getX(), (int) pts[i].getY(),
                    (int) pts[i + 1].getX(), (int) pts[i + 1].getY());
        }
    }

    private void render(final String word, BufferedImage image , Graphics2D g) {
        FontRenderContext frc = g.getFontRenderContext();
        int xBaseline = (int) Math.round(image.getWidth() * XOFFSET);
        char[] chars = new char[1];
        
        for (char c : word.toCharArray()) {
            int yBaseline = image.getHeight() - (int) Math.round(image.getHeight() * YOFFSET);
        
            if (Character.isWhitespace(c)) {
                xBaseline = xBaseline + 2;
            } else {
                chars[0] = c;
                g.setColor(randomColorFont.get(random.nextInt(randomColorFont.size())));

                int choiceFont = random.nextInt(randomFont.size());
                Font font = randomFont.get(choiceFont);
                g.setFont(font);

                GlyphVector gv = font.createGlyphVector(frc, chars);
                g.drawChars(chars, 0, chars.length, xBaseline, yBaseline+random.nextInt(10)-random.nextInt(10));

                int width = (int) gv.getVisualBounds().getWidth();
                xBaseline += width;
                xBaseline += 3;
            }
        }
    }
    
}
