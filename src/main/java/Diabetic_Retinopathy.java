
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.rosuda.JRI.Rengine;

/**
 * DiabeticRetinopathy
 *
 * A template for processing each pixel of either GRAY8, GRAY16, GRAY32 or
 * COLOR_RGB images.
 *
 * @author The Fiji Team
 */
public class Diabetic_Retinopathy implements PlugIn { //PlugInFilter {

    protected ImagePlus image;

    // image property members
    private int width;
    private int height;

    // plugin parameters
    public double value;
    public String name;

    public List<ImageHelper> ImagesHelperList;

    public Diabetic_Retinopathy() {

        this.ImagesHelperList = new ArrayList<ImageHelper>();

    }

    public void loadCSVFile(String csvFile) {

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        String imageFnTitle = "";
        String DRlevelTitle = "";

        try {

            br = new BufferedReader(new FileReader(csvFile));
            int lineCntr = 0;

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] parsedLine = line.split(cvsSplitBy);

                // Extract the titles
                if (lineCntr == 0) {
                    imageFnTitle = parsedLine[0];
                    DRlevelTitle = parsedLine[1];
                } else {

                    //System.out.println(imageFnTitle + ": " + parsedLine[0]
                    //        + " ," + DRlevelTitle + ": " + parsedLine[1]);
                    // Create an image helper for this file
                    ImageHelper s = new ImageHelper(parsedLine[0], Integer.parseInt(parsedLine[1]));
                    ImagesHelperList.add(s);

                }
                lineCntr++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.printf("Number of files referenced in file = %d\n", ImagesHelperList.size());

    }
    
    public int findMode(int[] array ){
        int mode = 0;
        int modecont = 0;
        for(int i=0 ; i < array.length ; i++){
            if( array[i] > modecont){
                mode = i;
                modecont = array[i];
            }
        }
        return mode;
    }
    
    public int findMax(int[] array){
        int max=0;
        for(int i = array.length - 1; i >= 0; i-- ){
            if(array[i] > 0)
                return i;
        }
        return -1; 
    }
    
    public int Integral(int[] array, int min, int max){
        int sum = 0;
        if(min >= 0 && max < array.length){
            for(int i = min; i <= max; i++){
                sum = sum + array[i];
            }
        }
        return sum;
    }
    
    
    public double TailToMode(int[] array, int max, int mode){
        int ten = (int) (max*0.1);
        int tailint = Integral(array, max - ten, max);
        int modeint = array[mode];
        return tailint/modeint;
        //return array[max]/array[mode];
    }
    
    public int[][] getDerivative(int[][] hist){
        int [][] output = new int[4][256];
        for(int i=0;i<4;i++){
            for(int j=0;j<255;j++){
                output[i][j] = hist[i][j+1] - hist[i][j];
            }
        }
        return output;
    }
    
    public int[][] getHistograms(String fn, double backgSubsRolling){
        ImagePlus image = IJ.openImage(fn);
        image.show();
        //String bgSubtraction = "rolling=" + backgSubsRolling + "  stack";
        //IJ.run("Subtract Background...", bgSubtraction);
        
        ImageProcessor ip;
        ip = image.getProcessor();
        int[] histogram = ip.getHistogram();
        int[] histogramR = new int[256];
        int[] histogramG = new int[256];
        int[] histogramB = new int[256];
        int[][] output = new int[4][256];
        
        int[][] img_matrix = ip.getIntArray();
        int sizex = img_matrix.length;
        int sizey = img_matrix[0].length;
      
        int B,G,R;
        
        for(int i = 0; i< sizex; i ++){
            for(int j = 0; j< sizey; j ++){
                B = img_matrix[i][j] & 0xFF;
                G = img_matrix[i][j] >> 8 & 0xFF;
                R = img_matrix[i][j] >> 16 & 0xFF;
                histogramR[R]++;
                histogramG[G]++;
                histogramB[B]++;
            }
        }
        output[0] = histogram;
        output[1] = histogramR;
        output[2] = histogramG;
        output[3] = histogramB;
        IJ.run(image, "Close All", ""); 
        return output;
    }
    
    public double[] getHistogramsRatios(String fn, double backgSubsRolling){
        ImagePlus image = IJ.openImage(fn);
        image.show();
        String bgSubtraction = "rolling=" + backgSubsRolling + "  stack";
        IJ.run("Subtract Background...", bgSubtraction);
        
        ImageProcessor ip;
        ip = image.getProcessor();
        int[] histogram = ip.getHistogram();
        int[] histogramR = new int[256];
        int[] histogramG = new int[256];
        int[] histogramB = new int[256];
        double[] output = new double[4];
        
        int[][] img_matrix = ip.getIntArray();
        int sizex = img_matrix.length;
        int sizey = img_matrix[0].length;
      
        int B,G,R;
        
        for(int i = 0; i< sizex; i ++){
            for(int j = 0; j< sizey; j ++){
                B = img_matrix[i][j] & 0xFF;
                G = img_matrix[i][j] >> 8 & 0xFF;
                R = img_matrix[i][j] >> 16 & 0xFF;
                histogramR[R]++;
                histogramG[G]++;
                histogramB[B]++;
            }
        }

        int modehist = findMode(histogram);
        int maxhist = findMax(histogram);
        output[0] = TailToMode(histogram, maxhist, modehist);
        modehist = findMode(histogramR);
        maxhist = findMax(histogramR);
        output[1] = TailToMode(histogramR, maxhist, modehist);
        modehist = findMode(histogramG);
        maxhist = findMax(histogramG);
        output[2] = TailToMode(histogramG, maxhist, modehist);
        modehist = findMode(histogramB);
        maxhist = findMax(histogramB);
        output[3] = TailToMode(histogramB, maxhist, modehist);
        IJ.run(image, "Close All", ""); 
        return output;
    }

    public double getNormalisedArea(String fn, double backgSubsRolling){
    //public double getNormalisedArea(ImagePlus image){
        double area = 0, areaeye = 0;
        ImagePlus image = IJ.openImage(fn);
        image.show();
        //String [] split;
        //split = fn.split("/");
        //IJ.run(image, "Split Channels", "");
        //IJ.run(image, "Images to Stack", "name=Stack title="+ split[5] + " use");
        //image = IJ.getImage();
        //backgSubsRolling = 1000;
        String bgSubtraction = "rolling=" + backgSubsRolling + "  stack";
        IJ.run("Subtract Background...", bgSubtraction);
        
        ImageProcessor ip;
        ip = image.getProcessor();

        int[][] img_matrix = ip.getIntArray();
        int sizex = img_matrix.length;
        int sizey = img_matrix[0].length;
      
        int halfy = sizey/2;
        //int halfx = sizex/2;
        int prevR = -1, prevB = -1, prevG = -1;
        boolean first = false;
        int firstp = 0;
        int lastp = 0;
        for(int i = 0; i< sizex; i ++){
      
            int B = img_matrix[i][halfy] & 0xFF;
            int G = img_matrix[i][halfy] >> 8 & 0xFF;
            int R = img_matrix[i][halfy] >> 16 & 0xFF;
      
            if(i == 0 && (R != 1 && G != 1 && B != 1) ){
                prevR = R;
                prevB = B;
                prevG = G;
                System.out.println("Starting value: " + R + ", " + G + ", " + B + " At pixel " + i + ", " + halfy);
            }
            else{
                if( (R != prevR && G != prevG && B != prevB ) && (R != 1 && G != 1 && B != 1)){
                    if(!first){
                       firstp = i;
                       first = true;
                    }
                    lastp = i ;
                }
                prevR = R;
                prevB = B;
                prevG = G;
            } 
        }
        int radius = ((lastp - firstp)/2);
        areaeye = radius*radius*3.1415 ;
        System.out.println( "First pixel " + firstp + ", last pixel: " + lastp + ", Area of the eye: " + areaeye);
        IJ.run(image, "16-bit", "");
        IJ.setAutoThreshold(image, "MaxEntropy dark");
        IJ.run(image,"Convert to Mask","");
        //IJ.run(image, "Auto Threshold","method=MaxEntropy");
        //ip.autoThreshold();
        //ip.setAutoThreshold(AutoThresholder.Method.MaxEntropy, false);
        
        //IJ.run(image, "Auto Threshold...", "method=MaxEntropy ignore_black ignore_white");
        ip = image.getProcessor();
        img_matrix = ip.getIntArray();
        sizex = img_matrix.length;
        sizey = img_matrix[0].length;
        for(int i = 0 ; i < sizex; i++){
            for(int j=0 ; j < sizey; j++){
                if(img_matrix[i][j] > 250)
                    area++;
            }
        }
        double percentage = 100*area/areaeye;
        System.out.println( "Area of the optical nerve: " + area + ", percentage: " + percentage);
        //IJ.run(image, "Close All", "");        
        return percentage;
    }

    public double[] getIntegralAboveBackground(String fn, double backgSubsRolling, double kfactor) {
    //public double[] getIntegralAboveBackground(ImagePlus image, double backgSubsRolling, double kfactor) {

        // open
        ImagePlus image = IJ.openImage(fn);

        //int sizey = img_matrix[].length;
        //ip.rotate( 45 );
        //imp
        image.show();
        
        String bgSubtraction = "rolling=" + backgSubsRolling + "  stack";
        IJ.run("Subtract Background...", bgSubtraction);
        //IJ.run("Median...", "radius=1");

        ImageProcessor ip = image.getProcessor();
        int[][] img_matrix = ip.getIntArray();
        int sizex = img_matrix.length;
        int sizey = img_matrix[0].length;
        int imageSize = sizex * sizey;

        // Before calculating the integrals I need to limit the measurement
        //  to a resonable radius.  Please find the radius of the image, i.e. the edge.
        // Calculate the average inside the selected radius
        double aR = 0, aG = 0, aB = 0;
        int pixelsConsidered = 0;
        for (int i = 0; i < sizex; i++) {
            for (int j = 0; j < sizey; j++) {

                int B = img_matrix[i][j] & 0xFF;
                int G = img_matrix[i][j] >> 8 & 0xFF;
                int R = img_matrix[i][j] >> 16 & 0xFF;
                //System.out.printf("R:%d,G:%d,B:%d\n", R, G, B);
                if (R != 0 && G != 0 && B != 0) {
                    aR += R;
                    aG += G;
                    aB += B;
                    pixelsConsidered++;
                }

            }
        }

        aR /= pixelsConsidered;
        aG /= pixelsConsidered;
        aB /= pixelsConsidered;

        // Integral over the average
        double inR = 0, inG = 0, inB = 0;
        for (int i = 0; i < sizex; i++) {
            for (int j = 0; j < sizey; j++) {
                int B = img_matrix[i][j] & 0xFF;
                int G = img_matrix[i][j] >> 8 & 0xFF;
                int R = img_matrix[i][j] >> 16 & 0xFF;

                if (R != 0 && G != 0 && B != 0) {

                    if (R > aR * kfactor) {
                        inR += R;
                    }
                    if (G > aG * kfactor) {
                        inG += G;
                    }
                    if (B > aB * kfactor) {
                        inB += B;
                    }

                }
            }
        }

        System.out.println("sizex : " + sizex + ", sizey = " + sizey);
        System.out.printf("Average  R,G,B = %.1f, %.1f, %.1f\n", aR, aG, aB);
        System.out.printf("Integral R,G,B = %.1f, %.1f, %.1f\n", inR, inG, inB);

        double[] inVector = {(inR + inG + inB) / 3, inG, inB};

        IJ.run(image, "Close All", "");
        return inVector;
    }

    /**
     * @see ij.plugin.filter.PlugInFilter#setup(java.lang.String, ij.ImagePlus)
     */
    /*
     @Override
     public int setup(String arg, ImagePlus imp) {
     if (arg.equals("about")) {
     showAbout();
     return DONE;
     }

     image = imp;
     return DOES_8G | DOES_16 | DOES_32 | DOES_RGB;
     }
     */
    /**
     * @see ij.plugin.filter.PlugInFilter#run(ij.process.ImageProcessor)
     */
    @Override
    public void run(String arg) { // run(ImageProcessor ip) {

        System.out.println("Run ...\n");
        String datapath = "/home/aocampor/DiabeticRetinophaty/sample/";
        //String datapath = "/media/aocampor/MyDisk/DiabeticRetinophaty/train/";
        loadCSVFile("/home/aocampor/DiabeticRetinophaty/trainLabels.csv");

        int setSize = ImagesHelperList.size();

        // prepare R plot
        int nImagesProcess = 10;

        double[] levV = new double[nImagesProcess];
        double[] intRV = new double[nImagesProcess];
        double[] areaN = new double[nImagesProcess];
        
        double[] historatio = new double[nImagesProcess];
        
        double[] historatioR = new double[nImagesProcess];
        double[] historatioG = new double[nImagesProcess];
        double[] historatioB = new double[nImagesProcess];
        
        int[] histogram = new int[256];
        int[] histogramR = new int[256];
        int[] histogramG = new int[256];
        int[] histogramB = new int[256];
        int[][][] histos = new int[nImagesProcess][4][256];
        int[][][] derhistos = new int[nImagesProcess][4][256];
        int[][][] der2histos = new int[nImagesProcess][4][256];
        
        for (int cntr = 0; cntr < nImagesProcess; cntr++) {

            ImageHelper oneImageHelper = ImagesHelperList.get(cntr);
            String fn = datapath + oneImageHelper.getFilenamePrefix() + ".jpeg";
            System.out.printf("-- %d -- ", cntr);
            System.out.printf("%s | DRlevel = %d\n", fn, oneImageHelper.getDRLevel());
            
            double[] inVector; 
            //inVector = getIntegralAboveBackground(fn, 100, 2.0);
            double OpticalNerveArea ;
            //OpticalNerveArea = getNormalisedArea(fn, 1000);
            double[] temp = new double[4];
            //int[][] histos = new int[4][256];
            //temp = getHistogramsRatios(fn, 100);    
            //historatio[cntr] = temp[0];
            //historatioR[cntr] = temp[1];
            //historatioG[cntr] = temp[2];
            //historatioB[cntr] = temp[3];
            histos[cntr] = getHistograms(fn, 100);        
            //derhistos = getDerivative(histos);
            levV[cntr] = oneImageHelper.getDRLevel();
            //intRV[cntr] = inVector[0];
            //areaN[cntr] = OpticalNerveArea;
        }

        // R
        String[] R_args = {"--no-save"};
        Rengine re = new Rengine(R_args, false, null);
        
        long levV3 = re.rniPutDoubleArray(levV);
        long intRV3 = re.rniPutDoubleArray(intRV);
        long areaV3 = re.rniPutDoubleArray(areaN);
        long histV3 = re.rniPutDoubleArray(historatio);
        long histRV3 = re.rniPutDoubleArray(historatioR);
        long histGV3 = re.rniPutDoubleArray(historatioG);
        long histBV3 = re.rniPutDoubleArray(historatioB);
        
        re.rniAssign("drlevel", levV3, 0);
        re.rniAssign("var", intRV3, 0);
        re.rniAssign("areaper", areaV3, 0);
        re.rniAssign("IntenRatio", histV3, 0);
        re.rniAssign("IntenRatioR", histRV3, 0);
        re.rniAssign("IntenRatioG", histGV3, 0);
        re.rniAssign("IntenRatioB", histBV3, 0);
        
        for(int i = 0 ; i < nImagesProcess;i++){
            histos[i][0][0] = 0;
            histos[i][1][0] = 0;
            histos[i][2][0] = 0;
            histos[i][3][0] = 0;
            derhistos[i] = getDerivative(histos[i]);
            der2histos[i] = getDerivative(derhistos[i]);
            re.assign("HistoLumi"+i, histos[i][0]);
            re.assign("HistoLumiR"+i, histos[i][1]);
            re.assign("HistoLumiG"+i, histos[i][2]);
            re.assign("HistoLumiB"+i, histos[i][3]);
            re.assign("DerHistoLumi"+i, derhistos[i][0]);
            re.assign("DerHistoLumiR"+i, derhistos[i][1]);
            re.assign("DerHistoLumiG"+i, derhistos[i][2]);
            re.assign("DerHistoLumiB"+i, derhistos[i][3]);
            re.assign("Der2HistoLumi"+i, der2histos[i][0]);
            re.assign("Der2HistoLumiR"+i, der2histos[i][1]);
            re.assign("Der2HistoLumiG"+i, der2histos[i][2]);
            re.assign("Der2HistoLumiB"+i, der2histos[i][3]);
             
        // Plot
        re.eval("jpeg('/home/aocampor/RPlots/histoL"+i+".jpg')");
        re.eval("plot(HistoLumi"+i+")");        
        re.eval("dev.off()");
        re.eval("jpeg('/home/aocampor/RPlots/histoLR"+i+".jpg')");
        re.eval("plot(HistoLumiR"+i+")");        
        re.eval("dev.off()");
        re.eval("jpeg('/home/aocampor/RPlots/histoLG"+i+".jpg')");
        re.eval("plot(HistoLumiG"+i+")");        
        re.eval("dev.off()");
        re.eval("jpeg('/home/aocampor/RPlots/histoLB"+i+".jpg')");
        re.eval("plot(HistoLumiB"+i+")");        
        re.eval("dev.off()");
        
        re.eval("jpeg('/home/aocampor/RPlots/DerhistoL"+i+".jpg')");
        re.eval("plot(DerHistoLumi"+i+")");        
        re.eval("dev.off()");
        re.eval("jpeg('/home/aocampor/RPlots/DerhistoLR"+i+".jpg')");
        re.eval("plot(DerHistoLumiR"+i+")");        
        re.eval("dev.off()");
        re.eval("jpeg('/home/aocampor/RPlots/DerhistoLG"+i+".jpg')");
        re.eval("plot(DerHistoLumiG"+i+")");        
        re.eval("dev.off()");
        re.eval("jpeg('/home/aocampor/RPlots/DerhistoLB"+i+".jpg')");
        re.eval("plot(DerHistoLumiB"+i+")");        
        re.eval("dev.off()");
        
                re.eval("jpeg('/home/aocampor/RPlots/Der2histoL"+i+".jpg')");
        re.eval("plot(Der2HistoLumi"+i+")");        
        re.eval("dev.off()");
        re.eval("jpeg('/home/aocampor/RPlots/Der2histoLR"+i+".jpg')");
        re.eval("plot(Der2HistoLumiR"+i+")");        
        re.eval("dev.off()");
        re.eval("jpeg('/home/aocampor/RPlots/Der2histoLG"+i+".jpg')");
        re.eval("plot(Der2HistoLumiG"+i+")");        
        re.eval("dev.off()");
        re.eval("jpeg('/home/aocampor/RPlots/Der2histoLB"+i+".jpg')");
        re.eval("plot(Der2HistoLumiB"+i+")");        
        re.eval("dev.off()");
        }
        System.out.print("Done");
        //re.eval("jpeg('/home/aocampor/idarraga.jpg')");
        //re.eval("plot(drlevel, var)");
        //re.eval("dev.off()");
        //re.eval("jpeg('/home/aocampor/ocampo.jpg')");
        //re.eval("plot(drlevel, areaper)");
        //re.eval("dev.off()");
        //re.eval("jpeg('/home/aocampor/IntenRatio.jpg')");
        //re.eval("plot(drlevel, IntenRatio)");
        //re.eval("dev.off()");
        //re.eval("jpeg('/home/aocampor/IntenRatioR.jpg')");
        //re.eval("plot(drlevel, IntenRatioR)");
        //re.eval("dev.off()");
        //re.eval("jpeg('/home/aocampor/IntenRatioG.jpg')");
        //re.eval("plot(drlevel, IntenRatioG)");
        //re.eval("dev.off()");
        //re.eval("jpeg('/home/aocampor/IntenRatioB.jpg')");
        //re.eval("plot(drlevel, IntenRatioB)");
        //re.eval("dev.off()");

        //showBackground("/home/idarraga/storage/Diabetic_Retinopathy_Detection/sample/10_left.jpeg");
        //showBackground("/home/idarraga/storage/Diabetic_Retinopathy_Detection/sample/13_left.jpeg");
        //showBackground("/home/idarraga/storage/Diabetic_Retinopathy_Detection/sample/17_left.jpeg");
        //showBackground("/home/idarraga/storage/Diabetic_Retinopathy_Detection/sample/15_left.jpeg");
        //showBackground("/home/idarraga/storage/Diabetic_Retinopathy_Detection/sample/16_left.jpeg");

        /*
         // get width and height
         width = ip.getWidth();
         height = ip.getHeight();

         if (showDialog()) {
         process(ip);
         image.updateAndDraw();
         }
         */
    }

    private boolean showDialog() {
        GenericDialog gd = new GenericDialog("Process pixels");

        // default value is 0.00, 2 digits right of the decimal point
        gd.addNumericField("value", 0.00, 2);
        gd.addStringField("name", "John");

        gd.showDialog();
        if (gd.wasCanceled()) {
            return false;
        }

        // get entered values
        value = gd.getNextNumber();
        name = gd.getNextString();

        return true;
    }

    /**
     * Process an image.
     *
     * Please provide this method even if {@link ij.plugin.filter.PlugInFilter}
     * does require it; the method
     * {@link ij.plugin.filter.PlugInFilter#run(ij.process.ImageProcessor)} can
     * only handle 2-dimensional data.
     *
     * If your plugin does not change the pixels in-place, make this method
     * return the results and change the
     * {@link #setup(java.lang.String, ij.ImagePlus)} method to return also the
     * <i>DOES_NOTHING</i> flag.
     *
     * @param image the image (possible multi-dimensional)
     */
    public void process(ImagePlus image) {
        // slice numbers start with 1 for historical reasons
        for (int i = 1; i <= image.getStackSize(); i++) {
            process(image.getStack().getProcessor(i));
        }
    }

    // Select processing method depending on image type
    public void process(ImageProcessor ip) {
        int type = image.getType();
        if (type == ImagePlus.GRAY8) {
            process((byte[]) ip.getPixels());
        } else if (type == ImagePlus.GRAY16) {
            process((short[]) ip.getPixels());
        } else if (type == ImagePlus.GRAY32) {
            process((float[]) ip.getPixels());
        } else if (type == ImagePlus.COLOR_RGB) {
            process((int[]) ip.getPixels());
        } else {
            throw new RuntimeException("not supported");
        }
    }

    // processing of GRAY8 images
    public void process(byte[] pixels) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // process each pixel of the line
                // example: add 'number' to each pixel
                pixels[x + y * width] += (byte) value;
            }
        }
    }

    // processing of GRAY16 images
    public void process(short[] pixels) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // process each pixel of the line
                // example: add 'number' to each pixel
                pixels[x + y * width] += (short) value;
            }
        }
    }

    // processing of GRAY32 images
    public void process(float[] pixels) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // process each pixel of the line
                // example: add 'number' to each pixel
                pixels[x + y * width] += (float) value;
            }
        }
    }

    // processing of COLOR_RGB images
    public void process(int[] pixels) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // process each pixel of the line
                // example: add 'number' to each pixel
                pixels[x + y * width] += (int) value;
            }
        }
    }

    public void showAbout() {
        IJ.showMessage("DiabeticRetinopathy",
                "a template for processing each pixel of an image"
        );
    }

    /**
     * Main method for debugging.
     *
     * For debugging, it is convenient to have a method that starts ImageJ,
     * loads an image and calls the plugin, e.g. after setting breakpoints.
     *
     * @param args unused
     */
    public static void main(String[] args) {

        // set the plugins.dir property to make the plugin appear in the Plugins menu
        Class<?> clazz = Diabetic_Retinopathy.class;
        String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
        String pluginsDir = url.substring(5, url.length() - clazz.getName().length() - 6);
        System.setProperty("plugins.dir", pluginsDir);

        // start ImageJ
        new ImageJ();

        /*
         double rollingBall = Prefs.get("EI_rollingBall.double", 100);
        
         String[] filters =  { "None","Median", "Sigma", "Smooth","Anisotropic Diffusion"};
         double gammaVal= Prefs.get("EI_gammaVal.double", 0.7);
         int filterID=(int)Prefs.get("EI_filterID.int", 0);
         double percSat = Prefs.get("EI_percSat.double", 0.1);
        
         GenericDialog gd = new GenericDialog("Image Enhance");
         gd.addNumericField("BG subtraction radius (zero for no adjustment)", rollingBall, 0);
         gd.addChoice("Filter to reduce noise", filters, filters[filterID]);
         gd.addNumericField("Percentage saturation (100 for no adjustment)", percSat, 2);
         gd.addNumericField("Gamma Value (zero for no adjustment)", gammaVal, 2);
         gd.showDialog();

         if (gd.wasCanceled()) {
         return;
         }
        
         // Get the info from the dialogue
         rollingBall= gd.getNextNumber();
         filterID= gd.getNextChoiceIndex();
         percSat= gd.getNextNumber();
         gammaVal= gd.getNextNumber();
         */
        // Identifica el nevio optico y obtener el perfil.
        // Un nervio optico sano deberia tener un perfil suave.
        //ImagePlus image = IJ.openImage("/home/idarraga/storage/Diabetic_Retinopathy_Detection/sample/16_left.jpeg");
        //image.show();
        // run the plugin
        IJ.runPlugIn(clazz.getName(), "");

        //new ij.ImageJ();
        //new Diabetic_Retinopathy().run("");
    }

}
