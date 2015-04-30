
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.PI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.rosuda.JRI.Rengine;

/**
 * DiabeticRetinopathy
 *
 * @author Alberto Ocampo, John Idarraga 2015
 */
public class Diabetic_Retinopathy implements PlugIn { //PlugInFilter {

    // List of ImageHelpers, one per image analyzed
    public List<ImageHelper> ImagesHelperList;
    // Blink run
    public boolean blindRun = false;
    
    
    public Diabetic_Retinopathy() {

        this.ImagesHelperList = new ArrayList<ImageHelper>();

    }

    @Override
    public void run(String arg) { // run(ImageProcessor ip) {

        System.out.println("Run ...\n");
        String datapath = "/home/idarraga/storage/Diabetic_Retinopathy_Detection/JonnyBeGood/";
        loadCSVFile("/home/idarraga/storage/Diabetic_Retinopathy_Detection/trainLabels.csv");
        int nImagesProcess = 20;
        boolean closeImageAfterProcessing = true;
        blindRun = false;
        
        //int setSize = ImagesHelperList.size();

        // prepare R plot
        double[] areaV = new double[nImagesProcess];
        double[] effectiveRadiusV = new double[nImagesProcess];
        double[] levV = new double[nImagesProcess];
        double[] intRV = new double[nImagesProcess];

        int cntr = 0;
        int imageItr = 0;
        while (imageItr < nImagesProcess) {

            ImageHelper oneImageHelper = ImagesHelperList.get(cntr);
            String fn = datapath + oneImageHelper.getFilenamePrefix() + ".jpeg";
            System.out.printf("-- trying : %s \n", fn);
            
            File f = new File(fn);
            if (f.exists() && !f.isDirectory()) {

                // Going to work on
                System.out.printf("-- %d -- ", imageItr );
                System.out.printf("%s | DRlevel = %d\n", fn, oneImageHelper.getDRLevel());

                // Get some image stats
                getImageStats(fn, oneImageHelper);
                
                // Blood ideantification
                // Run the background extractio algo and calculate the integrals
                double[] inVector = {0,0,0,0};
                if( ! getIntegralAboveBackground(inVector, fn, 100, 0.8, closeImageAfterProcessing) ) { 
                    cntr++;
                    continue;
                }
                
                // TODO
                // aqui falta normalizar al tama~no de la imagen para poder comparar integrales
                // !!!!!!!!!!!!!!!!!!!
                
                areaV[imageItr]             = oneImageHelper.getSizeX() * oneImageHelper.getSizeY();
                effectiveRadiusV[imageItr]  = inVector[3]; // The effective radious info is here
                levV[imageItr]              = oneImageHelper.getDRLevel();
                intRV[imageItr]             = inVector[0];
                imageItr++;
            }
            cntr++;
        }

        // The areas will have to be normalized cause all the pictures have different size
        // Find the biggest Image.
        int biggestImageId = -1;
        double biggestArea = 0;
        for(int i = 0 ; i < imageItr ; i++) {
            if( areaV[i] > biggestArea ) {
                biggestArea = areaV[i];
                biggestImageId = i;
            }
        }
        // Take the area of the biggest image to be 100.
        // Convert all areas to these units
        for(int i = 0 ; i < imageItr ; i++) {
            areaV[i] = areaV[i] * 100 / biggestArea;
            //intRV[i] = 
        }
        
        // On this units normalize the integrals to the biggest
        //for(int i = 0 ; i < imageItr ; i++) {
        //    intRV[i] = intRV[i] * (100/areaV[i]);
       // }
        
        // R
        String[] R_args = {"--no-save"};
        Rengine re = new Rengine(R_args, false, null);

        long levV3 = re.rniPutDoubleArray(levV);
        long intRV3 = re.rniPutDoubleArray(intRV);

        re.rniAssign("drlevel", levV3, 0);
        re.rniAssign("var", intRV3, 0);

        // Plot
        re.eval("plot(drlevel, var)");
        
    }

    
    public boolean getIntegralAboveBackground(double [] inVector, String fn, double backgSubsRolling, double kfactor, boolean closeImage) {

        // Open
        ImagePlus imageOr = IJ.openImage(fn);
        imageOr.show();
        
        // Dialogue
        GenericDialog gd = new GenericDialog("Parameters");
        // default value is 0.00, 2 digits right of the decimal point
        gd.addCheckbox("Enhance contrast", true);
        gd.addNumericField("Saturated [Enhance Contrast]", 0.4, 2);
        gd.addCheckbox("Equalize", true);
        gd.addCheckbox("Substract Background", false);
        if (!blindRun) { gd.showDialog(); }
        if (gd.wasCanceled()) {
            if ( closeImage ) {
                imageOr.changes = false;
                imageOr.close();
            }     
            return false;
        }
        // get entered values
        boolean enhanceContrast = gd.getNextBoolean();
        double saturated = gd.getNextNumber();
        boolean equalize = gd.getNextBoolean();
        boolean substractBackground = gd.getNextBoolean();
        
        
        ImagePlus image = imageOr.duplicate();
        image.show();

        
        // close the image used to make a selection
        //imageOr.close();
        
        // Trying to equalize the image by Enhance Contrast using Equalize histogram
        if( enhanceContrast ) {
            String runS = "saturated=";
            runS += String.format("%.1f", saturated);
            if ( equalize ) { runS += " equalize"; }
            IJ.run("Enhance Contrast...", runS );
        }
        // Perform background substraction
        if ( substractBackground ) {
            String bgSubtraction = "rolling=" + backgSubsRolling + "  stack";
            IJ.run("Subtract Background...", bgSubtraction);
        }

        // Give it a go
        // Dialogue
        GenericDialog gd2 = new GenericDialog("Continue ...");
        gd2.addMessage("Proceed ?");
        if (!blindRun) { gd2.showDialog(); }
        if (gd.wasCanceled()) {
            if ( closeImage ) {
                image.changes = false;
                image.close();
                imageOr.changes = false;
                imageOr.close();
            }     
            return false;
        }
        
        
        ImageProcessor ip = image.getProcessor();
        int[][] img_matrix = ip.getIntArray();
        int sizex = img_matrix.length;
        int sizey = img_matrix[0].length;
        int imageSize = sizex * sizey;

        // Before calculating the integrals I need to limit the measurement
        //  to a resonable radius.  Please find the radius of the image, i.e. the edge.
        double effectiveRadius = findEyeEffectiveRadius(ip, 20);
        System.out.printf("effectiveRadius = %f\n", effectiveRadius);
        
        // Calculate the average inside the selected radius
        double aR = 0, aG = 0, aB = 0;
        int pixelsConsidered = 0;
        for (int i = 0; i < sizex; i++) {
            for (int j = 0; j < sizey; j++) {

                // See that this coordinates fall inside the effctive radius.
                // This removes the halo and all the zeroes outside.
                // Also the tab in the right upper corner on some images.
                if ( CalcDistance2(sizex/2, sizey/2, i, j) > effectiveRadius*effectiveRadius ) continue; 
                
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

        System.out.println("sizex : " + sizex + ", sizey = " + sizey);
        System.out.printf("Average  R,G,B = %.1f, %.1f, %.1f\n", aR, aG, aB);

        // Integral over the average
        double inR = 0, inG = 0, inB = 0;
        for (int i = 0; i < sizex; i++) {
            for (int j = 0; j < sizey; j++) {
                int B = img_matrix[i][j] & 0xFF;
                int G = img_matrix[i][j] >> 8 & 0xFF;
                int R = img_matrix[i][j] >> 16 & 0xFF;

                if ( CalcDistance2(sizex/2, sizey/2, i, j) > effectiveRadius*effectiveRadius ) continue; 
                
                if ( i == 840 && j == 1284 ) {
                    System.err.printf("%d, %d, %d\n", R,G,B);
                }
                
                if (R != 0 && G != 0 && B != 0) {

                    if (R > aR && G < aG && B < aB) {
                        inR++; // += R;
                        // Mark then this pixel for the purpose of seeing what the algoright did
                        img_matrix[i][j] = 0x000000FF;

                    }
                    if (G < aG * kfactor) {
                        inG++; // += G;
                    }
                    if (B < aB * kfactor) {
                        inB++; // += B;
                    }
                    
                }
            }
        }

        // push the modified matrix in the processor
        ip.setIntArray(img_matrix);
        image.setProcessor(ip);
        
        // Dialogue
        GenericDialog gd3 = new GenericDialog("Done");
        gd3.addMessage("Done");
        if (!blindRun) { gd3.showDialog(); }
        
        // Close the image used to compare
        imageOr.changes = false;
        imageOr.close();

        
        // Let's express area as a fraction of the total area
        // use the radius
        inR = inR / pixelsConsidered; //( PI * effectiveRadius*effectiveRadius );
        inG = inG / pixelsConsidered; //( PI * effectiveRadius*effectiveRadius );
        inB = inB / pixelsConsidered; //( PI * effectiveRadius*effectiveRadius );
        
        System.out.printf("Integral R,G,B = %.5f, %.5f, %.5f\n", inR, inG, inB);

        inVector[0] = inR;
        inVector[1] = inG;
        inVector[2] = inB;
        inVector[3] = effectiveRadius;
        
        // get rid of the image
        if ( closeImage ) {
            image.changes = false;
            image.close();
        }        
        
        return true;
    }


    private boolean showDialog() {
        
        /*
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
*/
        
        return true;
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
       
        // run the plugin
        IJ.runPlugIn(clazz.getName(), "");

        //new ij.ImageJ();
        //new Diabetic_Retinopathy().run("");
    }

    private double findEyeEffectiveRadius(ImageProcessor ip, int zeroThreshold) {

        double rad = 0;
        int[][] img_matrix = ip.getIntArray();
        int sizex = img_matrix.length;
        int sizey = img_matrix[0].length;
        
        // Calculate the average inside the selected radius
        double aR = 0, aG = 0, aB = 0;
        int pixelsConsidered = 0;
        int zeroCntr = 0;
        int i = sizex/2; // start in the middle
        for ( ; i < sizex; i++) {
            // Count how many zeros have been found
            // if it finds a bump, rewind
            int B = img_matrix[i][sizey/2] & 0xFF;
            int G = img_matrix[i][sizey/2] >> 8 & 0xFF;
            int R = img_matrix[i][sizey/2] >> 16 & 0xFF;
            if ( R == 0 && G == 0 && B == 0 ) zeroCntr++;
            else zeroCntr = 0;
            
            if ( zeroCntr > zeroThreshold ) break; // stop here
        }

        // This will be the usable radius
        rad = (i - sizex/2) - zeroThreshold*3;
        
        return rad;
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

        System.out.printf("Number of files referenced in file %s = %d\n", csvFile, ImagesHelperList.size());

    }

    private double CalcDistance2(int xi, int yi, int xf, int yf) {

        return ( (xf - xi) * (xf - xi) ) + ( (yf - yi) * (yf - yi) );
    }


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

    private void getImageStats(String fn, ImageHelper oneImageHelper) {
        
        ImagePlus image = IJ.openImage(fn);
        ImageProcessor ip = image.getProcessor();

        // Fill the helper with the needed info
        oneImageHelper.setSizeY( ip.getHeight() );
        oneImageHelper.setSizeX( ip.getWidth() );
        
        image.close();
        
    }
    
}


