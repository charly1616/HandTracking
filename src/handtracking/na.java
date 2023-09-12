/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package handtracking;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;
import org.opencv.core.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Moments;

public class na extends JFrame {

    private JLabel camaraScreen;
    private JButton btnCaptura;

    private VideoCapture captura;
    private Mat imagen;
    private boolean clicked;

    public na() {
        // Diseño de la UI
        setLayout(null);
        camaraScreen = new JLabel();
        camaraScreen.setBounds(0, 0, 640, 480);
        add(camaraScreen);

        btnCaptura = new JButton("Capturar");
        btnCaptura.setBounds(250, 500, 100, 40);
        add(btnCaptura);

        btnCaptura.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clicked = true;
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                captura.release();
                imagen.release();
                System.exit(0);
            }
        });

        this.setSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void startCamera() {
        captura = new VideoCapture(0);
        imagen = new Mat();
        byte[] dataImagen;

        ImageIcon icon;
        while (true) {
            // Leer la imagen
            captura.read(imagen);

            // Convertir a escala de grises
            Mat gris = new Mat();
            Imgproc.cvtColor(imagen, gris, Imgproc.COLOR_BGR2GRAY);

            // Aplicar desenfoque gaussiano
            Imgproc.GaussianBlur(gris, gris, new Size(7, 7), 0);

            // Realizar umbralización para obtener una imagen binaria
            Mat binaria = new Mat();
            Imgproc.threshold(gris, binaria, 128, 255, Imgproc.THRESH_BINARY);

            // Encontrar contornos
            List<MatOfPoint> contornos = new ArrayList<>();
            Imgproc.findContours(binaria, contornos, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            int dedos = 0;

            for (MatOfPoint contorno : contornos) {
                double area = Imgproc.contourArea(contorno);

                // Filtrar contornos por tamaño
                if (area > 1000) {
                    dedos++;

                    // Encontrar el centro del contorno
                    Moments momentos = Imgproc.moments(contorno);
                    int x = (int) (momentos.get_m10() / momentos.get_m00());
                    int y = (int) (momentos.get_m01() / momentos.get_m00());

                    // Dibujar un círculo en el centro del dedo
                    Imgproc.circle(imagen, new Point(x,y), 10, new Scalar(0, 0, 255), -1);
                }
            }

            // Dibujar el número de dedos detectados
            Imgproc.putText(imagen, "Dedos: " + dedos, new Point(10, 30), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 255), 2);

            // Mostrar la imagen con los dedos detectados
            icon = new ImageIcon(HighGui.toBufferedImage(imagen));
            camaraScreen.setIcon(icon);

            if (clicked) {
                String nombre = JOptionPane.showInputDialog(this, "Ingresa un nombre para la imagen");
                if (nombre == null) {
                    nombre = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
                }
                // Guardar la imagen en un archivo
                Imgcodecs.imwrite("images/" + nombre + ".jpg", imagen);
                clicked = false;
            }
        }
    }

    public static void main(String[] args) {
        // Cargar la biblioteca nativa de OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        EventQueue.invokeLater(() -> {
            Camara camara = new Camara();

            new Thread(() -> {
                camara.startCamera();
            }).start();
        });
    }
}
