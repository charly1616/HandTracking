
package handtracking;

import java.awt.Image;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import java.util.Random;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

class CopyMakeBorderRun {
    
    public void run(String[] args) {
        // Declare the variables
        Mat src, dst = new Mat();
        VideoCapture captura = new VideoCapture(0);
        int top, bottom, left, right;
        int borderType = Core.BORDER_CONSTANT;
        String window_name = "copyMakeBorder Demo";
        Random rng;
        // Load an image
        src = new Mat();
        captura.read(src);
        // Check if image is loaded fine
        if (src.empty()) {
            System.out.println("Error opening image!");
            System.out.println("Program Arguments: [image_name -- default ../data/lena.jpg] \n");
            System.exit(-1);
        }
        // Brief how-to for this program
        System.out.println("\n"
                + "\t copyMakeBorder Demo: \n"
                + "\t -------------------- \n"
                + " ** Press 'c' to set the border to a random constant value \n"
                + " ** Press 'r' to set the border to be replicated \n"
                + " ** Press 'ESC' to exit the program \n");
        HighGui.namedWindow(window_name, HighGui.WINDOW_AUTOSIZE);
        // Initialize arguments for the filter
        top = (int) (0.05 * src.rows());
        bottom = top;
        left = (int) (0.05 * src.cols());
        right = left;
        while (true) {
            rng = new Random();
            Scalar value = new Scalar(rng.nextInt(256),
                    rng.nextInt(256), rng.nextInt(256));
            Core.copyMakeBorder(src, dst, top, bottom, left, right, borderType, value);
            HighGui.imshow(window_name, dst);
            char c = (char) HighGui.waitKey(500);
            c = Character.toLowerCase(c);
            if (c == 27) {
                break;
            } else if (c == 'c') {
                borderType = Core.BORDER_CONSTANT;
            } else if (c == 'r') {
                borderType = Core.BORDER_REPLICATE;
            }
        }
        System.exit(0);
    }
}

public class CopyMakeBorder {

    public static Mat border(Mat src) {

        final int MAX_LOW_THRESHOLD = 100;
        final int RATIO = 3;
        final int KERNEL_SIZE = 3;
        final Size BLUR_SIZE = new Size(3, 3);
        int lowThresh = 0;
        Mat srcBlur = new Mat();
        Mat detectedEdges = new Mat();
        Mat dst = new Mat();
        Image img = HighGui.toBufferedImage(src);
        Imgproc.blur(src, srcBlur, BLUR_SIZE);
        Imgproc.Canny(srcBlur, detectedEdges, lowThresh, lowThresh * RATIO, KERNEL_SIZE, false);
        dst = new Mat(src.size(), CvType.CV_8UC3, Scalar.all(0));
        src.copyTo(dst, detectedEdges);
        return dst;
    }

    public static Mat Rotate(Mat src, double angle) {
        Point[] srcTri = new Point[3];
        srcTri[0] = new Point(0, 0);
        srcTri[1] = new Point(src.cols() - 1, 0);
        srcTri[2] = new Point(0, src.rows() - 1);
        Point[] dstTri = new Point[3];
        dstTri[0] = new Point(0, src.rows() * 0.33);
        dstTri[1] = new Point(src.cols() * 0.85, src.rows() * 0.25);
        dstTri[2] = new Point(src.cols() * 0.15, src.rows() * 0.7);
        Mat warpMat = Imgproc.getAffineTransform(new MatOfPoint2f(srcTri), new MatOfPoint2f(dstTri));
        Mat warpDst = Mat.zeros(src.rows(), src.cols(), src.type());
        Imgproc.warpAffine(src, warpDst, warpMat, warpDst.size());
        Point center = new Point(warpDst.cols() / 2, warpDst.rows() / 2);
        double scale = 0.6;
        Mat rotMat = Imgproc.getRotationMatrix2D(center, angle, scale);
        Mat warpRotateDst = new Mat();
        Imgproc.warpAffine(warpDst, warpRotateDst, rotMat, warpDst.size());
        return warpDst;
    }

    public static Mat Laplace(Mat src) {
        Mat src_gray = new Mat(), dst = new Mat();
        int kernel_size = 3;
        int scale = 1;
        int delta = 0;
        int ddepth = CvType.CV_16S;
        Imgproc.GaussianBlur(src, src, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT);
        Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_RGB2GRAY);
        Mat abs_dst = new Mat();
        Imgproc.Laplacian(src_gray, dst, ddepth, kernel_size, scale, delta, Core.BORDER_DEFAULT);
        // converting back to CV_8U
        Core.convertScaleAbs(dst, abs_dst);
        return abs_dst;
    }

    public static Mat voltear(Mat src) {
        Mat dst = new Mat();
        for (int i = 0; i < src.cols(); i++) {
            for (int j = 0; j < src.rows(); j++) {
                dst.put(j, i, src.get(j, i));
            }
        }
        return dst;
    }

 
    public static void main(String[] args) {
        // Load the native library.
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new CopyMakeBorderRun().run(args);
    }
}
