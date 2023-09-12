
package handtracking;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class Camara extends JFrame{
    
    //La pantalla de la camara
    private JLabel camaraScreen;
    private JButton btnCptura;
    
    private VideoCapture captura;
    private Mat imagen;
    private boolean clicked;
    
    
    
    //Constructor, se instancia la camara
    public Camara(){ 
        //diseño de la UI
        setLayout(null);
        camaraScreen = new JLabel();
        camaraScreen.setBounds(0,0,640,480);
        add(camaraScreen);
        
        btnCptura = new JButton("capturar");
        btnCptura.setBounds(250,500,100,40);
        add(btnCptura);
        
        btnCptura.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                clicked = true;
            }
        });
        
        
        this.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                super.windowClosing(e);
                captura.release();
                imagen.release();
                System.exit(0);
            }
        });
        
        
        this.setSize(new Dimension(600,600));
        setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    
    
    
    
    //Guardar la imagen
    public void startCamera(){ 
        captura = new VideoCapture(0);
        imagen = new Mat();
        byte[] DataImagen;
        
        
        ImageIcon icon;
        while (true){
            // leer la imagen
            
            captura.read(imagen);
            
            
            
            imagen = CopyMakeBorder.voltear(imagen);
            
            //Convertir la matriz en byte
            final MatOfByte but = new MatOfByte();
            Imgcodecs.imencode(".jpg", imagen, but);
            DataImagen = but.toArray();
            
            //Añadir esto al JLabel
            icon = new ImageIcon(DataImagen);
            camaraScreen.setIcon(icon);
            
            if (clicked){
                String name = JOptionPane.showInputDialog(this,"Coloca un facking nombre para la imagen");
                if (name == null){
                    name = new SimpleDateFormat("yyyy-mm-dd-hh-mm-ss").format(new Date());
                }
                //Escribir en archivopsss
                Imgcodecs.imwrite("images/"+name+".jpg", imagen);
                clicked = false;
            }
        }
    }
    
    
    
    //Ejecutable, se crea una camara para ejecutarla
    public static void main(String[] args) {
        // Load the native library.
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        VideoCapture camera = new VideoCapture(0); // 0 represents the default camera (change if needed)
        if (!camera.isOpened()) {
            System.out.println("Camera not found!");
            return;
        }

        Mat frame = new Mat();
        Mat hsvFrame = new Mat();
        Mat handMask = new Mat();
        Scalar lowerBound = new Scalar(0, 20, 70);
        Scalar upperBound = new Scalar(20, 255, 255);

        while (true) {
            camera.read(frame);

            if (frame.empty()) {
                System.out.println("End of video");
                break;
            }

            Imgproc.cvtColor(frame, hsvFrame, Imgproc.COLOR_BGR2HSV);
            Core.inRange(hsvFrame, lowerBound, upperBound, handMask);

            // You can perform additional image processing here to refine the hand detection

            // Find contours in the hand mask
            Mat hierarchy = new Mat();
            List<MatOfPoint> contours = new ArrayList<>();
            Imgproc.findContours(handMask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            // Draw contours on the original frame
            Imgproc.drawContours(frame, contours, -1, new Scalar(0, 0, 255), 2);

            HighGui.imshow("Hand Detection", frame);
            if (HighGui.waitKey(10) == 27) {
                System.out.println("Esc key pressed. Exiting...");
                break;
            }
        }

        camera.release();
        HighGui.destroyAllWindows();
    
        
        
        
        
        
        
//        EventQueue.invokeLater(new Runnable(){
//            @Override
//            public void run(){
//                Camara camara = new Camara();
//                
//                new Thread(new Runnable(){
//                    @Override
//                    public void run(){
//                        camara.startCamera();
//                    }
//                }).start();
//            }
//        });
    }
    
}
