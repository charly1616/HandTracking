
package handtracking;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
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
        
        double angle = 0;
        
        ImageIcon icon;
        while (true){
            // leer la imagen
            
            captura.read(imagen);
            angle += 0.00001;
            
            imagen = CopyMakeBorder.Rotate(imagen, angle);
            
            
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
        EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                Camara camara = new Camara();
                
                new Thread(new Runnable(){
                    @Override
                    public void run(){
                        camara.startCamera();
                    }
                }).start();
            }
        });
    }
    
}
